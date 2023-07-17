package com.bjknrt.doctor.patient.management.controller

import com.bjknrt.doctor.patient.management.api.DoctorPatientApi
import com.bjknrt.doctor.patient.management.service.DoctorPatientService
import com.bjknrt.doctor.patient.management.service.DoctorService
import com.bjknrt.doctor.patient.management.vo.BindDoctorInfoResponse
import com.bjknrt.doctor.patient.management.vo.BindDoctorRequest
import com.bjknrt.doctor.patient.management.vo.UpdateStatusRequest
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.operation.logsdk.util.OperationLogUtil
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RefreshScope
@RestController("com.bjknrt.doctor.patient.management.server.api.DoctorPatientController")
class DoctorPatientController(
    val doctorPatientService: DoctorPatientService,
    val doctorService: DoctorService
) : AppBaseController(), DoctorPatientApi {

    override fun bindDoctor(bindDoctorRequest: BindDoctorRequest) {
        val doctor = doctorService.getDoctorInfo(bindDoctorRequest.doctorId)
        OperationLogUtil.buzLog(
            module = LogModule.DPM,
            action = LogAction.DPM_PATIENT_BINDING_DOCTOR,
            currentOrgId = doctor.hospitalId
        ) {
            doctorPatientService.bindDoctor(bindDoctorRequest)
        }
    }

    override fun bindPatientNum(body: BigInteger): Long {
       return doctorPatientService.getBindPatientNum(body)
    }

    override fun getBindDoctor(body: BigInteger): BindDoctorInfoResponse {
        val doctor = doctorPatientService.getBindDoctor(body)
            ?: return BindDoctorInfoResponse(
                 patientId = body,
                 doctorId = null,
                 doctorName = null,
                 doctorHospitalId = null,
                 doctorHospitalName = null,
             )
        return BindDoctorInfoResponse(
            patientId = body,
            doctorId = doctor.knId,
            doctorName = doctor.knName,
            doctorHospitalId = doctor.knHospitalId,
            doctorHospitalName = doctor.knHospitalName
        )
    }

    override fun unbindDoctor(body: BigInteger) {
        val doctor = doctorPatientService.getBindDoctor(body)
            ?: throw MsgException(AppSpringUtil.getMessage("doctor-patient.patient-not-bind-doctor"))
        OperationLogUtil.buzLog(
            module = LogModule.DPM,
            action = LogAction.DPM_PATIENT_UN_BINDING_DOCTOR,
            currentOrgId = doctor.knHospitalId
        ) {
            doctorPatientService.unbindDoctor(body)
        }
    }

    override fun updateStatus(updateStatusRequest: UpdateStatusRequest) {
        doctorPatientService.updateStatus(updateStatusRequest)
    }

}
