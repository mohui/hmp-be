package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * AnswerRecordListRequest
 * @param examinationPaperCodeList  问卷唯一标识
 * @param answerBy
 * @param startDate
 * @param endDate
 */
data class AnswerRecordListRequest(

    @field:JsonProperty("examinationPaperCodeList", required = true) val examinationPaperCodeList: kotlin.collections.List<kotlin.String>,

    @field:Valid
    @field:JsonProperty("answerBy", required = true) val answerBy: java.math.BigInteger,

    @field:JsonProperty("startDate", required = true) val startDate: java.time.LocalDateTime,

    @field:JsonProperty("endDate", required = true) val endDate: java.time.LocalDateTime
) {

}

