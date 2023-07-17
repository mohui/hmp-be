package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * PageAnswerRecordRequest
 * @param examinationPaperCodeList  问卷唯一标识
 * @param pageNo  页码
 * @param pageSize  每页条数
 * @param answerBy
 * @param createdBy
 */
data class PageAnswerRecordRequest(

    @field:JsonProperty("examinationPaperCodeList", required = true) val examinationPaperCodeList: kotlin.collections.List<kotlin.String>,

    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,

    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,

    @field:Valid
    @field:JsonProperty("answerBy") val answerBy: java.math.BigInteger? = null,

    @field:Valid
    @field:JsonProperty("createdBy") val createdBy: java.math.BigInteger? = null
) {

}

