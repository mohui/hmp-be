package com.bjknrt.doctor.patient.management.service

import com.bjknrt.doctor.patient.management.vo.DoctorAddRequest
import com.bjknrt.doctor.patient.management.vo.DoctorEditRequest
import com.bjknrt.doctor.patient.management.vo.DoctorInfo
import java.math.BigInteger

interface DoctorService {
    fun addDoctor(request: DoctorAddRequest)
    fun deleteDoctor(id: BigInteger)
    fun editDoctor(request: DoctorEditRequest)
    fun getDoctorInfo(doctorId: BigInteger): DoctorInfo

}
