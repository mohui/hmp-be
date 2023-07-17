package com.bjknrt.user.permission.centre.api

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.user.permission.centre.vo.Org
import com.bjknrt.user.permission.centre.vo.OrgCodePageParam
import com.bjknrt.user.permission.centre.vo.OrgPageParam
import com.bjknrt.framework.api.vo.PagedResult
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid

import kotlin.collections.List

@KatoClient(name = "\${app.hmp-user-permission-centre.kato-server-name:\${spring.application.name}}", contextId = "OrgApi2")
@Validated
interface OrgApi {


    /**
     * 根据id数组查询列表
     *
     *
     * @param id
     * @return List<Org>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/org/listByIds"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun listByIds(@Valid id: kotlin.collections.List<Id>): List<Org>


    /**
     * 组织/机构列表查询
     *
     *
     * @param orgPageParam
     * @return PagedResult<Org>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/org/page"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun page(@Valid orgPageParam: OrgPageParam): PagedResult<Org>


    /**
     * 根据行政区域code查询直属组织列表
     *
     *
     * @param orgCodePageParam
     * @return PagedResult<Org>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/org/pageOrgByAdministrativeCode"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun pageOrgByAdministrativeCode(@Valid orgCodePageParam: OrgCodePageParam): PagedResult<Org>
}
