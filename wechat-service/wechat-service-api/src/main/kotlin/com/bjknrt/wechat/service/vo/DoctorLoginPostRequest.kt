package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * DoctorLoginPostRequest
 * @param code
 * @param user
 */
data class DoctorLoginPostRequest(

    @field:JsonProperty("code", required = true) val code: kotlin.String,

    @field:Valid
    @field:JsonProperty("user", required = true) val user: Id
) {

}

