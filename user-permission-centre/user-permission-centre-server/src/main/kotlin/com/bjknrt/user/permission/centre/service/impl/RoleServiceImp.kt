package com.bjknrt.user.permission.centre.service.impl

import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.operation.logsdk.annotation.OperationLog
import com.bjknrt.security.SUPER_ADMIN_ROLE_CODE
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.*
import com.bjknrt.user.permission.centre.dao.RoleUserPermissionDao
import com.bjknrt.user.permission.centre.entity.RoleEntity
import com.bjknrt.user.permission.centre.service.RoleService
import com.bjknrt.user.permission.centre.vo.*
import me.danwi.kato.common.exception.KatoException
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.`in`
import me.danwi.sqlex.core.query.ne
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleServiceImp(
    val roleTable: UpcsRoleTable,
    val userRoleTable: UpcsUserRoleTable,
    val rolePermissionTable: UpcsRolePermissionTable,
    val roleUserPermissionDao: RoleUserPermissionDao
) : RoleService {

    override fun listByCodes(roleSet: Collection<String>): List<UpcsRole> {
        return roleTable.select()
            .where(UpcsRoleTable.IsDel eq false)
            .where(UpcsRoleTable.KnCode `in` roleSet.map { i -> i.arg })
            .find()
    }

    override fun listPermissionByRoleIds(roleIds: Collection<Id>): List<RoleService.RolePermission> {
        return roleIds.takeIf { it.isNotEmpty() }?.let { rids ->
            roleUserPermissionDao.listPermissionByRoleIds(rids.toList())
                .groupBy { it.roleId }
                .map {
                    RoleService.RolePermission(
                        it.key,
                        it.value.map { p -> PermissionEnum.valueOf(p.permissionCode) }
                    )
                }
        } ?: listOf()
    }

    @Transactional
    override fun saveRolePermission(saveRolePermissionParam: SaveRolePermissionParam) {
        // 角色菜单权限
        val permissions = saveRolePermissionParam.permissions
        // 判断是添加还是修改
        val isNew = saveRolePermissionParam.id == null
        val roleCode = if (isNew) "R${AppIdUtil.nextId()}" else null

        // 如果id有值: 是修改, 需要判断是否是超级管理员; 如果没有值: 不需要做判断
        val isAdmin = saveRolePermissionParam.id?.let {
            prefabricatedRoleList().find { findIt -> findIt == it } != null
        } ?: false
        if (isAdmin) throw KatoException(AppSpringUtil.getMessage("role.save.admin"))

        val currentLoginId: Id = AppSecurityUtil.currentUserIdWithDefault()
        val id = saveRolePermissionParam.id ?: AppIdUtil.nextId()
        // 保存角色
        val role = UpcsRole.builder()
            .setKnId(id)
            .setKnCreatedBy(currentLoginId)
            .setKnUpdatedBy(currentLoginId)
            .setKnName(saveRolePermissionParam.name)
            .build()
        roleCode?.let { role.knCode = it }
        roleTable.upsertWithoutNull(role)
        if (!isNew) {
            // 查询原有权限关系，删除
            delPermission(id)
        }
        // 新增权限
        permissions.forEach {
            rolePermissionTable.insertWithoutNull(
                UpcsRolePermission.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnRoleId(id)
                    .setKnPermissionCode(it.name)
                    .setKnCreatedBy(currentLoginId)
                    .setKnUpdatedBy(currentLoginId)
                    .build()
            )
        }
    }

    /**
     * 删除角色
     *
     * 1. 超级管理员角色, 不允许删除
     * 2. 角色下有人员，不允许删除
     *
     *
     * @param body
     * @return Unit
     */
    override fun del(body: Id) {
        // 获取不可删除角色列表
        val pFind = prefabricatedRoleList().find { it == body }
        // 1.判断是否是超级管理员角色(不可删除的角色)
        if (pFind != null) throw KatoException(AppSpringUtil.getMessage("role.del.admin-role"))

        // 2. 判断 角色下有人员, 不能删除
        if (
            userRoleTable.select()
                .where(UpcsUserRoleTable.IsDel eq false)
                .where(UpcsUserRoleTable.KnRoleId eq body)
                .count() > 0
        ) throw MsgException(AppSpringUtil.getMessage("role.del.role"))

        // 删除角色表
        val row = roleTable.update().setIsDel(true).where(UpcsRoleTable.KnId eq body).execute()
        if (row > 0) {
            // 删除权限及关联关系
            delPermission(body)
        }
    }

    override fun updUsed(updUsedParam: UpdUsedParam) {
        val id = updUsedParam.id
        val status = updUsedParam.isUsed

        // 判断 角色下有人员
        if (
            userRoleTable.select()
                .where(UpcsUserRoleTable.IsDel eq false)
                .where(UpcsUserRoleTable.KnRoleId eq id)
                .count() > 0
        ) throw MsgException(AppSpringUtil.getMessage("role.used.role"))

        roleTable
            .update()
            .setIsUsed(status)
            .where(UpcsRoleTable.KnId eq id)
            .execute()
    }

    /**
     * 分页查询角色列表
     *
     *
     * @param page
     * @return PagedResult<Inner1>
     */
    override fun page(pageQueryRoleParam: PageQueryRoleParam): PagedResult<RoleRowInner> {
        val name = pageQueryRoleParam.name?.let { "%${pageQueryRoleParam.name}%" }
        val createdName = pageQueryRoleParam.createdName?.let { "%${pageQueryRoleParam.createdName}%" }
        return PagedResult.fromDbPaged(
            roleUserPermissionDao.selectRoleWithUserPermissionNum(
                name,
                createdName,
                pageQueryRoleParam.status,
                pageQueryRoleParam.createdStartAt,
                pageQueryRoleParam.createdEndAt,
                pageQueryRoleParam.pageSize,
                pageQueryRoleParam.pageNo
            )
        ) {
            RoleRowInner(
                id = it.id,
                name = it.name,
                code = it.code,
                permissionNum = it.permissionNum.toInt(),
                userNum = it.userNum.toInt(),
                createdAt = it.createdAt,
                createdName = it.createdName,
                isUsed = it.isUsed
            )
        }
    }

    // 角色列表
    override fun rolePage(page: Page): PagedResult<RoleInfo> {
        return PagedResult.fromDbPaged(
            roleTable.select()
                .where(UpcsRoleTable.IsDel eq false)
                .where(UpcsRoleTable.KnCode ne SUPER_ADMIN_ROLE_CODE)
                .page(page.pageSize, page.pageNo)
        ) {
            RoleInfo(id = it.knId, name = it.knName, code = it.knCode)
        }
    }

    /**
     * 根据角色id获取详情
     */
    override fun roleDetail(roleId: Id): RoleEntity {
        return roleTable.select()
            .where(UpcsRoleTable.KnId eq roleId)
            .findOne()?.let {
                RoleEntity(id = it.knId, name = it.knName, code = it.knCode, isUsed = it.isUsed)
            } ?: throw NotFoundDataException(AppSpringUtil.getMessage("role.detail.no-found"))
    }

    /**
     * 获取不能删除的角色列表
     */
    override fun prefabricatedRoleList(): List<Id> {
        val roleCodes = listOf(SUPER_ADMIN_ROLE_CODE)

        return roleTable.select()
            .where(UpcsRoleTable.IsDel eq false)
            .where(UpcsRoleTable.KnCode `in` roleCodes.map { i -> i.arg })
            .find()
            .map {
                it.knId
            }
    }

    /**
     * 删除角色权限关系
     */
    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_DELETE_ROLE_PERMISSION)
    private fun delPermission(roleId: Id) {
        // 根据角色id删除原有权限关系
        rolePermissionTable
            .update()
            .setIsDel(true)
            .where(UpcsRolePermissionTable.KnRoleId eq roleId)
            .execute()
    }
}