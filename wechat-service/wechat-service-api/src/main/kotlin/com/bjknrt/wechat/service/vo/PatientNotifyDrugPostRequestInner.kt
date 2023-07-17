package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param id
 * @param patientId
 * @param name  药品名称
 * @param time Time 时间
 */
data class PatientNotifyDrugPostRequestInner(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:Valid
    @field:JsonProperty("time", required = true) val time: java.time.LocalTime
) {

}

