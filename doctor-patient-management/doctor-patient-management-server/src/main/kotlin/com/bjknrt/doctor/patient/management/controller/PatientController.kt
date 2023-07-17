package com.bjknrt.doctor.patient.management.controller

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.service.PatientService
import com.bjknrt.doctor.patient.management.vo.*
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.MsgException
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.security.client.AppSecurityUtil
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController("com.bjknrt.doctor.patient.management.server.api.PatientController")
class PatientController(
    val patientService: PatientService
) : AppBaseController(), PatientApi {

    override fun edit(editRequest: EditRequest) {
        patientService.edit(editRequest)
    }

    override fun getInfo(): PatientInfoResponse {
        return AppSecurityUtil.executeWithLogin {
            patientService.getPatientInfo(it.userId)
        } ?: throw MsgException(AppSpringUtil.getMessage("user.not-login"))
    }

    override fun getPatientInfo(body: BigInteger): PatientInfoResponse {
        return patientService.getPatientInfo(body)
    }


    override fun page(patientPageRequest: PatientPageRequest): PagedResult<PatientPageResult> {
        return patientService.page(patientPageRequest)
    }

    override fun register(registerRequest: RegisterRequest) {
        patientService.register(registerRequest)
    }


}
