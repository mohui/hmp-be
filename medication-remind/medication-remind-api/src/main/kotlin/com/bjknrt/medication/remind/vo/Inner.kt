package com.bjknrt.medication.remind.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param id 主键ID 
 * @param drugName 药品名称 可是多种药品名称
 * @param time Time 时间
 * @param status 状态 true:可用,false:禁用
 * @param weeks 星期 周一到周日
 */
data class Inner(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:JsonProperty("drugName", required = true) val drugName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("time", required = true) val time: java.time.LocalTime,
    
    @field:JsonProperty("status", required = true) val status: kotlin.Boolean,
    
    @field:Valid
    @field:JsonProperty("weeks", required = true) val weeks: kotlin.collections.List<Week>
) {

}

