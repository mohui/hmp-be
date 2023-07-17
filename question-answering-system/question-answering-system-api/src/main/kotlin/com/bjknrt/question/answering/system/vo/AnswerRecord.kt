package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * AnswerRecord
 * @param id
 * @param answerBy
 * @param examinationPaperId
 * @param examinationPaperCode  问卷唯一标识
 * @param examinationPaperTitle  问卷标题
 * @param examinationPaperDesc  问卷简介
 * @param examinationPaperReference  问卷参考文献
 * @param resultsTag  结果标签
 * @param createdBy
 * @param createdAt
 * @param examinationPaperTip  问卷提示
 * @param resultsMsg  结果解读内容
 * @param totalScore
 */
data class AnswerRecord(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("answerBy", required = true) val answerBy: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("examinationPaperId", required = true) val examinationPaperId: java.math.BigInteger,

    @field:JsonProperty("examinationPaperCode", required = true) val examinationPaperCode: kotlin.String,

    @field:JsonProperty("examinationPaperTitle", required = true) val examinationPaperTitle: kotlin.String,

    @field:JsonProperty("examinationPaperDesc", required = true) val examinationPaperDesc: kotlin.String,

    @field:JsonProperty("examinationPaperReference", required = true) val examinationPaperReference: kotlin.String,

    @field:JsonProperty("resultsTag", required = true) val resultsTag: kotlin.String,

    @field:Valid
    @field:JsonProperty("createdBy", required = true) val createdBy: java.math.BigInteger,

    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,

    @field:JsonProperty("examinationPaperTip") val examinationPaperTip: kotlin.String? = null,

    @field:JsonProperty("resultsMsg") val resultsMsg: kotlin.String? = null,

    @field:JsonProperty("totalScore") val totalScore: java.math.BigDecimal? = null
) {

}

