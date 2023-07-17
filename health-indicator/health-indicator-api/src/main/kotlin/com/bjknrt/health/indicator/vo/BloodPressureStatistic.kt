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
 * @param dbpValue  
 * @param sbpValue  
 */
data class BloodPressureStatistic(
    
    @field:Valid
    @field:JsonProperty("measureDate", required = true) val measureDate: java.time.LocalDate,
    
    @field:JsonProperty("dbpValue", required = true) val dbpValue: java.math.BigDecimal,
    
    @field:JsonProperty("sbpValue", required = true) val sbpValue: java.math.BigDecimal
) {

}

