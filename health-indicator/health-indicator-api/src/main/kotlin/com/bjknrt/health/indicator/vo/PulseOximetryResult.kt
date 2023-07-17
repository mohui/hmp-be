package com.bjknrt.health.indicator.vo

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
 * PulseOximetryResult
 * @param knId  
 * @param knCreatedAt  
 * @param knPulseOximetry  
 * @param knMeasureAt  
 */
data class PulseOximetryResult(
    
    @field:Valid
    @field:JsonProperty("KnId", required = true) val knId: Id,
    
    @field:JsonProperty("knCreatedAt", required = true) val knCreatedAt: java.time.LocalDateTime,
    
    @field:JsonProperty("knPulseOximetry", required = true) val knPulseOximetry: java.math.BigDecimal,
    
    @field:JsonProperty("knMeasureAt", required = true) val knMeasureAt: java.time.LocalDateTime
) {

}

