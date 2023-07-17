package com.bjknrt.question.answering.system.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param examinationPaperId
 * @param examinationPaperCode  问卷表编码
 * @param examinationPaperData
 */
data class ExerciseAddPostRequest(

    @field:Valid
    @field:JsonProperty("examinationPaperId", required = true) val examinationPaperId: Id,

    @field:JsonProperty("examinationPaperCode", required = true) val examinationPaperCode: kotlin.String,

    @field:Valid
    @field:JsonProperty("examinationPaperData", required = true) val examinationPaperData: ExerciseEvaluationInner
) {

}

