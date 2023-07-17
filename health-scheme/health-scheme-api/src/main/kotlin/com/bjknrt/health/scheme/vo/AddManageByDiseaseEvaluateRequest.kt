package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.bjknrt.health.scheme.vo.PatientSynthesisTag
import com.bjknrt.health.scheme.vo.PatientTag
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
 * AddManageByDiseaseEvaluateRequest
 * @param patientId  
 * @param startDate  
 * @param synthesisDiseaseTag  
 * @param hypertensionDiseaseTag  
 * @param diabetesDiseaseTag  
 * @param acuteCoronaryDiseaseTag  
 * @param cerebralStrokeDiseaseTag  
 * @param copdDiseaseTag  
 */
data class AddManageByDiseaseEvaluateRequest(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("startDate", required = true) val startDate: java.time.LocalDate,

    @field:Valid
    @field:JsonProperty("synthesisDiseaseTag") val synthesisDiseaseTag: PatientSynthesisTag? = null,

    @field:Valid
    @field:JsonProperty("hypertensionDiseaseTag") val hypertensionDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("diabetesDiseaseTag") val diabetesDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("acuteCoronaryDiseaseTag") val acuteCoronaryDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("cerebralStrokeDiseaseTag") val cerebralStrokeDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("copdDiseaseTag") val copdDiseaseTag: PatientTag? = null
) {

}

