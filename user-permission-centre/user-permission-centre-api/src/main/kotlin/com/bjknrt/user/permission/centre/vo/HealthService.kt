package com.bjknrt.user.permission.centre.vo

import java.util.Objects
import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

/**
 *
 * @param healthServiceId
 * @param healthServiceCode  服务编码
 * @param healthServiceName  服务名称
 * @param isSigned  是否签约
 * @param duringTime  有效时长-月
 * @param expireDate
 * @param activationDate
 */
data class HealthService(

    @field:Valid
    @field:JsonProperty("healthServiceId", required = true) val healthServiceId: Id,

    @field:JsonProperty("healthServiceCode", required = true) val healthServiceCode: kotlin.String,

    @field:JsonProperty("healthServiceName", required = true) val healthServiceName: kotlin.String,

    @field:JsonProperty("isSigned", required = true) val isSigned: kotlin.Boolean,

    @field:JsonProperty("duringTime") val duringTime: kotlin.Int? = null,

    @field:JsonProperty("expireDate") val expireDate: java.time.LocalDateTime? = null,

    @field:JsonProperty("activationDate") val activationDate: java.time.LocalDateTime? = null
) {

}

