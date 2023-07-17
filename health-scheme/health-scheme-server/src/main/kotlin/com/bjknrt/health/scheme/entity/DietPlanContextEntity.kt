package com.bjknrt.health.scheme.entity

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

data class DietPlanContextEntity(

    @field:JsonProperty("title", required = true) val title: kotlin.String,

    @field:JsonProperty("context", required = true) val context: kotlin.String,

    @field:Valid
    @field:JsonProperty("pictures") val pictures: kotlin.collections.List<PictureEntity>? = listOf()
) {

}
