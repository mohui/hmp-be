package com.bjknrt.user.permission.centre.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.operation.logsdk.annotation.OperationLog
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.*
import com.bjknrt.user.permission.centre.api.UserApi
import com.bjknrt.user.permission.centre.dao.RoleUserPermissionDao
import com.bjknrt.user.permission.centre.dao.UserIdentityTagDao
import com.bjknrt.user.permission.centre.dao.UserRoleOrgRegionDao
import com.bjknrt.user.permission.centre.event.DeleteManagerEvent
import com.bjknrt.user.permission.centre.event.SaveUserEvent
import com.bjknrt.user.permission.centre.service.RoleService
import com.bjknrt.user.permission.centre.service.UserService
import com.bjknrt.user.permission.centre.vo.*
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.kato.common.exception.KatoAuthenticationException
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.`in`
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController("com.bjknrt.user.permission.centre.api.UserController")
class UserController(
    val appConfigProperties: AppConfigProperties,
    val userTable: UpcsUserTable,
    val userRoleTable: UpcsUserRoleTable,
    val userOrgTable: UpcsUserOrgTable,
    val userRegionTable: UpcsUserRegionTable,
    val userIdentityTagTable: UpcsUserIdentityTagTable,
    val userIdentityTagDao: UserIdentityTagDao,
    val userRoleOrgRegionDao: UserRoleOrgRegionDao,
    val roleUserPermissionDao: RoleUserPermissionDao,
    val userService: UserService,
    val roleService: RoleService,
    val eventPublisher: ApplicationEventPublisher,
    val objectMapper: ObjectMapper
) : AppBaseController(), UserApi {


    /**
     * 删除用户
     * @param body
     * @return Unit
     */
    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_DELETE_USER)
    @Transactional
    override fun del(body: Id) {
        TODO("接口未实现，目前无任何地方调用")
    }

    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_DELETE_USER)
    @Transactional
    override fun delManager(body: Id) {
        val userId = body.arg
        //查询用户信息
        val user = userTable.select()
            .where(UpcsUserTable.IsDel eq false.arg)
            .where(UpcsUserTable.IsEnabled eq true.arg)
            .where(UpcsUserTable.KnId eq userId)
            .findOne() ?: throw MsgException(AppSpringUtil.getMessage("user.no-find"))

        //查询此用户的身份标识信息
        val userIdentityTags = userIdentityTagTable.select()
            .where(UpcsUserIdentityTagTable.IsDel eq false.arg)
            .where(UpcsUserIdentityTagTable.KnUserId eq userId)
            .find()

        //判断是否包含患者身份标识
        val containsPatientIdentityTag =
            userIdentityTags.map { IdentityTag.valueOf(it.knIdentifyTag) }.contains(IdentityTag.PATIENT)

        if (containsPatientIdentityTag) {
            //过滤出需要删除的身份标识
            val delUserIdentityTagIds =
                userIdentityTags
                    .filter { IdentityTag.valueOf(it.knIdentifyTag) != IdentityTag.PATIENT }
                    .map { it.knId }

            userIdentityTagTable.update().setIsDel(true)
                .where(UpcsUserIdentityTagTable.KnId `in` delUserIdentityTagIds.map { it.arg })
                .execute()
        } else {
            userIdentityTagTable.update().setIsDel(true)
                .where(UpcsUserIdentityTagTable.KnUserId eq userId)
                .execute()

            userTable.update().setIsDel(true).where(UpcsUserTable.KnId eq userId).execute()
        }

        //删除其他关联关系
        userRoleTable.update().setIsDel(true).where(UpcsUserRoleTable.KnUserId eq userId).execute()
        userOrgTable.update().setIsDel(true).where(UpcsUserOrgTable.KnUserId eq userId).execute()
        userRegionTable.update().setIsDel(true).where(UpcsUserRegionTable.KnUserId eq userId).execute()


        //发布删除管理人员事件
        eventPublisher.publishEvent(DeleteManagerEvent(this, user, userIdentityTags))
    }


    override fun validOrgRegionPermission(validOrgRegionPermissionParam: ValidOrgRegionPermissionParam): ValidOrgRegionPermissionResult {
        return userService.validOrgRegionPermission(
            validOrgRegionPermissionParam.orgIdSet,
            validOrgRegionPermissionParam.regionIdSet
        )
    }

    override fun getUserList(id: List<BigInteger>): List<UserRoleOrg> {
        return userTable.select()
            .where(UpcsUserTable.IsDel eq false.arg)
            .where(UpcsUserTable.IsEnabled eq true.arg)
            .where(UpcsUserTable.KnId `in` id.map { it.arg })
            .find()
            .takeIf { it.isNotEmpty() }
            ?.let { userList ->
                val userIdList = userList.map { it.knId }

                val rolePermissionMap =
                    roleUserPermissionDao.listPermissionByUserIds(userIdList)
                        .groupBy({ it.roleId }, { PermissionEnum.valueOf(it.permissionCode) })

                val userOrgMap = userRoleOrgRegionDao.selectOrgByUserId(userIdList)
                    .groupBy({ it.knUserId }, { Org(it.knName, it.knRegionCode, it.knSort, it.knId) })

                val userRoleMap = userRoleOrgRegionDao.selectRoleByUserId(userIdList)
                    .groupBy(
                        { it.knUserId },
                        { Role(it.knId, it.knName, it.knCode, rolePermissionMap[it.knId] ?: emptyList()) })

                val userRegionMap = userRoleOrgRegionDao.selectRegionByUserId(userIdList)
                    .groupBy({ it.knUserId }, { Region(it.knCode, it.knName, it.knLevelCode, it.knParentCode) })

                val userIdentityTagMap = userIdentityTagDao.selectIdentityTagByUserId(userIdList)
                    .groupBy({ it.knUserId }, { IdentityTag.valueOf(it.knIdentifyTag) })

                userList.map { user ->
                    UserRoleOrg(
                        user.knId,
                        user.knName ?: "",
                        userRoleMap[user.knId] ?: emptyList(),
                        userOrgMap[user.knId] ?: emptyList(),
                        userRegionMap[user.knId] ?: emptyList(),
                        userIdentityTagMap[user.knId] ?: emptyList(),
                    )
                }
            } ?: emptyList()
    }

    /**
     * 根据id批量获取用户信息
     *
     *
     * @param id
     * @return List<ListByIdsParamInner>
     */
    override fun listByIds(id: List<Id>): List<ListByIdsParamInner> {
        if (id.isEmpty()) return listOf()
        return userTable.select()
            .where(UpcsUserTable.KnId `in` id.map { it.arg })
            .where(UpcsUserTable.IsDel eq false)
            .find().map {
                ListByIdsParamInner(
                    id = it.knId,
                    name = it.knName ?: "",
                    loginName = it.knLoginName,
                    createdAt = it.knCreatedAt,
                    updatedAt = it.knUpdatedAt,
                    phone = it.knPhone ?: "",
                    email = it.knEmail,
                    profilePic = it.knProfilePic,
                    birthday = it.knBirthday,
                    gender = Gender.valueOf(it.knGender),
                    idCard = it.knIdCard,
                    extends = it.knExtends,
                    createdBy = it.knCreatedBy,
                    updatedBy = it.knUpdatedBy,
                    address = it.knAddress
                )
            }
    }

    /**
     * 根据id批量获取用户简单信息
     *
     *
     * @param id
     * @return List<ListSimpleInfoByIdsParamInner>
     */
    override fun listSimpleInfoByIds(id: List<Id>): List<ListSimpleInfoByIdsParamInner> {
        if (id.isEmpty()) return listOf()
        return userTable.select()
            .where(UpcsUserTable.KnId `in` id.map { it.arg })
            .where(UpcsUserTable.IsDel eq false)
            .find().map {
                ListSimpleInfoByIdsParamInner(
                    id = it.knId,
                    name = it.knName ?: "",
                    loginName = it.knLoginName,
                    email = it.knEmail,
                    profilePic = it.knProfilePic,
                    birthday = it.knBirthday,
                    gender = it.knGender.let { g -> Gender.valueOf(g) } ?: Gender.MAN,
                    extends = it.knExtends,
                    address = it.knAddress
                )
            }
    }


    /**
     * 分页查询用户列表
     *
     *
     * @param page
     * @return PagedResult<Inner2>
     */
    override fun page(page: Page): PagedResult<UserInfo> {
        return userService.page(page)
    }

    override fun pageList(pageQueryUserParam: PageQueryUserParam): PagedResult<UserInfo> {
        return userService.page(
            Page(pageQueryUserParam.pageNo, pageQueryUserParam.pageSize),
            pageQueryUserParam.roleIds,
            pageQueryUserParam.identityTagList?.map { it.name }?.toSet(),
            pageQueryUserParam.orgIds,
            pageQueryUserParam.regionIds,
            pageQueryUserParam.name?.takeIf { it.isNotEmpty() },
            pageQueryUserParam.createdBy?.takeIf { it.isNotEmpty() },
            pageQueryUserParam.createdAtStart,
            pageQueryUserParam.createdAtEnd
        )
    }


    /**
     * 新增管理员
     *
     *
     * @param saveUserRoleOrgRequest
     * @return Unit
     */
    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_ADD_ADMINISTRATOR)
    @Transactional
    override fun saveUserRoleOrg(saveUserRoleOrgRequest: SaveUserRoleOrgRequest): Id {
        val currentId = AppSecurityUtil.currentUserIdWithDefault()
        val isNew = saveUserRoleOrgRequest.id == null

        // 保存用户
        val user = UpcsUser.builder()
            .setKnId(saveUserRoleOrgRequest.id ?: AppIdUtil.nextId())
            .setKnLoginName(saveUserRoleOrgRequest.loginName)
            .setKnLoginPassword(appConfigProperties.defaultPassword)
            .setKnCreatedBy(currentId)
            .setKnUpdatedBy(currentId)
            .setKnGender(saveUserRoleOrgRequest.gender.name)
            .setKnName(saveUserRoleOrgRequest.name)
            .setKnPhone(saveUserRoleOrgRequest.phone)
            .setKnAddress(saveUserRoleOrgRequest.address)
            .build()

        saveUserRoleOrgRequest.extendInfo?.let {
            user.knExtends = objectMapper.writeValueAsString(it)
        }

        val id = userService.save(user, isNew)

        //发布保存用户事件
        eventPublisher.publishEvent(SaveUserEvent(this, user, saveUserRoleOrgRequest.extendInfo))

        return id
    }

    /**
     * 获取当前用户信息
     *
     *
     * @return CurrentUser
     */
    override fun userCurrentPost(): UserRoleOrg {
        val current = AppSecurityUtil.jwtUser() ?: throw KatoAuthenticationException()

        val roleList = current.roleCodeSet.takeIf { it.isNotEmpty() }
            ?.let {
                val roleMap = roleService.listByCodes(it).associateBy { r -> r.knId }
                val permissionMap = roleService.listPermissionByRoleIds(roleMap.keys)
                    .associateBy { p -> p.roleId }
                roleMap.map { entry ->
                    val upcsRole = entry.value
                    permissionMap[entry.key].takeIf { p -> p != null }
                        ?.let { pc ->
                            Role(
                                upcsRole.knId,
                                upcsRole.knName,
                                upcsRole.knCode,
                                pc.permissionCollection.map { p ->
                                    PermissionEnum.valueOf(p.name)
                                }
                            )
                        } ?: Role(upcsRole.knId, upcsRole.knName, upcsRole.knCode, listOf())
                }
            } ?: listOf()

        val userIdList = listOf(current.userId)

        val orgList = userRoleOrgRegionDao.selectOrgByUserId(userIdList)
            .map { Org(it.knName, it.knRegionCode, it.knSort, it.knId) }

        val regionList = userRoleOrgRegionDao.selectRegionByUserId(userIdList)
            .map { Region(it.knCode, it.knName, it.knLevelCode, it.knParentCode) }

        val identityTagList = userIdentityTagDao.selectIdentityTagByUserId(userIdList)
            .map { IdentityTag.valueOf(it.knIdentifyTag) }

        return UserRoleOrg(
            current.userId,
            current.nickName,
            roleList,
            orgList,
            regionList,
            identityTagList
        )
    }
}
