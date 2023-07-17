package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * EvaluateResultRequest
 * @param answerBy  
 * @param examinationPaperId  
 * @param questionsOption  
 */
data class EvaluateResultRequest(
    
    @field:Valid
    @field:JsonProperty("answerBy", required = true) val answerBy: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("examinationPaperId", required = true) val examinationPaperId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("questionsOption", required = true) val questionsOption: kotlin.collections.List<EvaluateResultQuestionsOption>
) {

}

