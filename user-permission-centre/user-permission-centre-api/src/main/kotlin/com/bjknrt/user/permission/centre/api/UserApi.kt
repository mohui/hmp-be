package com.bjknrt.user.permission.centre.api

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.user.permission.centre.vo.ListByIdsParamInner
import com.bjknrt.user.permission.centre.vo.ListSimpleInfoByIdsParamInner
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.user.permission.centre.vo.PageQueryUserParam
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.user.permission.centre.vo.SaveUserRoleOrgRequest
import com.bjknrt.user.permission.centre.vo.UserRoleOrg
import com.bjknrt.user.permission.centre.vo.ValidOrgRegionPermissionParam
import com.bjknrt.user.permission.centre.vo.ValidOrgRegionPermissionResult
import com.bjknrt.user.permission.centre.vo.UserInfo
import me.danwi.kato.client.KatoClient
import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid

import kotlin.collections.List

@KatoClient(name = "\${app.hmp-user-permission-centre.kato-server-name:\${spring.application.name}}", contextId = "UserApi")
@Validated
interface UserApi {


    /**
     * 删除用户
     * 
     *
     * @param body
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/del"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun del(@Valid body: java.math.BigInteger): Unit


    /**
     * 删除用户
     *
     *
     * @param body
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/delManager"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun delManager(@Valid body: java.math.BigInteger): Unit


    /**
     * 获取用户信息
     * 
     *
     * @param id
     * @return List<UserRoleOrg>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getUserList(@Valid id: kotlin.collections.List<Id>): List<UserRoleOrg>


    /**
     * 根据id批量获取用户信息
     * 
     *
     * @param id
     * @return List<ListByIdsParamInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/listByIds"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun listByIds(@Valid id: kotlin.collections.List<Id>): List<ListByIdsParamInner>


    /**
     * 根据id批量获取用户简单信息
     * 
     *
     * @param id
     * @return List<ListSimpleInfoByIdsParamInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/listSimpleInfoByIds"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun listSimpleInfoByIds(@Valid id: kotlin.collections.List<Id>): List<ListSimpleInfoByIdsParamInner>


    /**
     * 分页查询用户列表
     * 
     *
     * @param page
     * @return PagedResult<UserInfo>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/page"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun page(@Valid page: Page): PagedResult<UserInfo>


    /**
     * 条件分页查询用户列表
     * 
     *
     * @param pageQueryUserParam
     * @return PagedResult<UserInfo>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/pageList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun pageList(@Valid pageQueryUserParam: PageQueryUserParam): PagedResult<UserInfo>


    /**
     * 新增修改后台用户
     * 
     *
     * @param saveUserRoleOrgRequest
     * @return Id
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/saveUserRoleOrg"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun saveUserRoleOrg(@Valid saveUserRoleOrgRequest: SaveUserRoleOrgRequest): Id


    /**
     * 获取当前用户信息
     * 
     *
     * @return UserRoleOrg
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/current"],
        produces = ["application/json"]
    )
    fun userCurrentPost(): UserRoleOrg


    /**
     * 根据机构ID和地区ID获取当前用户下的地区和机构ID
     * 
     *
     * @param validOrgRegionPermissionParam
     * @return ValidOrgRegionPermissionResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/user/validOrgRegionPermission"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun validOrgRegionPermission(@Valid validOrgRegionPermissionParam: ValidOrgRegionPermissionParam): ValidOrgRegionPermissionResult
}
