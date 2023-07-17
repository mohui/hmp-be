package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param title  标题
 * @param context  内容
 * @param pictures  图片
 */
data class DietPlanContext(
    
    @field:JsonProperty("title", required = true) val title: kotlin.String,
    
    @field:JsonProperty("context", required = true) val context: kotlin.String,

    @field:Valid
    @field:JsonProperty("pictures") val pictures: kotlin.collections.List<Picture>? = null
) {

}

