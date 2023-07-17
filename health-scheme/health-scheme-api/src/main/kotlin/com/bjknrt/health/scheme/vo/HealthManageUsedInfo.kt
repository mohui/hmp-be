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
 * 
 * @param startDate  
 * @param healthManageType  
 * @param createdAt  
 * @param endDate  
 */
data class HealthManageUsedInfo(
    
    @field:Valid
    @field:JsonProperty("startDate", required = true) val startDate: java.time.LocalDate,
    
    @field:Valid
    @field:JsonProperty("healthManageType", required = true) val healthManageType: HealthManageType,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,

    @field:Valid
    @field:JsonProperty("endDate") val endDate: java.time.LocalDate? = null
) {

}

