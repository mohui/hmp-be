package com.bjknrt.wechat.service.controller

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.wechat.service.api.DoctorApi
import com.bjknrt.wechat.service.boot.DoctorWechatConfig
import com.bjknrt.wechat.service.boot.ExecutorConfig
import com.bjknrt.wechat.service.dao.KnWechatUser
import com.bjknrt.wechat.service.dao.KnWechatUserTable
import com.bjknrt.wechat.service.vo.DoctorLoginPostRequest
import com.bjknrt.wechat.service.vo.UnbindNotify
import me.danwi.sqlex.core.query.Order
import me.danwi.sqlex.core.query.eq
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter
import javax.annotation.Resource

@RestController("com.bjknrt.wechat.service.api.DoctorController")
class DoctorController(
    var wechatUserTable: KnWechatUserTable,
    var wechatService: DoctorWechatConfig,
    var patientClient: PatientApi
) : AppBaseController(), DoctorApi {

    @Resource(name = ExecutorConfig.WE_CHAT_NOTIFY_EXECUTOR)
    lateinit var weChatNotifyExecutor: ThreadPoolTaskExecutor

    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    override fun doctorLoginPost(doctorLoginPostRequest: DoctorLoginPostRequest) {
        //获取微信用户
        val userResult = wechatService.doctorService().jsCode2SessionInfo(doctorLoginPostRequest.code)
        //upsert微信用户表
        val model = KnWechatUser.builder()
            .setKnWechat(wechatService.id)
            .setKnOpenid(userResult.openid)
            .setKnUser(doctorLoginPostRequest.user)
            .build()
        wechatUserTable.saveOrUpdate(model)
    }

    override fun doctorNotifyUnbindPost(unbindNotify: UnbindNotify): Any? {
        //获取患者信息
        val patientModel = patientClient.getPatientInfo(unbindNotify.patientId)
        //获取医生对应的微信信息
        val wechatUserModel = wechatUserTable
            .select()
            .where(KnWechatUserTable.KnUser eq unbindNotify.doctorId)
            .where(KnWechatUserTable.KnWechat eq wechatService.id)
            .order(KnWechatUserTable.KnUpdatedAt, Order.Desc)
            .findOne() ?: throw NotFoundDataException("医生 [${unbindNotify.doctorId}] 不存在")
        weChatNotifyExecutor.execute {
            //发送通知
            wechatService
                .doctorService()
                .msgService
                .sendSubscribeMsg(
                    WxMaSubscribeMessage.builder()
                        .toUser(wechatUserModel.knOpenid)
                        .templateId(wechatService.unbindMessageId)
                        .miniprogramState(wechatService.state)
                        .page("/pages/splash/index")
                        .data(
                            listOf(
                                WxMaSubscribeMessage.MsgData("thing1", patientModel.name),
                                WxMaSubscribeMessage.MsgData("phrase2", "解绑"),
                                WxMaSubscribeMessage.MsgData(
                                    "time3",
                                    unbindNotify.date.format(dateTimeFormatter)
                                ),
                            )
                        )
                        .build()
                )
        }
        return null
    }
}
