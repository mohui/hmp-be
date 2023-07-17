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
 * 
 * @param ageRangeMin  年龄范围-小
 * @param ageRangeMax  年龄范围-大
 * @param manCount  男性慢病人数
 * @param womanCount  女性慢病人数
 */
data class ChronicDiseasePopulationInner(
    
    @field:JsonProperty("ageRangeMin", required = true) val ageRangeMin: kotlin.Int,
    
    @field:JsonProperty("ageRangeMax", required = true) val ageRangeMax: kotlin.Int,
    
    @field:JsonProperty("manCount", required = true) val manCount: kotlin.Int,
    
    @field:JsonProperty("womanCount", required = true) val womanCount: kotlin.Int
) {

}

