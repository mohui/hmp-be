package com.bjknrt.user.permission.centre.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.user.permission.centre.UpcsUserRoleTable
import com.bjknrt.user.permission.centre.api.PermissionApi
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.service.PermissionService
import com.bjknrt.user.permission.centre.service.RoleService
import com.bjknrt.user.permission.centre.vo.*
import me.danwi.sqlex.core.query.eq
import org.springframework.web.bind.annotation.RestController


@RestController("com.bjknrt.user.permission.centre.api.PermissionController")
class PermissionController(
    val userRoleTable: UpcsUserRoleTable,
    val roleService: RoleService,
    val permissionService: PermissionService
) : AppBaseController(), PermissionApi {

    override fun listMenuPermission(): List<PermissionEnum> {
        return AppSecurityUtil.currentUserId()?.let { userId ->
            userRoleTable.select()
                .where(UpcsUserRoleTable.IsDel eq false)
                .where(UpcsUserRoleTable.KnUserId eq userId)
                .find() // 查询角色Id
                .mapNotNull { ur -> ur.knRoleId }
                .takeIf { it.isNotEmpty() }
                ?.let { roleIds ->
                    roleService.listPermissionByRoleIds(roleIds)
                        .flatMap { it.permissionCollection.map { p -> PermissionEnum.valueOf(p.name) } }
                }
        } ?: listOf()
    }

    override fun roleIdGetlMenuPermission(roleIdGetMenuPermissionParam: RoleIdGetMenuPermissionParam): List<PermissionEnum> {
        return permissionService.roleIdGetMenuPermission(roleIdGetMenuPermissionParam.id)
    }
}
