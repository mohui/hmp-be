package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.bjknrt.framework.api.vo.Id
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
 * 
 * @param knAnswerRecordId  
 * @param knAnswerResultId  
 * @param knQuestionsId  
 * @param knOptionId  
 * @param knOptionLabel  选项名称
 * @param knMessage  扩展信息
 */
data class ExaminationPaperOption(
    
    @field:Valid
    @field:JsonProperty("knAnswerRecordId", required = true) val knAnswerRecordId: Id,
    
    @field:Valid
    @field:JsonProperty("knAnswerResultId", required = true) val knAnswerResultId: Id,
    
    @field:Valid
    @field:JsonProperty("knQuestionsId", required = true) val knQuestionsId: Id,
    
    @field:Valid
    @field:JsonProperty("knOptionId", required = true) val knOptionId: Id,

    @field:JsonProperty("knOptionLabel") val knOptionLabel: kotlin.String? = null,

    @field:JsonProperty("knMessage") val knMessage: kotlin.String? = null
) {

}

