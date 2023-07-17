package com.bjknrt.user.permission.centre.api

import com.bjknrt.user.permission.centre.vo.PermissionEnum
import com.bjknrt.user.permission.centre.vo.RoleIdGetMenuPermissionParam
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid

import kotlin.collections.List

@KatoClient(name = "\${app.hmp-user-permission-centre.kato-server-name:\${spring.application.name}}", contextId = "PermissionApi")
@Validated
interface PermissionApi {


    /**
     * 查询当前用户菜单权限列表
     * 
     *
     * @return List<PermissionEnum>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/permission/listMenuPermission"],
        produces = ["application/json"]
    )
    fun listMenuPermission(): List<PermissionEnum>


    /**
     * 根据角色id查询菜单权限
     * 
     *
     * @param roleIdGetMenuPermissionParam
     * @return List<PermissionEnum>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/permission/roleIdGetMenuPermission"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun roleIdGetlMenuPermission(@Valid roleIdGetMenuPermissionParam: RoleIdGetMenuPermissionParam): List<PermissionEnum>
}
