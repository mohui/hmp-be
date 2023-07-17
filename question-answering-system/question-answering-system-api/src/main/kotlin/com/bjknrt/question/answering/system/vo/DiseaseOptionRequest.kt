package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * DiseaseOptionRequest
 * @param patientId
 * @param startDate
 * @param endDate
 */
data class DiseaseOptionRequest(

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:JsonProperty("startDate", required = true) val startDate: java.time.LocalDateTime,

    @field:JsonProperty("endDate", required = true) val endDate: java.time.LocalDateTime
) {

}

