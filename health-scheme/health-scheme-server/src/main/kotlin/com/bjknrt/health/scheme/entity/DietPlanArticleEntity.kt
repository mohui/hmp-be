package com.bjknrt.health.scheme.entity

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

data class DietPlanArticleEntity(

    // 饮食计划标题
    @field:JsonProperty("title", required = true) val title: kotlin.String,

    // 达标小贴士下的图片
    @field:Valid
    @field:JsonProperty("pictures") val pictures: kotlin.collections.List<PictureEntity>? = listOf(),

    // 文章内容
    @field:Valid
    @field:JsonProperty("contexts") val contexts: kotlin.collections.List<DietPlanContextEntity>? = listOf()
) {

}
