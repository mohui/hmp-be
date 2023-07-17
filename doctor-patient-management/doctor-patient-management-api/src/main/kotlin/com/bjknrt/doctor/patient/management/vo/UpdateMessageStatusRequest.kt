package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * UpdateMessageStatusRequest
 * @param patientId
 * @param doctorId
 * @param patientMessageStatus
 * @param patientMessageNum
 * @param doctorMessageStatus
 * @param doctorMessageNum
 */
data class UpdateMessageStatusRequest(

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("doctorId", required = true) val doctorId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("patientMessageStatus", required = true) val patientMessageStatus: MessageStatus,

    @field:JsonProperty("patientMessageNum", required = true) val patientMessageNum: kotlin.Int,

    @field:JsonProperty("doctorMessageStatus", required = true) val doctorMessageStatus: MessageStatus,

    @field:JsonProperty("doctorMessageNum", required = true) val doctorMessageNum: kotlin.Int
) {

}

