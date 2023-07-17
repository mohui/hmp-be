package com.bjknrt.wechat.service.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  PatientLoginPostRequest
 * @param phone 换取phone的凭证 微信小程序获取用户手机号码同意后获得的凭证
 * @param user 换取微信用户的凭证 微信小程序login后获得的凭证
 */
data class PatientLoginPostRequest(

    @field:JsonProperty("phone", required = true) val phone: kotlin.String,

    @field:JsonProperty("user", required = true) val user: kotlin.String
) {

}

