package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * AnswerDetailResponse
 * @param evaluateResult  
 * @param options  
 * @param examinationPaper  
 */
data class AnswerDetailResponse(
    
    @field:Valid
    @field:JsonProperty("evaluateResult", required = true) val evaluateResult: EvaluateResultResponse,
    
    @field:Valid
    @field:JsonProperty("options", required = true) val options: kotlin.collections.List<AnswerResultToQuestionsOption>,
    
    @field:Valid
    @field:JsonProperty("examinationPaper", required = true) val examinationPaper: ExaminationPaper
) {

}

