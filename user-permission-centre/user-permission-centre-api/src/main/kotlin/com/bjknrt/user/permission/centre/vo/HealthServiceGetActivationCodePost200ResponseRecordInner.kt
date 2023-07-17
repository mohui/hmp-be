package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param operate  操作
 * @param operaterId
 * @param operaterName  操作人名称
 * @param operaterLoginName  操作人登陆名称（患者为手机号）
 * @param operateTime
 */
data class HealthServiceGetActivationCodePost200ResponseRecordInner(

    @field:JsonProperty("operate", required = true) val operate: kotlin.String,

    @field:Valid
    @field:JsonProperty("operaterId", required = true) val operaterId: Id,

    @field:JsonProperty("operaterName", required = true) val operaterName: kotlin.String,

    @field:JsonProperty("operaterLoginName", required = true) val operaterLoginName: kotlin.String,

    @field:JsonProperty("operateTime", required = true) val operateTime: java.time.LocalDateTime,
) {

}

