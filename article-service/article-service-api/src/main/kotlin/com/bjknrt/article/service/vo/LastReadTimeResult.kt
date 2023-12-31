package com.bjknrt.article.service.vo

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
 * lastReadTimeResult
 * @param readDateTime  
 */
data class LastReadTimeResult(

    @field:JsonProperty("readDateTime") val readDateTime: java.time.LocalDateTime? = null
) {

}

