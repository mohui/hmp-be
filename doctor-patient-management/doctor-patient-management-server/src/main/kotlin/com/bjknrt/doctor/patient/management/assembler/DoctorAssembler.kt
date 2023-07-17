package com.bjknrt.doctor.patient.management.assembler

import com.bjknrt.doctor.patient.management.DpmDoctorInfo
import com.bjknrt.doctor.patient.management.vo.*
import com.bjknrt.framework.enum.BooleanIntEnum
import com.bjknrt.security.client.AppSecurityUtil
import java.time.LocalDateTime


fun doctorInfoToDoctorInfo(doctorInfo: DpmDoctorInfo): DoctorInfo {
    return DoctorInfo(
        id = doctorInfo.knId,
        name = doctorInfo.knName,
        gender = Gender.valueOf(doctorInfo.knGender),
        phone = doctorInfo.knPhone,
        deptName = doctorInfo.knDeptName,
        hospitalId = doctorInfo.knHospitalId,
        hospitalName = doctorInfo.knHospitalName,
        address = doctorInfo.knAddress,
        doctorLevel = DoctorLevel.valueOf(doctorInfo.knDoctorLevel)
    )
}

fun doctorRequestToDoctorInfo(request: DoctorAddRequest): DpmDoctorInfo {
    val userId = AppSecurityUtil.currentUserIdWithDefault()
    return DpmDoctorInfo.builder()
        .setKnId(request.authId)
        .setKnName(request.name)
        .setKnGender(request.gender.name)
        .setKnPhone(request.phone)
        .setKnDeptName(request.deptName)
        .setKnHospitalId(request.hospitalId)
        .setKnHospitalName(request.hospitalName)
        .setKnAddress(request.address)
        .setKnAuthId(request.authId)
        .setKnCreatedBy(userId)
        .setKnUpdatedBy(userId)
        .setKnUpdatedAt(LocalDateTime.now())
        .setKnDoctorLevel(request.doctorLevel.name)
        .setKnDel(BooleanIntEnum.FALSE.value)
        .build()
}

fun doctorEditRequestCopyToDoctorInfo(request: DoctorEditRequest, doctorInfo: DpmDoctorInfo) {
    doctorInfo.knName = request.name
    doctorInfo.knGender = request.gender.value
    doctorInfo.knDeptName = request.deptName
    doctorInfo.knAddress = request.address
    doctorInfo.knPhone = request.phone
    doctorInfo.knDoctorLevel = request.doctorLevel.value
}
