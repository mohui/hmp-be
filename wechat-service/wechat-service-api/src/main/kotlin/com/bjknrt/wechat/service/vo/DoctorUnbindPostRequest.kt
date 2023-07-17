package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param id  医生id
 * @param patient  患者id
 * @param date
 */
data class DoctorUnbindPostRequest(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:Valid
    @field:JsonProperty("patient", required = true) val patient: Id,

    @field:JsonProperty("date", required = true) val date: java.time.LocalDateTime
) {

}

