package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * SaveAnswerRecordRequest
 * @param examinationPaperId
 * @param examinationPaperCode  问卷唯一标识
 * @param resultsTag  结果标签
 * @param answerBy
 * @param createdBy
 * @param totalScore
 * @param resultsMsg  结果解读内容
 */
data class SaveAnswerRecordRequest(

    @field:Valid
    @field:JsonProperty("examinationPaperId", required = true) val examinationPaperId: java.math.BigInteger,

    @field:JsonProperty("examinationPaperCode", required = true) val examinationPaperCode: kotlin.String,

    @field:JsonProperty("resultsTag", required = true) val resultsTag: kotlin.String,

    @field:Valid
    @field:JsonProperty("answerBy", required = true) val answerBy: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("createdBy", required = true) val createdBy: java.math.BigInteger,

    @field:JsonProperty("totalScore") val totalScore: java.math.BigDecimal? = null,

    @field:JsonProperty("resultsMsg") val resultsMsg: kotlin.String? = null
) {

}

