package com.bjknrt.user.permission.centre.vo

import java.util.Objects
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
 * HealthServiceExistsRequest
 * @param patientId  
 * @param serviceCode  basic,基础健康管理服务 htn,高血压健康管理服务 t2dm,糖尿病健康管理服务 pain,疼痛健康管理服务
 */
data class HealthServiceExistsRequest(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,
    
    @field:JsonProperty("serviceCode", required = true) val serviceCode: kotlin.String
) {

}

