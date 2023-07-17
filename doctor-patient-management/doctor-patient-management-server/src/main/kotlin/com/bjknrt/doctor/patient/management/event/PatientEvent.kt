package com.bjknrt.doctor.patient.management.event

import com.bjknrt.doctor.patient.management.DpmPatientInfo
import com.bjknrt.doctor.patient.management.vo.EditRequest
import org.springframework.context.ApplicationEvent

class PatientEvent(
    provider: Any,
    val status: PatientEventStatus,
    val patientInfo: DpmPatientInfo,
    val patientEditRequest: EditRequest?
) : ApplicationEvent(provider) {
}

enum class PatientEventStatus {
    /**
     * 修改
     */
    MODIFY,

    /**
     * 注册
     */
    REGISTER
}
