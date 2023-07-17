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
 * @param fbsValue  
 * @param bbsValue  
 * @param abpValue  
 * @param sbsValue
 * @param rbsValue
 */
data class BloodSugarStatistic(
    
    @field:Valid
    @field:JsonProperty("measureDate", required = true) val measureDate: java.time.LocalDate,

    @field:JsonProperty("fbsValue") val fbsValue: java.math.BigDecimal? = null,

    @field:JsonProperty("bbsValue") val bbsValue: java.math.BigDecimal? = null,

    @field:JsonProperty("abpValue") val abpValue: java.math.BigDecimal? = null,

    @field:JsonProperty("sbsValue") val sbsValue: java.math.BigDecimal? = null,

    @field:JsonProperty("rbsValue") val rbsValue: java.math.BigDecimal? = null
) {

}

