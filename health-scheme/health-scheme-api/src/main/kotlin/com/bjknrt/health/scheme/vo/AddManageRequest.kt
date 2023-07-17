package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * AddManageRequest
 * @param patientId
 * @param isStandard  是否达标
 * @param startDate
 * @param healthManageType
 * @param hypertensionDiseaseTag
 * @param diabetesDiseaseTag
 * @param acuteCoronaryDiseaseTag
 * @param cerebralStrokeDiseaseTag
 * @param copdDiseaseTag
 * @param managementStage
 * @param jobOldId  旧任务ID
 */
data class AddManageRequest(

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:JsonProperty("isStandard", required = true) val isStandard: kotlin.Boolean,

    @field:Valid
    @field:JsonProperty("startDate", required = true) val startDate: java.time.LocalDate,

    @field:Valid
    @field:JsonProperty("healthManageType", required = true) val healthManageType: HealthManageType,

    @field:Valid
    @field:JsonProperty("hypertensionDiseaseTag") val hypertensionDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("diabetesDiseaseTag") val diabetesDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("acuteCoronaryDiseaseTag") val acuteCoronaryDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("cerebralStrokeDiseaseTag") val cerebralStrokeDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("copdDiseaseTag") val copdDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("managementStage") val managementStage: ManageStage? = null,

    @field:JsonProperty("jobOldId") val jobOldId: kotlin.Int? = null
) {

}

