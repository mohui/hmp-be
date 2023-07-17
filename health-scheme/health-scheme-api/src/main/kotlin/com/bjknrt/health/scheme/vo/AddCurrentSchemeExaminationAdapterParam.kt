package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.bjknrt.health.scheme.vo.ExaminationPaperOption
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
 * AddCurrentSchemeExaminationAdapterParam
 * @param knPatientId  
 * @param knExaminationPaperCode  问卷Code
 * @param knExaminationPaperOptionList  选项集合
 */
data class AddCurrentSchemeExaminationAdapterParam(
    
    @field:Valid
    @field:JsonProperty("knPatientId", required = true) val knPatientId: Id,
    
    @field:JsonProperty("knExaminationPaperCode", required = true) val knExaminationPaperCode: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("knExaminationPaperOptionList", required = true) val knExaminationPaperOptionList: kotlin.collections.List<ExaminationPaperOption>
) {

}

