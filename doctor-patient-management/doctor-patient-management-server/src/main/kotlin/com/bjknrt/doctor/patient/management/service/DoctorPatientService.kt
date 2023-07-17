package com.bjknrt.doctor.patient.management.service

import com.bjknrt.doctor.patient.management.DpmDoctorInfo
import com.bjknrt.doctor.patient.management.vo.BindDoctorRequest
import com.bjknrt.doctor.patient.management.vo.UpdateStatusRequest
import java.math.BigInteger

interface DoctorPatientService {

    fun bindDoctor(request: BindDoctorRequest)
    fun unbindDoctor(patientId: BigInteger)
    fun updateStatus(updateStatusRequest: UpdateStatusRequest)
    fun getBindDoctor(patientId: BigInteger): DpmDoctorInfo?
    fun deleteDoctorPatient(doctorId: BigInteger, patientId: BigInteger? = null)
    fun getBindPatientNum(doctorId: BigInteger): Long
}

