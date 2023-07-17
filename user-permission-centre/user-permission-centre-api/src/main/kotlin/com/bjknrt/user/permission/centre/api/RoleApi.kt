package com.bjknrt.user.permission.centre.api

import com.bjknrt.framework.api.vo.Page
import com.bjknrt.user.permission.centre.vo.PageQueryRoleParam
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.user.permission.centre.vo.RoleDetailParam
import com.bjknrt.user.permission.centre.vo.RoleDetailResult
import com.bjknrt.user.permission.centre.vo.SaveRolePermissionParam
import com.bjknrt.user.permission.centre.vo.UpdUsedParam
import com.bjknrt.user.permission.centre.vo.RoleInfo
import com.bjknrt.user.permission.centre.vo.RoleRowInner
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid

@KatoClient(name = "\${app.hmp-user-permission-centre.kato-server-name:\${spring.application.name}}", contextId = "RoleApi")
@Validated
interface RoleApi {


    /**
     * 删除角色
     * 
     *
     * @param body
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/role/del"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun del(@Valid body: java.math.BigInteger): Unit


    /**
     * 分页查询角色列表
     * 
     *
     * @param pageQueryRoleParam
     * @return PagedResult<RoleRowInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/role/page"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun page(@Valid pageQueryRoleParam: PageQueryRoleParam): PagedResult<RoleRowInner>


    /**
     * 根据角色id查询角色详情
     * 
     *
     * @param roleDetailParam
     * @return RoleDetailResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/role/roleDetail"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun roleDetail(@Valid roleDetailParam: RoleDetailParam): RoleDetailResult


    /**
     * 分页查询角色列表(只有基本信息)
     * 
     *
     * @param page
     * @return PagedResult<RoleInfo>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/role/rolePage"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun rolePage(@Valid page: Page): PagedResult<RoleInfo>


    /**
     * 保存角色权限
     * 
     *
     * @param saveRolePermissionParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/role/saveRolePermission"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun saveRolePermission(@Valid saveRolePermissionParam: SaveRolePermissionParam): Unit


    /**
     * 启用禁用角色
     * 
     *
     * @param updUsedParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/role/updUsed"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updUsed(@Valid updUsedParam: UpdUsedParam): Unit
}
