package com.bjknrt.question.answering.system.vo

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
 * ImageRequest
 * @param questionsId  
 */
data class ImageRequest(
    
    @field:Valid
    @field:JsonProperty("questionsId", required = true) val questionsId: java.math.BigInteger
) {

}

