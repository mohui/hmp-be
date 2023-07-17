package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.bjknrt.health.scheme.vo.HealthManageType
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
 * HealthManageUsedInfoRequest
 * @param patientId  
 * @param healthManageType  健康方案类型
 */
data class HealthManageUsedInfoRequest(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,
    
    @field:Valid
    @get:Size(min=1)
    @field:JsonProperty("healthManageType", required = true) val healthManageType: kotlin.collections.Set<HealthManageType>
) {

}

