package com.bjknrt.wechat.service.controller

import com.bjknrt.doctor.patient.management.api.DoctorApi
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.security.JwtAuthenticationConverter
import com.bjknrt.user.permission.centre.api.AuthApi
import com.bjknrt.user.permission.centre.vo.IdentityTag
import com.bjknrt.user.permission.centre.vo.LoginNoVerifyParam
import com.bjknrt.wechat.service.api.PatientApi
import com.bjknrt.wechat.service.boot.ExecutorConfig.Companion.WE_CHAT_NOTIFY_EXECUTOR
import com.bjknrt.wechat.service.boot.WechatConfig
import com.bjknrt.wechat.service.dao.KnWechatUser
import com.bjknrt.wechat.service.dao.KnWechatUserTable
import com.bjknrt.wechat.service.service.PushMessageDTO
import com.bjknrt.wechat.service.service.PushService
import com.bjknrt.wechat.service.vo.*
import org.springframework.scheduling.annotation.Async
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController("com.bjknrt.wechat.service.api.PatientController")
class PatientController(
    var wechatUserTable: KnWechatUserTable,
    var wechatService: WechatConfig,
    val authClient: AuthApi,
    val doctorClient: DoctorApi,
    val jwtAuthenticationConverter: JwtAuthenticationConverter,
    val pushService: PushService,
) : AppBaseController(), PatientApi {
    /**
     * 日期格式
     */
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")

    /**
     * 时间格式
     */
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    @Transactional
    override fun patientLoginPost(patientLoginPostRequest: PatientLoginPostRequest): PatientLoginPostResponse {
        //获取微信用户
        val userResult = wechatService.service().jsCode2SessionInfo(patientLoginPostRequest.user)
        //获取手机号码
        val phoneResult = wechatService.service().userService.getNewPhoneNoInfo(patientLoginPostRequest.phone)
        //手机号码
        val phone = phoneResult.purePhoneNumber
        //获取认证中心认证
        val loginRPCResponse = authClient.loginNoVerify(LoginNoVerifyParam(phone, phone, setOf(IdentityTag.PATIENT)))
        //upsert微信用户表
        val model = KnWechatUser.builder()
            .setKnWechat(wechatService.id)
            .setKnOpenid(userResult.openid)
            .setKnUser(loginRPCResponse.id)
            .build()
        wechatUserTable.saveOrUpdate(model)
        //生成认证信息
        jwtAuthenticationConverter.renew(
            jwtAuthenticationConverter.jwtStrToJwtUserDetails(loginRPCResponse.jwt),
            getResponse()
        )

        //返回结果
        return PatientLoginPostResponse(model.knUser.toString(), model.knUser, phone)
    }

    /**
     * 用药提醒通知
     *
     * K93bnEmamoVbHu3hI-y0F_mN5lH-HV019DMx8sXPi5A
     * {{first.DATA}}
     * 药物名称：{{keyword1.DATA}}
     * 用药时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyDrugPost(patientNotifyDrugPostRequestInner: List<PatientNotifyDrugPostRequestInner>) {
        patientNotifyDrugPostRequestInner.forEach {
            pushService.push(
                PushMessageDTO(
                    type = "patient",
                    templateId = "K93bnEmamoVbHu3hI-y0F_mN5lH-HV019DMx8sXPi5A",
                    userId = it.patientId.toString(),
                    title = "您的用药时间到了！",
                    keyword1 = it.name,
                    keyword2 = it.time.format(timeFormatter),
                    keyword3 = null,
                    keyword4 = null,
                    remark = "请遵医嘱按时服用药物，有助您的疾病治疗，保持身体健康！",
                )
            )
        }
    }

    /**
     * 测量计划通知
     *
     * u7GW3uNVib3NwAJ3DnCp7xeOXGYIpxsMuf0ZZZSojtU
     * {{first.DATA}}
     * 测量项目：{{keyword1.DATA}}
     * 测量时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyIndicatorScheduledPost(patientNotifyIndicatorScheduledPostRequestInner: List<PatientNotifyIndicatorScheduledPostRequestInner>) {
        patientNotifyIndicatorScheduledPostRequestInner.forEach {
            pushService.push(
                PushMessageDTO(
                    type = "patient",
                    templateId = "u7GW3uNVib3NwAJ3DnCp7xeOXGYIpxsMuf0ZZZSojtU",
                    userId = it.patientId.toString(),
                    title = "",
                    keyword1 = it.name,
                    keyword2 = it.desc,
                    keyword3 = null,
                    keyword4 = null,
                    remark = "定期测量是监控病情的最好方式，请及时完成！",
                )
            )
        }
    }

    /**
     * 异常指标推送
     *
     * YFjxjhD3oNgsZ1czzTRH_58JUaOcJtsOVvh4AFmI6-8
     * {{first.DATA}}
     * 报告时间：{{keyword1.DATA}}
     * 异常结果：{{keyword2.DATA}}
     * 风险因素：{{keyword3.DATA}}
     * 健康建议：{{keyword4.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyIndicatorWarningPost(indicatorNotify: IndicatorNotify) {
        pushService.push(
            PushMessageDTO(
                type = "patient",
                templateId = "YFjxjhD3oNgsZ1czzTRH_58JUaOcJtsOVvh4AFmI6-8",
                userId = indicatorNotify.patientId.toString(),
                title = "您近期的监测结果异常！",
                keyword1 = indicatorNotify.date.format(dateTimeFormatter),
                keyword2 = indicatorNotify.indicators.joinToString("，") { "${it.name}测量值为${it.value}${it.unit}" },
                keyword3 = indicatorNotify.name,
                keyword4 = indicatorNotify.message,
                remark = null
            )
        )
    }

    /**
     * 医生留言通知
     *
     * 36x62qiy_h4FoNiFigjHG4B1KJDXO5XIV3rSO1UYO6M
     * {{first.DATA}}
     * 咨询内容：{{keyword1.DATA}}
     * 回复医生：{{keyword2.DATA}}
     * 回复时间：{{keyword3.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyMessagePost(messageNotify: MessageNotify) {
        pushService.push(
            PushMessageDTO(
                type = "patient",
                templateId = "36x62qiy_h4FoNiFigjHG4B1KJDXO5XIV3rSO1UYO6M",
                userId = messageNotify.patientId.toString(),
                title = "您有新的医生留言，请及时查看！",
                keyword1 = "",
                keyword2 = messageNotify.name,
                keyword3 = messageNotify.date.format(dateTimeFormatter),
                keyword4 = null,
                remark = null
            )
        )
    }

    /**
     * 购买健康管理服务通知
     *
     * Eanok9n0FeYE8Jx84o_EpM5JdHR6bE4EPPmCWog1Hxc
     * {{first.DATA}}
     * 方案名称：{{keyword1.DATA}}
     * 服务费用：{{keyword2.DATA}}
     * 购买时间：{{keyword3.DATA}}
     * 服务周期：{{keyword4.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyServicePost(serviceNotifyList: List<ServiceNotifyInner>) {
        val name = serviceNotifyList.joinToString("、") { it.name }
        pushService.push(serviceNotifyList.fold(
            PushMessageDTO(
                type = "patient",
                templateId = "Eanok9n0FeYE8Jx84o_EpM5JdHR6bE4EPPmCWog1Hxc",
                userId = "",
                title = "您已成功订阅$name",
                keyword1 = name,
                keyword2 = "",
                keyword3 = "",
                keyword4 = null,
                remark = "您的专属健康方案已生成，请坚持每天按时完成健康计划，有助于您的疾病治疗，保持身体健康！",
            )
        ) { result, current ->
            result.userId = current.patientId.toString()
            result.keyword3 = current.date.format(dateTimeFormatter)
            return@fold result
        })
    }

    /**
     * 医生解绑, 主动通知患者消息
     *
     * MsKgHQqrl66Y2phLC9uKlD-hvigWTBV_hbeT8kbAlpE
     * {{first.DATA}}
     * 原绑定成员：{{keyword1.DATA}}
     * 解绑设备：{{keyword2.DATA}}
     * 解绑时间：{{keyword3.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyUnbindPost(unbindNotify: UnbindNotify) {
        // 查询患者信息
        val doctorModel = doctorClient.getInfo(unbindNotify.doctorId)

        pushService.push(
            PushMessageDTO(
                type = "patient",
                templateId = "MsKgHQqrl66Y2phLC9uKlD-hvigWTBV_hbeT8kbAlpE",
                userId = unbindNotify.patientId.toString(),
                title = "解绑成功通知",
                keyword1 = "${doctorModel.name}医生",
                keyword2 = "",
                keyword3 = unbindNotify.date.format(dateTimeFormatter),
                keyword4 = null,
                remark = "${doctorModel.hospitalName}${doctorModel.name}医生已与您解除绑定，${doctorModel.name}医生后续将无法为您服务。祝您永葆健康！",
            )
        )
    }

    /**
     * 出院随访通知
     *
     * Kvix7sv5vu52LWzS8364pIpqF_96BxuGdiBLjdMplLw
     * {{first.DATA}}
     * 随访方案：{{keyword1.DATA}}
     * 本次随访：{{keyword2.DATA}}
     * 随访时间：{{keyword3.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyVisitDischargePost(dischargeVisitNotify: DischargeVisitNotify) {
        pushService.push(
            PushMessageDTO(
                type = "patient",
                templateId = "Kvix7sv5vu52LWzS8364pIpqF_96BxuGdiBLjdMplLw",
                userId = dischargeVisitNotify.patientId.toString(),
                title = "请您尽快完成出院随访",
                keyword1 = "出院随访",
                keyword2 = "",
                keyword3 = null,
                keyword4 = null,
                remark = "请您联系主治医生进行出院随访填写，有助于后期健康方案生成，利于您的健康管理！",
            )
        )
    }

    /**
     * 最后一月1号线下随访通知
     *
     * 每月1号 10:00 通知
     * 根据患者和时间分组, 聚合成一条通知
     *
     * Kvix7sv5vu52LWzS8364pIpqF_96BxuGdiBLjdMplLw
     * {{first.DATA}}
     * 随访方案：{{keyword1.DATA}}
     * 本次随访：{{keyword2.DATA}}
     * 随访时间：{{keyword3.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyVisitOfflineFinalPost(visitNotify: List<VisitNotify>) {
        visitNotify.zip().forEach {
            pushService.push(
                PushMessageDTO(
                    type = "patient",
                    templateId = "Kvix7sv5vu52LWzS8364pIpqF_96BxuGdiBLjdMplLw",
                    userId = it.patientId.toString(),
                    title = "",
                    keyword1 = "前往服务医生所属医疗机构进行线下随访",
                    keyword2 = "",
                    keyword3 = "${it.start.format(dateTimeFormatter)}-${it.end.format(dateTimeFormatter)}",
                    keyword4 = null,
                    remark = "本阶段线下随访时间即将结束，请您尽快前往线下就医进行随访！",
                )
            )
        }
    }

    /**
     * 线下随访通知
     *
     * 每天 10:00 通知
     * 根据患者和时间分组, 聚合成一条通知
     *
     * Kvix7sv5vu52LWzS8364pIpqF_96BxuGdiBLjdMplLw
     * {{first.DATA}}
     * 随访方案：{{keyword1.DATA}}
     * 本次随访：{{keyword2.DATA}}
     * 随访时间：{{keyword3.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyVisitOfflinePost(visitNotify: List<VisitNotify>) {
        visitNotify.zip().forEach {
            pushService.push(
                PushMessageDTO(
                    type = "patient",
                    templateId = "Kvix7sv5vu52LWzS8364pIpqF_96BxuGdiBLjdMplLw",
                    userId = it.patientId.toString(),
                    title = "",
                    keyword1 = "前往服务医生所属医疗机构进行线下随访",
                    keyword2 = "",
                    keyword3 = "${it.start.format(dateTimeFormatter)}-${it.end.format(dateTimeFormatter)}",
                    keyword4 = null,
                    remark = "请您及时填写随访，有助于跟踪观察您的病情变化，利于您的疾病管理！",
                )
            )
        }
    }

    /**
     * 线上随访通知
     *
     * 每天 09:00 通知
     * 根据患者和时间分组, 聚合成一条通知
     *
     * Kvix7sv5vu52LWzS8364pIpqF_96BxuGdiBLjdMplLw
     * {{first.DATA}}
     * 随访方案：{{keyword1.DATA}}
     * 本次随访：{{keyword2.DATA}}
     * 随访时间：{{keyword3.DATA}}
     * {{remark.DATA}}
     */
    @Async(WE_CHAT_NOTIFY_EXECUTOR)
    override fun patientNotifyVisitOnlinePost(visitNotify: List<VisitNotify>) {
        visitNotify.zip().forEach {
            pushService.push(
                PushMessageDTO(
                    type = "patient",
                    templateId = "Kvix7sv5vu52LWzS8364pIpqF_96BxuGdiBLjdMplLw",
                    userId = it.patientId.toString(),
                    title = "请您在规定时间内完成随访",
                    keyword1 = it.name,
                    keyword2 = "",
                    keyword3 = "${it.start.format(dateTimeFormatter)}-${it.end.format(dateTimeFormatter)}",
                    keyword4 = null,
                    remark = "请您及时填写随访，有助于跟踪观察您的病情变化，利于您的疾病管理！",
                )
            )
        }
    }
}
