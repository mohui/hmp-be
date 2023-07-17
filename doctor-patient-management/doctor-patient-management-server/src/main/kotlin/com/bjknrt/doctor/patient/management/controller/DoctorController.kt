package com.bjknrt.doctor.patient.management.controller

import com.bjknrt.doctor.patient.management.api.DoctorApi
import com.bjknrt.doctor.patient.management.service.DoctorService
import com.bjknrt.doctor.patient.management.vo.*
import com.bjknrt.framework.api.AppBaseController
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RefreshScope
@RestController("com.bjknrt.doctor.patient.management.server.api.DoctorController")
class DoctorController(
    val doctorService: DoctorService
) : AppBaseController(), DoctorApi {

    override fun addDoctor(doctorAddRequest: DoctorAddRequest) {
        doctorService.addDoctor(doctorAddRequest)
    }

    override fun deleteDoctor(body: BigInteger) {
        doctorService.deleteDoctor(body)
    }

    override fun editDoctor(doctorEditRequest: DoctorEditRequest) {
        doctorService.editDoctor(doctorEditRequest)
    }

    override fun getInfo(body: BigInteger): DoctorInfo {
        return doctorService.getDoctorInfo(body)
    }


}
