package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param title  饮食计划标题
 * @param pictures  达标小贴士下的图片
 * @param contexts  文章内容
 */
data class DietPlanArticle(
    
    @field:JsonProperty("title", required = true) val title: kotlin.String,

    @field:Valid
    @field:JsonProperty("pictures") val pictures: kotlin.collections.List<Picture>? = null,

    @field:Valid
    @field:JsonProperty("contexts") val contexts: kotlin.collections.List<DietPlanContext>? = null
) {

}

