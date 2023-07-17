package com.bjknrt.user.permission.centre.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.operation.logsdk.annotation.OperationLog
import com.bjknrt.user.permission.centre.*
import com.bjknrt.user.permission.centre.api.RoleApi
import com.bjknrt.user.permission.centre.service.PermissionService
import com.bjknrt.user.permission.centre.service.RoleService
import com.bjknrt.user.permission.centre.vo.*
import me.danwi.sqlex.core.query.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.user.permission.centre.api.RoleController")
class RoleController(
    val roleService: RoleService,
    val permissionService: PermissionService
) : AppBaseController(), RoleApi {

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
    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_DELETE_ROLE)
    @Transactional
    override fun del(body: Id) {
        roleService.del(body)
    }

    /**
     * 分页查询角色列表
     *
     *
     * @param page
     * @return PagedResult<Inner1>
     */
    override fun page(pageQueryRoleParam: PageQueryRoleParam): PagedResult<RoleRowInner> {
        return roleService.page(pageQueryRoleParam)
    }

    override fun roleDetail(roleDetailParam: RoleDetailParam): RoleDetailResult {
        val roleDetail = roleService.roleDetail(roleDetailParam.id)
        val menuPermission  = permissionService.roleIdGetMenuPermission(roleDetailParam.id)
        return RoleDetailResult(
            id = roleDetail.id,
            name = roleDetail.name,
            code = roleDetail.code,
            isUsed = roleDetail.isUsed,
            permission = menuPermission
        )

    }

    override fun rolePage(page: Page): PagedResult<RoleInfo> {
        return roleService.rolePage(page)
    }

    /**
     * 保存角色权限
     *
     *
     * @param saveRolePermissionParam
     * @return Unit
     */
    @OperationLog(module = LogModule.UPS, action = LogAction.UPS_SAVE_ROLE_PERMISSION)
    @Transactional
    override fun saveRolePermission(saveRolePermissionParam: SaveRolePermissionParam) {
        roleService.saveRolePermission(saveRolePermissionParam)
    }

    override fun updUsed(updUsedParam: UpdUsedParam) {
        roleService.updUsed(updUsedParam)
    }
}
