package com.bjknrt.wechat.service.api

import com.bjknrt.wechat.service.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.wechat-service.kato-server-name:\${spring.application.name}}", contextId = "PatientApi2")
@Validated
interface PatientApi {


    /**
     * 患者登录 (v1.0)
     *
     *
     * @param patientLoginPostRequest
     * @return PatientLoginPostResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/login"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientLoginPost(@Valid patientLoginPostRequest: PatientLoginPostRequest): PatientLoginPostResponse


    /**
     *  用药提醒通知 (v1.10)
     *
     *
     * @param patientNotifyDrugPostRequestInner
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/drug"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyDrugPost(@Valid patientNotifyDrugPostRequestInner: kotlin.collections.List<PatientNotifyDrugPostRequestInner>): kotlin.Any?


    /**
     * 测量计划通知 (v1.10)
     *
     *
     * @param patientNotifyIndicatorScheduledPostRequestInner
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/indicator-scheduled"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyIndicatorScheduledPost(@Valid patientNotifyIndicatorScheduledPostRequestInner: kotlin.collections.List<PatientNotifyIndicatorScheduledPostRequestInner>): kotlin.Any?


    /**
     * 监测指标异常通知 (v1.10)
     *
     *
     * @param indicatorNotify
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/indicator-warning"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyIndicatorWarningPost(@Valid indicatorNotify: IndicatorNotify): kotlin.Any?


    /**
     * 医生留言通知 (FE v1.10)
     *
     *
     * @param messageNotify
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/message"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyMessagePost(@Valid messageNotify: MessageNotify): kotlin.Any?


    /**
     * 购买服务通知 (FE v1.10)
     *
     *
     * @param serviceNotifyList 购买服务数组
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/service"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyServicePost(@Valid serviceNotifyList: kotlin.collections.List<ServiceNotifyInner>): kotlin.Any?


    /**
     * 医生解绑通知 (FE v1.10)
     *
     *
     * @param unbindNotify 解绑通知实体
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/unbind"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyUnbindPost(@Valid unbindNotify: UnbindNotify): kotlin.Any?


    /**
     * 生成出院随访提醒 (v1.10)
     *
     *
     * @param dischargeVisitNotify 出院随访通知实体
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/visit-discharge"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyVisitDischargePost(@Valid dischargeVisitNotify: DischargeVisitNotify): kotlin.Any?


    /**
     * 最后月初线下随访提醒 (v1.10)
     *
     *
     * @param visitNotify
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/visit-offline-final"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyVisitOfflineFinalPost(@Valid visitNotify: kotlin.collections.List<VisitNotify>): kotlin.Any?


    /**
     * 线下随访提醒 (v1.10)
     *
     *
     * @param visitNotify
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/visit-offline"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyVisitOfflinePost(@Valid visitNotify: kotlin.collections.List<VisitNotify>): kotlin.Any?


    /**
     * 线上随访提醒 (v1.10)
     *
     *
     * @param visitNotify
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/patient/notify/visit-online"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun patientNotifyVisitOnlinePost(@Valid visitNotify: kotlin.collections.List<VisitNotify>): kotlin.Any?
}
