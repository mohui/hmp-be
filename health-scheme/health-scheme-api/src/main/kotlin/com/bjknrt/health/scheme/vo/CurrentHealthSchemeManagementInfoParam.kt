package com.bjknrt.health.scheme.vo

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
 * CurrentHealthSchemeManagementInfoParam
 * @param patientId  
 * @param currentDate  
 */
data class CurrentHealthSchemeManagementInfoParam(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:JsonProperty("currentDate") val currentDate: java.time.LocalDateTime? = null
) {

}

