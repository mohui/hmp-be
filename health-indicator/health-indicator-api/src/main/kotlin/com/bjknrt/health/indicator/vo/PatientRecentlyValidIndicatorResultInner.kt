package com.bjknrt.health.indicator.vo

import java.util.Objects
import com.bjknrt.health.indicator.vo.IndicatorEnum
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
 * PatientRecentlyValidIndicatorResultInner
 * @param key  
 * @param knCreatedAt  
 * @param knMeasureAt  
 * @param _value  指标值
 */
data class PatientRecentlyValidIndicatorResultInner(
    
    @field:Valid
    @field:JsonProperty("key", required = true) val key: IndicatorEnum,
    
    @field:JsonProperty("knCreatedAt", required = true) val knCreatedAt: java.time.LocalDateTime,
    
    @field:JsonProperty("knMeasureAt", required = true) val knMeasureAt: java.time.LocalDateTime,

    @field:JsonProperty("value") val _value: java.math.BigDecimal? = null
) {

}

