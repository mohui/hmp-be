package com.bjknrt.statistic.analysis.vo

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
 * RegionPatientPeopleNum
 * @param code  
 * @param name  行政区域名称
 * @param num  
 */
data class RegionPatientPeopleNum(
    
    @field:Valid
    @field:JsonProperty("code", required = true) val code: java.math.BigInteger,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:JsonProperty("num", required = true) val num: kotlin.Long
) {

}

