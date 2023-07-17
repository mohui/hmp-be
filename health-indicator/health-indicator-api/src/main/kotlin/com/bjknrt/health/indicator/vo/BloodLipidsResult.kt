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
 * BloodLipidsResult
 * @param knId  
 * @param knCreatedAt  
 * @param knMeasureAt  
 * @param knTotalCholesterol  
 * @param knTriglycerides  
 * @param knLowDensityLipoprotein  
 * @param knHighDensityLipoprotein  
 */
data class BloodLipidsResult(
    
    @field:Valid
    @field:JsonProperty("KnId", required = true) val knId: Id,
    
    @field:JsonProperty("knCreatedAt", required = true) val knCreatedAt: java.time.LocalDateTime,
    
    @field:JsonProperty("knMeasureAt", required = true) val knMeasureAt: java.time.LocalDateTime,

    @field:JsonProperty("knTotalCholesterol") val knTotalCholesterol: java.math.BigDecimal? = null,

    @field:JsonProperty("knTriglycerides") val knTriglycerides: java.math.BigDecimal? = null,

    @field:JsonProperty("knLowDensityLipoprotein") val knLowDensityLipoprotein: java.math.BigDecimal? = null,

    @field:JsonProperty("knHighDensityLipoprotein") val knHighDensityLipoprotein: java.math.BigDecimal? = null
) {

}

