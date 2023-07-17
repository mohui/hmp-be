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
 * DrinkingResult
 * @param knId  
 * @param knCreatedAt  
 * @param knBeer  
 * @param knWhiteSpirit  
 * @param knWine  
 * @param knYellowRichSpirit  
 * @param knTotalAlcohol  
 * @param knMeasureAt  
 */
data class DrinkingResult(
    
    @field:Valid
    @field:JsonProperty("KnId", required = true) val knId: Id,
    
    @field:JsonProperty("knCreatedAt", required = true) val knCreatedAt: java.time.LocalDateTime,
    
    @field:JsonProperty("knBeer", required = true) val knBeer: java.math.BigDecimal,
    
    @field:JsonProperty("knWhiteSpirit", required = true) val knWhiteSpirit: java.math.BigDecimal,
    
    @field:JsonProperty("knWine", required = true) val knWine: java.math.BigDecimal,
    
    @field:JsonProperty("knYellowRichSpirit", required = true) val knYellowRichSpirit: java.math.BigDecimal,
    
    @field:JsonProperty("knTotalAlcohol", required = true) val knTotalAlcohol: java.math.BigDecimal,
    
    @field:JsonProperty("knMeasureAt", required = true) val knMeasureAt: java.time.LocalDateTime
) {

}

