package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 解绑通知实体
 *
 *
 * @param patientId 患者id
 * @param doctorId  医生id
 * @param date 解绑时间
 */
data class UnbindNotify(

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:Valid
    @field:JsonProperty("doctorId", required = true) val doctorId: Id,

    @field:JsonProperty("date", required = true) val date: java.time.LocalDateTime
) {

}

