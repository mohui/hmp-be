package com.bjknrt.health.indicator.vo

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
 * 
 * @param measureDate  
 * @param pulse  
 */
data class PulseStatistic(
    
    @field:Valid
    @field:JsonProperty("measureDate", required = true) val measureDate: java.time.LocalDate,
    
    @field:JsonProperty("pulse", required = true) val pulse: java.math.BigDecimal
) {

}

