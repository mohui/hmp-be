package com.bjknrt.medication.guide.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param name 通用名名称
 * @param level 禁忌等级
 * @param content 禁忌描述
 * @param contraindication 禁忌编号
 * @param crowd 禁忌人群
 * @param id 通用名编号
 * @param dayLow 年龄（天）-小
 * @param dayHigh 年龄（天）-大
 * @param weightLow  体重（kg）-小
 * @param weightHigh  体重（kg）-大
 * @param doseageForm 药品剂型
 * @param strength 药品规格
 */
data class SubmitGeneric200ResponseInner(
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:JsonProperty("level", required = true) val level: kotlin.Int,
    
    @field:JsonProperty("content", required = true) val content: kotlin.String,
    
    @field:JsonProperty("contraindication", required = true) val contraindication: kotlin.Long,
    
    @field:Valid
    @field:JsonProperty("crowd", required = true) val crowd: Crowd,

    @field:JsonProperty("id") val id: kotlin.Long? = null,

    @field:JsonProperty("dayLow") val dayLow: kotlin.Int? = null,

    @field:JsonProperty("dayHigh") val dayHigh: kotlin.Int? = null,

    @field:JsonProperty("weightLow") val weightLow: java.math.BigDecimal? = null,

    @field:JsonProperty("weightHigh") val weightHigh: java.math.BigDecimal? = null,

    @field:JsonProperty("doseageForm") val doseageForm: kotlin.String? = null,

    @field:JsonProperty("strength") val strength: kotlin.String? = null
) {

}

