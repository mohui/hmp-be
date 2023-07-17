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
 * AddDrinkingParam
 * @param knPatientId  
 * @param knBeer  
 * @param knWhiteSpirit  
 * @param knWine  
 * @param knYellowRichSpirit  
 * @param fromTag  
 * @param knMeasureAt  
 */
data class AddDrinkingParam(
    
    @field:Valid
    @field:JsonProperty("knPatientId", required = true) val knPatientId: Id,
    
    @field:JsonProperty("knBeer", required = true) val knBeer: java.math.BigDecimal,
    
    @field:JsonProperty("knWhiteSpirit", required = true) val knWhiteSpirit: java.math.BigDecimal,
    
    @field:JsonProperty("knWine", required = true) val knWine: java.math.BigDecimal,
    
    @field:JsonProperty("knYellowRichSpirit", required = true) val knYellowRichSpirit: java.math.BigDecimal,
    
    @field:Valid
    @field:JsonProperty("fromTag", required = true) val fromTag: FromTag,

    @field:JsonProperty("knMeasureAt") val knMeasureAt: java.time.LocalDateTime? = null
) {

}

