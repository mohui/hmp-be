package com.bjknrt.wechat.service.service

import cn.hutool.http.HttpRequest
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.extension.LOGGER
import com.bjknrt.wechat.service.vo.*
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * 推送服务消息实体
 */
data class PushMessageDTO(
    /**
     * 类型
     *
     * patient: 患者端; doctor: 医生端
     */
    @JsonProperty("userType") var type: String,
    /**
     * 模板id
     */
    @JsonProperty("messageCode") var templateId: String,
    /**
     * 微信用户UnionID
     *
     * TODO: 目前暂用手机号码, 等待国卫升级
     */
    @JsonProperty("phoneNumber") var userId: String,
    /**
     * 消息title
     */
    @JsonProperty("title") var title: String,
    /**
     * 消息keyword1
     */
    @JsonProperty("keyword1") var keyword1: String,
    /**
     * 消息keyword2
     */
    @JsonProperty("keyword2") var keyword2: String,
    /**
     * 消息keyword3
     */
    @JsonProperty("keyword3") var keyword3: String?,
    /**
     * 消息keyword4
     */
    @JsonProperty("keyword4") var keyword4: String?,
    /**
     * 消息remark
     */
    @JsonProperty("remark") var remark: String?,
)

/**
 * 推送服务结果实体
 */
data class PushResultDTO(
    var code: Int,
    var message: String,
)

@Service
class PushService(
    @Value("\${fake_token}") val token: String,
    val json: ObjectMapper,
    val patientClient: PatientApi
) {
    fun push(message: PushMessageDTO) {
        try {
            // TODO: 日后实现患者id换unionid
            // 查询患者信息
            val patientModel = patientClient.getPatientInfo(message.userId.toBigInteger())
            message.userId = patientModel.phone
            LOGGER.info("推送开始: $message")
            val result = HttpRequest
                .post("http://gwcloud.ncddata.org:8001/cdmp-backend/tps/sendMessage")
                .header("Token", token)
                .body(json.writeValueAsString(message))
                .timeout(4000)
                .execute()
                .body()
            LOGGER.info("推送返回: $result")
            val resultModel: PushResultDTO = json.readValue(result)
            LOGGER.info("推送 ${if (resultModel.code == 200) "成功" else "失败"} $message")
        } catch (e: Exception) {
            LOGGER.error("推送异常: $e")
        }
    }
}
