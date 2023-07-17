package com.bjknrt.medication.remind.api

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.medication.remind.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.hmp-medication-remind.kato-server-name:\${spring.application.name}}", contextId = "MedicationRemindApi")
@Validated
interface MedicationRemindApi {


    /**
     * 根据患者ID和type删除
     * 
     *
     * @param batchDelParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medicationRemind/batchDel"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchDel(@Valid batchDelParam: BatchDelParam): Unit


    /**
     * 批量根据type和患者ID删除
     * 
     *
     * @param typesAndPatientParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medicationRemind/batchDeleteByTypeAndPatientId"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchDeleteByTypeAndPatientId(@Valid typesAndPatientParam: kotlin.collections.List<TypesAndPatientParam>): Unit


    /**
     * 根据主键ID批量删除
     * 
     *
     * @param id
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medicationRemind/batchIdDel"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchIdDel(@Valid id: kotlin.collections.List<Id>): Unit


    /**
     * 用药提醒删除
     * 
     *
     * @param body
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medicationRemind/del"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun del(@Valid body: java.math.BigInteger): kotlin.Boolean


    /**
     * 用药提醒列表
     * 
     *
     * @return List<Inner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medicationRemind/list"],
        produces = ["application/json"]
    )
    fun list(): List<Inner>


    /**
     * 用药提醒批量停用启用
     * 
     *
     * @param updBatchStatusParams
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medicationRemind/updBatchStatus"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updBatchStatus(@Valid updBatchStatusParams: UpdBatchStatusParams): Unit


    /**
     * 用药提醒停用启用
     * 
     *
     * @param updStatusParams
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medicationRemind/updStatus"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updStatus(@Valid updStatusParams: UpdStatusParams): kotlin.Boolean


    /**
     * 药品提醒添加和修改
     * 
     *
     * @param upsertParams
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/medicationRemind/upsert"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun upsert(@Valid upsertParams: UpsertParams): kotlin.Boolean
}
