package com.bjknrt.health.scheme.vo

import java.util.Objects
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
 * @param url  图片url
 * @param desc  图片描述
 */
data class Picture(

    @field:JsonProperty("url") val url: kotlin.String? = null,

    @field:JsonProperty("desc") val desc: kotlin.String? = null
) {

}

