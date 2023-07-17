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
 * QuestionImageInfo
 * @param id  
 * @param questionsId  
 * @param imageUrl  图片URL
 */
data class QuestionImageInfo(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("questionsId", required = true) val questionsId: java.math.BigInteger,
    
    @field:JsonProperty("imageUrl", required = true) val imageUrl: kotlin.String
) {

}

