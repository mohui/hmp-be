package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * BindDoctorRequest
 * @param patientId
 * @param doctorId
 */
data class BindDoctorRequest(

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("doctorId", required = true) val doctorId: java.math.BigInteger
) {

}

