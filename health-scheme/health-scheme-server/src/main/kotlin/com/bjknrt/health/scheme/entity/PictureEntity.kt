package com.bjknrt.health.scheme.entity

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @param url  图片url
 * @param desc  图片描述
 */
data class PictureEntity(

    @field:JsonProperty("url") val url: kotlin.String? = null,

    @field:JsonProperty("desc") val desc: kotlin.String? = null
) {

}
