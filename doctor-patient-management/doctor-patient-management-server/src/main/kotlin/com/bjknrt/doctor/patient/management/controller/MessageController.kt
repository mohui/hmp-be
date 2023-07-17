package com.bjknrt.doctor.patient.management.controller

import com.bjknrt.doctor.patient.management.DpmDoctorPatientTable
import com.bjknrt.doctor.patient.management.api.MessageApi
import com.bjknrt.doctor.patient.management.vo.UpdateMessageStatusRequest
import com.bjknrt.framework.api.AppBaseController
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.doctor.patient.management.api.MessageController")
class MessageController(
    val doctorPatientTable: DpmDoctorPatientTable
) : AppBaseController(), MessageApi {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateMessageStatus(updateMessageStatusRequest: UpdateMessageStatusRequest) {
        doctorPatientTable.update()
            .setKnMessageStatus(updateMessageStatusRequest.doctorMessageStatus.name)
            .setKnMessageNum(updateMessageStatusRequest.doctorMessageNum)
            .where(DpmDoctorPatientTable.KnDoctorId eq updateMessageStatusRequest.doctorId.arg)
            .where(DpmDoctorPatientTable.KnPatientId eq updateMessageStatusRequest.patientId.arg)
            .execute()
            .takeIf { it.toInt() == 0 }
            ?.let {
                doctorPatientTable.update()
                    .setKnMessageStatus(updateMessageStatusRequest.patientMessageStatus.name)
                    .setKnMessageNum(updateMessageStatusRequest.patientMessageNum)
                    .where(DpmDoctorPatientTable.KnDoctorId eq updateMessageStatusRequest.patientId.arg)
                    .where(DpmDoctorPatientTable.KnPatientId eq updateMessageStatusRequest.doctorId.arg)
                    .execute()
            }
    }
}
