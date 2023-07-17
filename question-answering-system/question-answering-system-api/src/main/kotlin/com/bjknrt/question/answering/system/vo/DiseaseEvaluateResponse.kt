package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * DiseaseEvaluateResponse
 * @param patientId
 * @param synthesisDiseaseTag
 * @param hypertensionDiseaseTag
 * @param diabetesDiseaseTag
 * @param acuteCoronaryDiseaseTag
 * @param cerebralStrokeDiseaseTag
 * @param copdDiseaseTag
 * @param evaluateDatetime
 */
data class DiseaseEvaluateResponse(

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("synthesisDiseaseTag", required = true) val synthesisDiseaseTag: PatientSynthesisTag,

    @field:Valid
    @field:JsonProperty("hypertensionDiseaseTag", required = true) val hypertensionDiseaseTag: PatientTag,

    @field:Valid
    @field:JsonProperty("diabetesDiseaseTag", required = true) val diabetesDiseaseTag: PatientTag,

    @field:Valid
    @field:JsonProperty("acuteCoronaryDiseaseTag", required = true) val acuteCoronaryDiseaseTag: PatientTag,

    @field:Valid
    @field:JsonProperty("cerebralStrokeDiseaseTag", required = true) val cerebralStrokeDiseaseTag: PatientTag,

    @field:Valid
    @field:JsonProperty("copdDiseaseTag", required = true) val copdDiseaseTag: PatientTag,

    @field:JsonProperty("evaluateDatetime", required = true) val evaluateDatetime: java.time.LocalDateTime
) {

}

