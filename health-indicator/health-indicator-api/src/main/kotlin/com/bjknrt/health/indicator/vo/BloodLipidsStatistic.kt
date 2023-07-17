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
 * @param hdlValue  
 * @param ldlValue  
 * @param tcValue  
 * @param tgValue
 */
data class BloodLipidsStatistic(
    
    @field:Valid
    @field:JsonProperty("measureDate", required = true) val measureDate: java.time.LocalDate,

    @field:JsonProperty("hdlValue") val hdlValue: java.math.BigDecimal? = null,

    @field:JsonProperty("ldlValue") val ldlValue: java.math.BigDecimal? = null,

    @field:JsonProperty("tcValue") val tcValue: java.math.BigDecimal? = null,

    @field:JsonProperty("tgValue") val tgValue: java.math.BigDecimal? = null
) {

}

