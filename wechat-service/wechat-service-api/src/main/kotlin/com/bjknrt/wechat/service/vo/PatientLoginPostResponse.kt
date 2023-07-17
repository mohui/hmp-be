package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *  PatientLoginPostResponse
 * @param token token
 * @param id
 * @param phone phone
 */
data class PatientLoginPostResponse(

    @field:JsonProperty("token", required = true) val token: kotlin.String,

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:JsonProperty("phone", required = true) val phone: kotlin.String
) {

}

