package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * BindDoctorInfoResponse
 * @param patientId
 * @param doctorId
 * @param doctorName  医生姓名
 * @param doctorHospitalId
 * @param doctorHospitalName  医生机构名称
 */
data class BindDoctorInfoResponse(

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("doctorId") val doctorId: java.math.BigInteger? = null,

    @field:JsonProperty("doctorName") val doctorName: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("doctorHospitalId") val doctorHospitalId: java.math.BigInteger? = null,

    @field:JsonProperty("doctorHospitalName") val doctorHospitalName: kotlin.String? = null
) {

}

