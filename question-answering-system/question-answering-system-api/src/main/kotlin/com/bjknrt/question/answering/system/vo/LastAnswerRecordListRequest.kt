package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * LastAnswerRecordListRequest
 * @param examinationPaperCode  问卷唯一标识
 * @param answerBy
 * @param needNum  查询几条数据
 * @param startDate
 * @param endDate
 */
data class LastAnswerRecordListRequest(

    @field:JsonProperty("examinationPaperCode", required = true) val examinationPaperCode: kotlin.String,

    @field:Valid
    @field:JsonProperty("answerBy", required = true) val answerBy: java.math.BigInteger,

    @field:JsonProperty("needNum", required = true) val needNum: kotlin.Int,

    @field:JsonProperty("startDate") val startDate: java.time.LocalDateTime? = null,

    @field:JsonProperty("endDate") val endDate: java.time.LocalDateTime? = null
) {

}

