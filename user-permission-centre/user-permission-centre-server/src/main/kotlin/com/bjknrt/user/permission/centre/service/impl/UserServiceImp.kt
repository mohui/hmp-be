package com.bjknrt.user.permission.centre.service.impl

import cn.hutool.core.date.DateUtil
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.security.JwtAuthenticationConverter
import com.bjknrt.security.JwtUserDetails
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.*
import com.bjknrt.user.permission.centre.dao.RoleUserPermissionDao
import com.bjknrt.user.permission.centre.dao.UserIdentityTagDao
import com.bjknrt.user.permission.centre.dao.UserRoleOrgRegionDao
import com.bjknrt.user.permission.centre.service.UserService
import com.bjknrt.user.permission.centre.vo.*
import me.danwi.kato.common.exception.KatoCommonException
import me.danwi.sqlex.core.query.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import javax.servlet.http.HttpServletResponse

@Service
class UserServiceImp(
    val appConfigProperties: AppConfigProperties,
    val userTable: UpcsUserTable,
    val userIdentityTagTable: UpcsUserIdentityTagTable,
    val userIdentityTagDao: UserIdentityTagDao,
    val userRoleOrgRegionDao: UserRoleOrgRegionDao,
    val roleUserPermissionDao: RoleUserPermissionDao,
    val passwordEncoder: PasswordEncoder,
    val jwtAuthenticationConverter: JwtAuthenticationConverter,
) : UserService {

    @Transactional
    override fun save(user: UpcsUser, isNew: Boolean): Id {
        // 登陆名判重
        val count = userTable.select().filter {
            val expression = (UpcsUserTable.KnLoginName eq user.knLoginName) and (UpcsUserTable.IsDel eq false)
            if (isNew) expression else expression and (UpcsUserTable.KnId ne user.knId.arg)
        }.count()
        if (count > 0) throw MsgException(AppSpringUtil.getMessage("user.register.name.repetition"))

        // 手机号判重
        user.knPhone
            .takeIf { item -> item.isNotEmpty() }
            ?.let {
                val phoneCount = userTable.select().filter {
                    val expression = (UpcsUserTable.KnPhone eq it) and (UpcsUserTable.IsDel eq false)
                    if (isNew) expression else expression and (UpcsUserTable.KnId ne user.knId.arg)
                }.count()
                if (phoneCount > 0) throw MsgException(AppSpringUtil.getMessage("user.register.phone.repetition"))
            }
        //处理密码
        if (isNew || user.knLoginPassword.isNotEmpty()) {
            // 加密密码
            user.knLoginPassword = passwordEncoder.encode(user.knLoginPassword)
        } else {
            //更新用户接口Id一定不为null
            user.knLoginPassword = userTable.findByKnId(user.knId)!!.knLoginPassword
        }

        // 保存
        userTable.saveOrUpdate(user)

        return user.knId
    }

    @Transactional
    override fun deleteUser(username: String) {
        userTable.update()
            .setIsDel(true)
            .where(UpcsUserTable.KnLoginName eq username.arg)
            .execute()
    }

    override fun verifyPwd(pwd: String): Boolean {
        return AppSecurityUtil.jwtUser()?.let {
            val loadUserByUsername = loadUserByUsername(it.loginName)
            loadUserByUsername?.let { passwordEncoder.matches(pwd, loadUserByUsername.loginPwd) }
        } ?: false
    }

    @Transactional
    override fun changePassword(userId: Id, newPassword: String) {
        userTable.findByKnId(userId)?.let {
            it.knLoginPassword = newPassword
            save(it, false)
        } ?: throw MsgException(AppSpringUtil.getMessage("user.no-find"))
    }

    override fun userExists(username: String?): Boolean {
        return username?.let {
            userTable.select()
                .where(UpcsUserTable.IsDel eq false)
                .where(UpcsUserTable.KnLoginName eq it.arg)
                .count() > 0
        } ?: false
    }

    override fun loadUserByUsername(username: String?): JwtUserDetails? {
        return username?.let {
            userTable.select()
                .where(UpcsUserTable.IsDel eq false)
                .where(UpcsUserTable.KnLoginName eq it)
                .findOne()
                ?.let { u ->
                    val userId = listOf(u.knId)
                    // 查找用户角色关系
                    val userRoleCodeSet = userRoleOrgRegionDao.selectRoleByUserId(userId)
                        .map { role -> role.knCode }.toSet()
                    // 查找用户组织关系
                    val userOrgIdSet = userRoleOrgRegionDao.selectOrgByUserId(userId)
                        .map { org -> org.knId }.toSet()

                    //组装JwtUser
                    userToJwtUser(u)
                }
        }
    }

    private fun userToJwtUser(u: UpcsUser): JwtUserDetails {
        val now = DateUtil.date().toJdkDate()
        return JwtUserDetails(
            id = AppIdUtil.nextId(),
            issuedAt = now,
            notBefore = now,
            expiration = now,
            userId = u.knId,
            nickName = u.knName ?: "",
            enabled = u.isEnabled,
            loginName = u.knLoginName
        ).apply {
            loginPwd = u.knLoginPassword
        }
    }

    override fun login(loginName: String, loginPwd: String, response: HttpServletResponse?): String {
        val jwtUserDetails = loadUserByUsername(loginName)
        return jwtUserDetails?.let {
            if (passwordEncoder.matches(loginPwd, it.loginPwd)) {
                jwtAuthenticationConverter.renew(it, response)
            } else {
                null
            }
        } ?: throw MsgException(AppSpringUtil.getMessage("user.login.error"))
    }

    override fun logout(response: HttpServletResponse?) {
        AppSecurityUtil.jwtUser()?.let { jwtAuthenticationConverter.renew(it, response, true) }
    }

    @Transactional
    override fun reset(id: Id) {
        changePassword(id, appConfigProperties.defaultPassword)
    }

    @Transactional
    override fun loginNoVerify(phone: String, name: String, identityTags: Set<String>): LoginNoVerifyResponse {
        var loginName: String = phone
        var user = userTable.select()
            .where(UpcsUserTable.IsDel eq false)
            .where(UpcsUserTable.KnPhone eq phone)
            .findOne()

        val userId: BigInteger = user?.knId ?: AppIdUtil.nextId()

        if (user == null) {
            // 查询登陆名是否和手机号同步的账号
            val users = userTable.select()
                .where(UpcsUserTable.IsDel eq false)
                .where(UpcsUserTable.KnLoginName like "${loginName}%")
                .find()
            // 如果存在相似登陆名，则排除相似登陆名计算新的登陆名进行保存
            if (users.size > 0) {
                val names = users.map { it.knLoginName }.toList()
                var count = 0
                while (names.contains(loginName) && count < 10) {
                    loginName += "R" // 由于手机号和账号重复情况不会特别多，这里直接写死
                    count++
                }
            }
            val currentId = AppSecurityUtil.currentUserIdWithDefault()

            // 新建用户
            save(
                user = UpcsUser.builder()
                    .setKnId(userId)
                    .setKnLoginName(loginName)
                    .setKnLoginPassword(appConfigProperties.defaultPassword)
                    .setKnName(name)
                    .setKnPhone(phone)
                    .setKnCreatedBy(currentId)
                    .setKnUpdatedBy(currentId)
                    .build(),
                isNew = true
            )

            identityTags.takeIf { it.isNotEmpty() }?.forEach { identityTag ->
                userIdentityTagTable.insertWithoutNull(
                    UpcsUserIdentityTag.builder()
                        .setKnId(AppIdUtil.nextId())
                        .setKnUserId(userId)
                        .setKnIdentifyTag(identityTag)
                        .setKnCreatedBy(currentId)
                        .setKnUpdatedBy(currentId)
                        .build()
                )
            } ?: throw MsgException(AppSpringUtil.getMessage("identity-tag.is-empty"))


        }

        // 查询最新数据
        user = userTable.findByKnId(userId)
            ?: throw KatoCommonException(AppSpringUtil.getMessage("user.loginNoVerify.save.error"))

        // 返回数据
        return LoginNoVerifyResponse(
            user.knId,
            jwtAuthenticationConverter.renew(userToJwtUser(user))
        )
    }

    override fun page(
        page: Page,
        roleIds: Set<BigInteger>?,
        identityTags: Set<String>?,
        orgIds: Set<BigInteger>?,
        regionIds: Set<BigInteger>?,
        name: String?,
        createdBy: String?,
        createdAtStart: LocalDate?,
        createdAtEnd: LocalDate?
    ): PagedResult<UserInfo> {


        val orgRegionCheck = validOrgRegionPermission(orgIds?.toList(), regionIds?.toList())
        // 如果isRun是false,直接返回
        if (!orgRegionCheck.isRun) return PagedResult.emptyPaged(page)

        val regionIdList: List<BigInteger>? = orgRegionCheck.regionIdSet
        val orgIdList: List<BigInteger>? = orgRegionCheck.orgIdSet

        val userPage = roleUserPermissionDao.pageUserByRegionIdNotNull(
            roleIds?.toList(),
            identityTags?.toList(),
            orgIdList,
            regionIdList,
            name,
            createdBy,
            createdAtStart,
            createdAtEnd,
            page.pageSize,
            page.pageNo
        )
        return transformUserInfo(page, userPage.total, userPage.totalPage, userPage.data.map {
            PageUserInfo(
                it.knId,
                it.knName,
                it.knLoginName,
                it.knPhone,
                it.knEmail,
                it.knProfilePic,
                it.knBirthday,
                it.knGender,
                it.knIdCard,
                it.knExtends,
                it.createdName,
                it.updatedName,
                it.knCreatedBy,
                it.knCreatedAt,
                it.knUpdatedBy,
                it.knUpdatedAt,
                it.knAddress
            )
        })
    }

    override fun validOrgRegionPermission(
        orgIds: List<BigInteger>?,
        regionIds: List<BigInteger>?
    ): ValidOrgRegionPermissionResult {
        var regionIdList: List<BigInteger>? = null
        var orgIdList: List<BigInteger>? = null

        // 是否执行,默认是执行,当不是超级管理员,且行政区域和机构都为空的时候不执行
        var isRun = true

        AppSecurityUtil.executeWithLogin { user ->
            // 判断如果是超级管理员, 不对参数做校验,直接返回
            if (user.isSuperAdmin()) {
                orgIdList = orgIds?.takeIf { it.isNotEmpty() }?.toList()
                regionIdList = regionIds?.takeIf { it.isNotEmpty() }?.toList()
                return@executeWithLogin user
            }
            // 参数是否都为null/empty, 走到这, 就不可能是超级管理员了
            if (regionIds.isNullOrEmpty() && orgIds.isNullOrEmpty()) {
                // 如果参数都为空, 取用户权限的regionId 和 orgId
                regionIdList = user.regionIdSet.takeIf { it.isNotEmpty() }?.toList()
                orgIdList = user.orgIdSet.takeIf { it.isNotEmpty() }?.toList()
            } else {
                // 用户的地区权限
                val userRegionList = user.regionIdSet.takeIf { it.isNotEmpty() }?.toList()
                // 用户的机构权限
                val userOrgList = user.orgIdSet.takeIf { it.isNotEmpty() }?.toList()

                // 取参数和用户权限交集
                regionIdList = regionIds?.takeIf { it.isNotEmpty() }?.let { regionIt ->
                    val regionParamList = regionIt.toList()

                    /**
                     * regionIds(地区参数) 和 userRegionIds(用户地区权限)取交集
                     * 1. userRegionIds(用户地区权限)为null或空, 直接返回null
                     * 2. regionIds (参数地区)为null或空,  直接返回null
                     */
                    if (regionParamList.isEmpty() || userRegionList.isNullOrEmpty())
                        return@let null
                    else {
                        // 3. 递归查询 过滤出用户权限下的地区
                        val regionMergeModels =
                            roleUserPermissionDao.paramRegionFilteruserRegion(userRegionList, regionParamList)
                        return@let regionMergeModels.map { it.knCode }
                    }
                }
                orgIdList = orgIds?.takeIf { it.isNotEmpty() }?.let { paramOrgIdSet ->
                    /**
                     * userRegionIds, userOrgId, orgIds
                     * userRegionIds(用户地区权限)和 userOrgId(用户机构权限)都为null或空,返回null
                     * orgIds(参数机构ID)为空或null, 返回null
                     * 用户权限（userRegionIds本级及下级所有orgID和userOrgId的交集取合集），orgIds(参数机构code)取交集
                     */
                    if ((userOrgList.isNullOrEmpty() && userRegionList.isNullOrEmpty()) || paramOrgIdSet.isEmpty()) {
                        return@let null
                    } else {
                        // select id from org where (id in ( 行政区域递归 关联 机构表 ) or id in (userOrgId)) and id in (orgIds)
                        val orgIdMergeModels = roleUserPermissionDao.paramOrgFilterUserRegion(
                            userRegionList,
                            userOrgList,
                            paramOrgIdSet.toList()
                        )
                        return@let orgIdMergeModels.map { it.knId }
                    }
                }
            }

            //机构、区域都为空，不查询数据
            if (orgIdList.isNullOrEmpty() && regionIdList.isNullOrEmpty()) {
                isRun = false
                return@executeWithLogin null
            }

            return@executeWithLogin user
        }
        return ValidOrgRegionPermissionResult(
            isRun = isRun,
            regionIdSet = regionIdList,
            orgIdSet = orgIdList
        )
    }

    private fun transformUserInfo(
        page: Page,
        total: Long,
        totalPage: Long,
        data: List<PageUserInfo>
    ): PagedResult<UserInfo> {
        if (total == 0L) {
            return PagedResult.emptyPaged(page)
        }

        val userIdList = data.map { it.id }
        val userOrgMap = userRoleOrgRegionDao.selectOrgByUserId(userIdList).groupBy({ it.knUserId }, { it })
        val userRoleMap = userRoleOrgRegionDao.selectRoleByUserId(userIdList).groupBy({ it.knUserId }, { it })
        val userRegionMap = userRoleOrgRegionDao.selectRegionByUserId(userIdList).groupBy({ it.knUserId }, { it })

        val userIdentityTagMap = userIdentityTagDao.selectIdentityTagByUserId(userIdList)
            .groupBy({ it.knUserId }, { IdentityTag.valueOf(it.knIdentifyTag) })


        val list = data.map {
            val id = it.id
            UserInfo(
                id = it.id,
                name = it.name ?: "",
                loginName = it.loginName,
                phone = it.phone,
                email = it.email,
                profilePic = it.profilePic,
                birthday = it.birthday,
                gender = Gender.valueOf(it.gender),
                idCard = it.idCard,
                extends = it.extends,
                createdName = it.createdName,
                updatedName = it.updatedName,
                createdBy = it.createdBy,
                createdAt = it.createdAt,
                updatedBy = it.updatedBy,
                updatedAt = it.updatedAt,
                address = it.address,
                regionList = userRegionMap[id]?.map { userRegion ->
                    Region(userRegion.knCode, userRegion.knName, userRegion.knLevelCode, userRegion.knParentCode)
                },
                orgList = userOrgMap[id]?.map { userOrg ->
                    Org(userOrg.knName, userOrg.knRegionCode, userOrg.knSort, userOrg.knId)
                },
                roleList = userRoleMap[id]?.map { userRole ->
                    RoleInfo(userRole.knId, userRole.knName, userRole.knCode)
                },
                identityTagList = userIdentityTagMap[id] ?: emptyList()
            )
        }
        return PagedResult(totalPage, page.pageSize, page.pageNo, total, list)
    }
}

data class PageUserInfo(
    val id: Id,
    val name: String?,
    val loginName: String,
    val phone: String,
    val email: String?,
    val profilePic: String?,
    val birthday: LocalDateTime?,
    val gender: String,
    val idCard: String?,
    val extends: String?,
    val createdName: String?,
    val updatedName: String?,
    val createdBy: BigInteger?,
    val createdAt: LocalDateTime,
    val updatedBy: BigInteger?,
    val updatedAt: LocalDateTime,
    val address: String?
)
