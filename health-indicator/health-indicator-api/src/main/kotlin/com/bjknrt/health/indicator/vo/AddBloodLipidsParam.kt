package com.bjknrt.health.indicator.vo

import java.util.Objects
import com.bjknrt.health.indicator.vo.FromTag
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
 * AddBloodLipidsParam
 * @param knPatientId  
 * @param fromTag  
 * @param knTotalCholesterol  
 * @param knTriglycerides  
 * @param knLowDensityLipoprotein  
 * @param knHighDensityLipoprotein 高密度脂蛋白/mmol/L 
 * @param knMeasureAt  
 */
data class AddBloodLipidsParam(
    
    @field:Valid
    @field:JsonProperty("knPatientId", required = true) val knPatientId: Id,
    
    @field:Valid
    @field:JsonProperty("fromTag", required = true) val fromTag: FromTag,

    @field:JsonProperty("knTotalCholesterol") val knTotalCholesterol: java.math.BigDecimal? = null,

    @field:JsonProperty("knTriglycerides") val knTriglycerides: java.math.BigDecimal? = null,

    @field:JsonProperty("knLowDensityLipoprotein") val knLowDensityLipoprotein: java.math.BigDecimal? = null,

    @field:JsonProperty("knHighDensityLipoprotein") val knHighDensityLipoprotein: java.math.BigDecimal? = null,

    @field:JsonProperty("knMeasureAt") val knMeasureAt: java.time.LocalDateTime? = null
) {

}

