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
 *  EvaluateResultResponse
 * @param id  
 * @param resultsTag  结果标签
 * @param createdAt  
 * @param totalScore  
 * @param resultsMsg  结果内容解答
 */
data class EvaluateResultResponse(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:JsonProperty("resultsTag", required = true) val resultsTag: kotlin.String,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,

    @field:JsonProperty("totalScore") val totalScore: java.math.BigDecimal? = null,

    @field:JsonProperty("resultsMsg") val resultsMsg: kotlin.String? = null
) {

}

