package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * ClockInRequest
 * @param patientId  
 * @param healthPlanType  
 * @param currentDateTime  
 * @param clockAt  
 */
data class ClockInRequest(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("healthPlanType", required = true) val healthPlanType: HealthPlanType,
    
    @field:JsonProperty("currentDateTime", required = true) val currentDateTime: java.time.LocalDateTime,

    @field:JsonProperty("clockAt") val clockAt: java.time.LocalDateTime? = null
) {

}

