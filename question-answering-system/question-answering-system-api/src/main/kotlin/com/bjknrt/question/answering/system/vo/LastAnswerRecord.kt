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
 * LastAnswerRecord
 * @param id  
 * @param answerBy  
 * @param resultsTag  结果标签
 * @param resultsMsg  结果解读内容
 * @param totalScore  
 * @param createdBy  
 * @param createdAt  
 */
data class LastAnswerRecord(

    @field:Valid
    @field:JsonProperty("id") val id: java.math.BigInteger? = null,

    @field:Valid
    @field:JsonProperty("answerBy") val answerBy: java.math.BigInteger? = null,

    @field:JsonProperty("resultsTag") val resultsTag: kotlin.String? = null,

    @field:JsonProperty("resultsMsg") val resultsMsg: kotlin.String? = null,

    @field:JsonProperty("totalScore") val totalScore: java.math.BigDecimal? = null,

    @field:Valid
    @field:JsonProperty("createdBy") val createdBy: java.math.BigInteger? = null,

    @field:JsonProperty("createdAt") val createdAt: java.time.LocalDateTime? = null
) {

}

