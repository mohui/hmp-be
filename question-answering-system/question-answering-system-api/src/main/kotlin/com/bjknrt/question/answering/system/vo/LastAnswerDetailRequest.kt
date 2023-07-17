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
 * LastAnswerDetailRequest
 * @param examinationPaperCode  问卷唯一标识
 * @param answerBy  
 * @param startDate  
 * @param endDate  
 */
data class LastAnswerDetailRequest(
    
    @field:JsonProperty("examinationPaperCode", required = true) val examinationPaperCode: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("answerBy", required = true) val answerBy: java.math.BigInteger,

    @field:JsonProperty("startDate") val startDate: java.time.LocalDateTime? = null,

    @field:JsonProperty("endDate") val endDate: java.time.LocalDateTime? = null
) {

}

