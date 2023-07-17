package com.bjknrt.medication.remind.vo

import java.util.Objects
import com.bjknrt.medication.remind.vo.FrequencyNumUnit
import com.bjknrt.medication.remind.vo.TimeUnit
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
 * @param frequencyTime  频次时间
 * @param frequencyTimeUnit  
 * @param frequencyNum  最小达标频次数量
 * @param frequencyNumUnit  
 * @param frequencyMaxNum  完成频次数量
 * @param actualNum  实际打卡数量
 */
data class FrequencyClock(
    
    @field:JsonProperty("frequencyTime", required = true) val frequencyTime: kotlin.Int,
    
    @field:Valid
    @field:JsonProperty("frequencyTimeUnit", required = true) val frequencyTimeUnit: TimeUnit,
    
    @field:JsonProperty("frequencyNum", required = true) val frequencyNum: kotlin.Int,
    
    @field:Valid
    @field:JsonProperty("frequencyNumUnit", required = true) val frequencyNumUnit: FrequencyNumUnit,
    
    @field:JsonProperty("frequencyMaxNum", required = true) val frequencyMaxNum: kotlin.Int,
    
    @field:JsonProperty("actualNum", required = true) val actualNum: kotlin.Int
) {

}

