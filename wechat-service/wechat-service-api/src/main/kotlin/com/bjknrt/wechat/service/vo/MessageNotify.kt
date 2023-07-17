package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 消息通知实体
 *
 *
 * @param id  消息id
 * @param patientId 患者id
 * @param name  医生名称
 * @param date 消息时间
 */
data class MessageNotify(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("date", required = true) val date: java.time.LocalDateTime
) {

}

