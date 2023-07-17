package com.bjknrt.medication.remind.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * UpsertParams
 * @param drugName 药品名称 可多种药品
 * @param time Time 时间
 * @param frequencys  周一至周日
 * @param id  
 */
data class UpsertParams(
    
    @field:JsonProperty("drugName", required = true) val drugName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("time", required = true) val time: java.time.LocalTime,
    
    @field:Valid
    @field:JsonProperty("frequencys", required = true) val frequencys: kotlin.collections.List<Week>,

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null
) {

}

