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
 * EvaluateResultQuestionsOption
 * @param questionsId  
 * @param optionId  
 * @param optionValue  选项结果
 * @param optionCode  选项编码
 * @param optionScore  
 */
data class EvaluateResultQuestionsOption(
    
    @field:Valid
    @field:JsonProperty("questionsId", required = true) val questionsId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("optionId", required = true) val optionId: java.math.BigInteger,
    
    @field:JsonProperty("optionValue", required = true) val optionValue: kotlin.String,

    @field:JsonProperty("optionCode") val optionCode: kotlin.String? = null,

    @field:JsonProperty("optionScore") val optionScore: java.math.BigDecimal? = null
) {

}

