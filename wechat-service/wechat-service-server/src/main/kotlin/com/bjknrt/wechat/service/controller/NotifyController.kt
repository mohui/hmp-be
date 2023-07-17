package com.bjknrt.wechat.service.controller

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.wechat.service.api.NotifyApi
import com.bjknrt.wechat.service.boot.DoctorWechatConfig
import com.bjknrt.wechat.service.boot.WechatConfig
import com.bjknrt.wechat.service.dao.KnWechatUserTable
import com.bjknrt.wechat.service.vo.NotifyListPostResponseInner
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.wechat.service.api.NotifyController")
class NotifyController(
    var wechatService: WechatConfig,
    var doctorService: DoctorWechatConfig,
    var wechatUserTable: KnWechatUserTable,
    var patientClient: PatientApi,
) : AppBaseController(), NotifyApi {
    override fun notifyListPost(body: String): List<NotifyListPostResponseInner> {
        if (body == "patient") {
            return listOf(
                NotifyListPostResponseInner("用药提醒", wechatService.drugMessageId)
            )
        }
        return emptyList()
    }
}