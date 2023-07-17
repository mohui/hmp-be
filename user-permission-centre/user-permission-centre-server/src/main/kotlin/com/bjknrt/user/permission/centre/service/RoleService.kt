package com.bjknrt.user.permission.centre.service

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.user.permission.centre.UpcsRole
import com.bjknrt.user.permission.centre.entity.RoleEntity
import com.bjknrt.user.permission.centre.vo.*
import java.math.BigInteger

interface RoleService {

    class RolePermission(
        val roleId: BigInteger,
        val permissionCollection: Collection<PermissionEnum> = listOf()
    )

    fun listByCodes(roleSet: Collection<String>): List<UpcsRole>

    fun listPermissionByRoleIds(roleIds: Collection<Id>): List<RolePermission>

    // 添加角色
    fun saveRolePermission(saveRolePermissionParam: SaveRolePermissionParam)

    // 删除角色
    fun del(body: Id)

    // 启用, 禁用
    fun updUsed(updUsedParam: UpdUsedParam)

    fun page(pageQueryRoleParam: PageQueryRoleParam): PagedResult<RoleRowInner>

    // 角色列表
    fun rolePage(page: Page): PagedResult<RoleInfo>

    fun roleDetail(roleId: Id): RoleEntity

    // 获取预制角色列表
    fun prefabricatedRoleList(): List<Id>
}