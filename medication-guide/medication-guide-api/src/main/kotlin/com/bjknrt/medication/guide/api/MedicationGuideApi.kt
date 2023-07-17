package com.bjknrt.medication.guide.api

import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.medication.guide.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-medication-guide.kato-server-name:\${spring.application.name}}", contextId = "MedicationGuideApi")
@Validated
interface MedicationGuideApi {


    /**
     * 药品列表
     *
     *
     * @param _param
     * @return PagedResult<DrugList200ResponseInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medication-guide/drug-list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun drugList(@Valid _param: Param): PagedResult<DrugList200ResponseInner>


    /**
     * 药品通用名列表
     * 
     *
     * @param body
     * @return List<GenericList200ResponseInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medication-guide/generic-list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun genericList(@Valid body: kotlin.String): List<GenericList200ResponseInner>


    /**
     * 说明书详情
     * 
     *
     * @param body
     * @return kotlin.String
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medication-guide/monograph"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun monograph(@Valid body: kotlin.Long): kotlin.String


    /**
     * 说明书列表
     * 
     *
     * @param body
     * @return List<Inner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medication-guide/monograph-list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun monographList(@Valid body: kotlin.String): List<Inner>


    /**
     * 获取药品用药禁忌
     * 
     *
     * @param submitDrugRequest
     * @return List<DrugContraindicationInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medication-guide/submit-drug"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun submitDrug(@Valid submitDrugRequest: SubmitDrugRequest): List<DrugContraindicationInner>


    /**
     * 获取通用名用药禁忌
     * 
     *
     * @param submitGenericRequest
     * @return List<SubmitGeneric200ResponseInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medication-guide/submit-generic"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun submitGeneric(@Valid submitGenericRequest: SubmitGenericRequest): List<SubmitGeneric200ResponseInner>
}
