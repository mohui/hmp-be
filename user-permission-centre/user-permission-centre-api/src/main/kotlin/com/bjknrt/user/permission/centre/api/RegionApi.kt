package com.bjknrt.user.permission.centre.api

import com.bjknrt.user.permission.centre.vo.ChildListRequest
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.user.permission.centre.vo.Region
import me.danwi.kato.client.KatoClient

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated

import javax.validation.Valid

import kotlin.collections.List

@KatoClient(name = "\${app.hmp-user-permission-centre.kato-server-name:\${spring.application.name}}", contextId = "RegionApi2")
@Validated
interface RegionApi {


    /**
     * 获取当前编码的子集行政区域代码集合
     * 
     *
     * @param childListRequest
     * @return List<Region>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/region/childList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getChildList(@Valid childListRequest: ChildListRequest): List<Region>


    /**
     * 获取当前的行政区域代码
     * 
     *
     * @param body
     * @return Region
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/region/currentCode"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getCurrentRegion(@Valid body: java.math.BigInteger): Region


    /**
     * 获取当前的行政区域代码
     * 
     *
     * @return Region
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/region/current"],
        produces = ["application/json"]
    )
    fun getCurrentRegion(): Region


    /**
     * 获取行政区域代码
     * 
     *
     * @param id
     * @return List<Region>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/region/list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun getRegionList(@Valid id: kotlin.collections.List<Id>): List<Region>
}
