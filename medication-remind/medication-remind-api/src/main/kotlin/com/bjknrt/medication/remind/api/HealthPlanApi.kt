package com.bjknrt.medication.remind.api

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.medication.remind.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid
@KatoClient(name = "\${app.hmp-medication-remind.kato-server-name:\${spring.application.name}}", contextId = "HealthPlanApi")
@Validated
interface HealthPlanApi {


    /**
     * 批量添加健康计划
     * 
     *
     * @param frequencyHealthAllParam
     * @return BatchHealthPlanResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/batchAddHealthPlan"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchAddHealthPlan(@Valid frequencyHealthAllParam: FrequencyHealthAllParam): BatchHealthPlanResult


    /**
     * 根据id批量打卡接口(不推荐)
     * 
     *
     * @param id
     * @return List<HealthPlan>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/batchIdClockIn"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchIdClockIn(@Valid id: kotlin.collections.List<Id>): List<HealthPlan>


    /**
     * 根据id批量打卡接口(推荐使用)
     * 
     *
     * @param batchClockInParams
     * @return List<HealthPlan>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/batchIdClockInTwo"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchIdClockIn(@Valid batchClockInParams: BatchClockInParams): List<HealthPlan>


    /**
     * 根据type批量打卡接口
     * 
     *
     * @param batchTypeClockInParams
     * @return List<HealthPlan>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/batchIdTypeClockIn"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun batchIdTypeClockIn(@Valid batchTypeClockInParams: BatchTypeClockInParams): List<HealthPlan>


    /**
     * 打卡(FE)
     * 
     *
     * @param body
     * @return List<HealthPlan>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/clockIn"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun clockIn(@Valid body: java.math.BigInteger): List<HealthPlan>


    /**
     * 打卡历史记录
     * 
     *
     * @param clockInHistoryParam
     * @return List<ClockInHistoryResult>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/clockInHistory"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun clockInHistory(@Valid clockInHistoryParam: ClockInHistoryParam): List<ClockInHistoryResult>


    /**
     * 根据开始时间,结束时间,健康计划ID获取打卡次数
     * 
     *
     * @param dateTimeGetClockInParams
     * @return kotlin.Int
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/dateTimeGetClockIn"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun dateTimeGetClockIn(@Valid dateTimeGetClockInParams: DateTimeGetClockInParams): kotlin.Int


    /**
     * 根据健康计划id和规则获取打卡记录
     * 
     *
     * @param frequencyGetClockInParam
     * @return kotlin.Int
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/frequencyGetClockIn"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun frequencyGetClockIn(@Valid frequencyGetClockInParam: FrequencyGetClockInParam): kotlin.Int


    /**
     * 根据健康计划id和规则id获取打卡记录
     * 
     *
     * @param healthPlanFrequencyGetClockInParam
     * @return kotlin.Int
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/healthPlanFrequencyGetClockIn"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun healthPlanFrequencyGetClockIn(@Valid healthPlanFrequencyGetClockInParam: HealthPlanFrequencyGetClockInParam): kotlin.Int


    /**
     * 根据健康计划id获取健康计划列表
     * 
     *
     * @param id
     * @return List<HealthPlanMain>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/idGetList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun idGetList(@Valid id: kotlin.collections.List<Id>): List<HealthPlanMain>


    /**
     * 健康计划列表 (FE v1.8)
     * 
     *
     * @return List<HealthPlan>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/list"],
        produces = ["application/json"]
    )
    fun list(): List<HealthPlan>


    /**
     * 根据患者ID获取健康计划列表
     * 
     *
     * @param patientIdGetListParam
     * @return List<HealthPlanMain>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/patientIdGetList"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientIdGetList(@Valid patientIdGetListParam: PatientIdGetListParam): List<HealthPlanMain>


    /**
     * 健康计划添加和修改
     * 
     *
     * @param frequencyHealthParams
     * @return UpsertHealthFrequencyResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/upsertFrequencyHealth"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun upsertFrequencyHealth(@Valid frequencyHealthParams: FrequencyHealthParams): UpsertHealthFrequencyResult


    /**
     * 批量添加健康计划(先删除,后添加)
     * 
     *
     * @param frequencyHealthAllParam
     * @return BatchHealthPlanResult
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/healthPlan/upsertTypeFrequencyHealth"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun upsertTypeFrequencyHealth(@Valid frequencyHealthAllParam: FrequencyHealthAllParam): BatchHealthPlanResult
}
