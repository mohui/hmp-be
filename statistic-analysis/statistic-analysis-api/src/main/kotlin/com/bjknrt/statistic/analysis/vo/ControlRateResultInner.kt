package com.bjknrt.statistic.analysis.vo

import java.util.Objects
import com.bjknrt.statistic.analysis.vo.CrowdType
import com.bjknrt.statistic.analysis.vo.Gender
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
 * @param crowd  
 * @param gender  
 * @param count  数量
 */
data class ControlRateResultInner(
    
    @field:Valid
    @field:JsonProperty("crowd", required = true) val crowd: CrowdType,
    
    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,
    
    @field:JsonProperty("count", required = true) val count: java.math.BigDecimal
) {

}

