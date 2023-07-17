package com.bjknrt.medication.remind.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * frequencyGetClockInResult
 * @param id  主键id
 * @param name  健康计划名称
 * @param type  
 * @param subName  副名称,如:有氧运动-游泳,的游泳
 * @param desc  打卡说明
 * @param subFrequency  
 * @param mainFrequency  
 */
data class FrequencyGetClockInResult(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("type", required = true) val type: HealthPlanType,

    @field:JsonProperty("subName") val subName: kotlin.String? = null,

    @field:JsonProperty("desc") val desc: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("subFrequency") val subFrequency: FrequencyClock? = null,

    @field:Valid
    @field:JsonProperty("mainFrequency") val mainFrequency: FrequencyClock? = null
) {

}

