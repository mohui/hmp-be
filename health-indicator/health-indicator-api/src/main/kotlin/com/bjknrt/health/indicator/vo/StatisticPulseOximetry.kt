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
 * @param knMeasureAt  
 * @param knPulseOximetry  
 */
data class StatisticPulseOximetry(
    
    @field:Valid
    @field:JsonProperty("knMeasureAt", required = true) val knMeasureAt: java.time.LocalDate,
    
    @field:JsonProperty("knPulseOximetry", required = true) val knPulseOximetry: java.math.BigDecimal
) {

}

