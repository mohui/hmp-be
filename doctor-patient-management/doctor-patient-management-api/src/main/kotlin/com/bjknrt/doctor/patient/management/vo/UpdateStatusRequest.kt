package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * UpdateStatusRequest
 * @param patientId
 * @param status
 */
data class UpdateStatusRequest(

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("status", required = true) val status: ToDoStatus
) {

}

