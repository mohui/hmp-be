package com.bjknrt.doctor.patient.management.assembler

import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.doctor.patient.management.DpmDoctorInfo
import com.bjknrt.doctor.patient.management.DpmPatientInfo
import com.bjknrt.doctor.patient.management.util.getFamilyHistoryString
import com.bjknrt.doctor.patient.management.vo.*
import com.bjknrt.framework.enum.BooleanIntEnum
import com.bjknrt.health.indicator.vo.PatientIndicatorResult
import com.bjknrt.question.answering.system.vo.DiseaseOption
import java.time.LocalDate
import java.time.LocalDateTime


/**
 * 注册请求对象转数据库实体
 * @param request 注册请求参数
 * @return 对应数据库实体
 */
fun registerRequestToPatientInfo(request: RegisterRequest): DpmPatientInfo {
    val now = LocalDateTime.now()
    return DpmPatientInfo.builder()
        .setKnId(request.id)
        .setKnName(request.name)
        .setKnGender(request.gender.name)
        .setKnPhone(request.phone)
        .setKnBirthday(request.birthday)
        .setKnIdCard(request.idCard)
        .setKnAuthId(request.id)
        .setKnCreatedBy(request.id)
        .setKnUpdatedBy(request.id)
        .setIsPmhEssentialHypertension(request.pmhEssentialHypertension)
        .setIsPmhTypeTwoDiabetes(request.pmhTypeTwoDiabetes)
        .setIsPmhCerebralInfarction(request.pmhCerebralInfarction)
        .setIsPmhCoronaryHeartDisease(request.pmhCoronaryHeartDisease)
        .setIsPmhCopd(request.pmhCopd)
        .setIsPmhDyslipidemiaHyperlipidemia(request.pmhDyslipidemiaHyperlipidemia)
        .setIsPmhDiabeticNephropathy(request.pmhDiabeticNephropathy)
        .setIsPmhDiabeticRetinopathy(request.pmhDiabeticRetinopathy)
        .setIsPmhDiabeticFoot(request.pmhDiabeticFoot)
        .setKnHypertensionDiseaseTag(request.pmhEssentialHypertension?.let { if (it) PatientTag.EXISTS.name else null })
        .setKnDiabetesDiseaseTag(request.pmhTypeTwoDiabetes?.let { if (it) PatientTag.EXISTS.name else null })
        .setKnAcuteCoronaryDiseaseTag(request.pmhCoronaryHeartDisease?.let { if (it) PatientTag.EXISTS.name else null })
        .setKnCerebralStrokeDiseaseTag(request.pmhCerebralInfarction?.let { if (it) PatientTag.EXISTS.name else null })
        .setKnCopdDiseaseTag(request.pmhCopd?.let { if (it) PatientTag.EXISTS.name else null })
        .setKnCreatedAt(now)
        .setKnUpdatedAt(now)
        .setKnDel(BooleanIntEnum.FALSE.value)
        .setIsSevenDaysNotLogin(false)
        .build()
}

/**
 * 注册请求对象到旧实体中
 * @param request 请求参数
 * @param oldPatientInfo 查询出的旧对象
 * @return 返回更新字段后的旧对象
 */
fun registerRequestToOldPatientInfo(
    request: RegisterRequest,
    oldPatientInfo: DpmPatientInfo,
): DpmPatientInfo {
    oldPatientInfo.knName = request.name
    oldPatientInfo.knGender = request.gender.name
    oldPatientInfo.knPhone = request.phone
    oldPatientInfo.knBirthday = request.birthday
    oldPatientInfo.knIdCard = request.idCard
    oldPatientInfo.knAuthId = request.id
    oldPatientInfo.knCreatedBy = request.id
    oldPatientInfo.knUpdatedBy = request.id
    oldPatientInfo.knUpdatedAt = LocalDateTime.now()
    oldPatientInfo.isPmhEssentialHypertension = request.pmhEssentialHypertension
    oldPatientInfo.isPmhTypeTwoDiabetes = request.pmhTypeTwoDiabetes
    oldPatientInfo.isPmhCerebralInfarction = request.pmhCerebralInfarction
    oldPatientInfo.isPmhCoronaryHeartDisease = request.pmhCoronaryHeartDisease
    oldPatientInfo.isPmhCopd = request.pmhCopd
    oldPatientInfo.isPmhDyslipidemiaHyperlipidemia = request.pmhDyslipidemiaHyperlipidemia
    oldPatientInfo.isPmhDiabeticNephropathy = request.pmhDiabeticNephropathy
    oldPatientInfo.isPmhDiabeticRetinopathy = request.pmhDiabeticRetinopathy
    oldPatientInfo.isPmhDiabeticFoot = request.pmhDiabeticFoot
    oldPatientInfo.knHypertensionDiseaseTag =
        request.pmhEssentialHypertension?.let { hy -> if (hy) PatientTag.EXISTS.name else null }
    oldPatientInfo.knDiabetesDiseaseTag =
        request.pmhTypeTwoDiabetes?.let { dia -> if (dia) PatientTag.EXISTS.name else null }
    oldPatientInfo.knAcuteCoronaryDiseaseTag =
        request.pmhCoronaryHeartDisease?.let { ch -> if (ch) PatientTag.EXISTS.name else null }
    oldPatientInfo.knCerebralStrokeDiseaseTag =
        request.pmhCerebralInfarction?.let { ci -> if (ci) PatientTag.EXISTS.name else null }
    oldPatientInfo.knCopdDiseaseTag = request.pmhCopd?.let { copd -> if (copd) PatientTag.EXISTS.name else null }
    return oldPatientInfo
}

/**
 * 修改请求对象拷贝属性到数据库实体
 * @param request 修改请求对象
 * @param info 患者数据数据库实体
 */
fun editRequestCopyToPatientInfo(request: EditRequest, info: DpmPatientInfo) {
    info.knId = request.id
    info.knUpdatedAt = LocalDateTime.now()
    request.name?.let { info.knName = it }
    request.gender?.let { info.knGender = it.value }
    request.idCard?.let { info.knIdCard = it }
    request.phone?.let { info.knPhone = it }
    request.provincialCode?.let { info.knProvincialCode = it }
    request.municipalCode?.let { info.knMunicipalCode = it }
    request.countyCode?.let { info.knCountyCode = it }
    request.familyHistory?.let { info.knFamilyHistory = getFamilyHistoryString(it) }
    request.symptom?.let { info.knSymptom = it.joinToString() }
    request.smoking?.let { info.knSmoking = if (it) BooleanIntEnum.TRUE.value else BooleanIntEnum.FALSE.value }
    request.drinkWine?.let { info.knDrinkWine = if (it) BooleanIntEnum.TRUE.value else BooleanIntEnum.FALSE.value }
    request.sports?.let { info.knSports = if (it) BooleanIntEnum.TRUE.value else BooleanIntEnum.FALSE.value }
    request.saltIntake?.let { info.knSaltIntake = it }
    request.birthday?.let { info.knBirthday = it }
    request.address?.let { info.knAddress = it }
    request.synthesisDiseaseTag?.let { info.knSynthesisDiseaseTag = it.value }
    request.hypertensionDiseaseTag?.let { info.knHypertensionDiseaseTag = it.value }
    request.diabetesDiseaseTag?.let { info.knDiabetesDiseaseTag = it.value }
    request.acuteCoronaryDiseaseTag?.let { info.knAcuteCoronaryDiseaseTag = it.value }
    request.cerebralStrokeDiseaseTag?.let { info.knCerebralStrokeDiseaseTag = it.value }
    request.copdDiseaseTag?.let { info.knCopdDiseaseTag = it.value }
    request.townshipCode?.let { info.knTownshipCode = it }
    info.isPmhEssentialHypertension = request.pmhEssentialHypertension
    info.isPmhTypeTwoDiabetes = request.pmhTypeTwoDiabetes
    info.isPmhCerebralInfarction = request.pmhCerebralInfarction
    info.isPmhCoronaryHeartDisease = request.pmhCoronaryHeartDisease
    info.isPmhCopd = request.pmhCopd
    info.knHypertensionDiseaseTag = request.pmhEssentialHypertension?.let { if (it) PatientTag.EXISTS.name else null }
    info.knDiabetesDiseaseTag = request.pmhTypeTwoDiabetes?.let { if (it) PatientTag.EXISTS.name else null }
    info.knCerebralStrokeDiseaseTag = request.pmhCerebralInfarction?.let { if (it) PatientTag.EXISTS.name else null }
    info.knAcuteCoronaryDiseaseTag = request.pmhCoronaryHeartDisease?.let { if (it) PatientTag.EXISTS.name else null }
    info.knCopdDiseaseTag = request.pmhCopd?.let { if (it) PatientTag.EXISTS.name else null }
    request.pmhDyslipidemiaHyperlipidemia?.let { info.isPmhDyslipidemiaHyperlipidemia = it }
    request.pmhDiabeticNephropathy?.let { info.isPmhDiabeticNephropathy = it }
    request.pmhDiabeticRetinopathy?.let { info.isPmhDiabeticRetinopathy = it }
    request.pmhDiabeticFoot?.let { info.isPmhDiabeticFoot = it }

}


fun patientInfoToPatientResponse(
    patientInfo: DpmPatientInfo,
    doctorInfo: DpmDoctorInfo?,
    indicator: PatientIndicatorResult,
    diseaseOption: DiseaseOption,
    bindDateTime: LocalDateTime?,
    servicePackageUseDays: Long?
): PatientInfoResponse {
    return PatientInfoResponse(
        id = patientInfo.knId,
        name = patientInfo.knName,
        gender = Gender.valueOf(patientInfo.knGender),
        phone = patientInfo.knPhone,
        idCard = patientInfo.knIdCard,
        birthday = patientInfo.knBirthday,
        age = LocalDateTimeUtil.betweenPeriod(patientInfo.knBirthday.toLocalDate(), LocalDate.now()).years,
        provincialCode = patientInfo.knProvincialCode,
        municipalCode = patientInfo.knMunicipalCode,
        countyCode = patientInfo.knCountyCode,
        townshipCode = patientInfo.knTownshipCode,
        regionAddress = patientInfo.knRegionAddress,
        address = patientInfo.knAddress,
        synthesisDiseaseTag = patientInfo.knSynthesisDiseaseTag?.let { PatientSynthesisTag.valueOf(it) },
        hypertensionDiseaseTag = patientInfo.knHypertensionDiseaseTag?.let { PatientTag.valueOf(it) },
        diabetesDiseaseTag = patientInfo.knDiabetesDiseaseTag?.let { PatientTag.valueOf(it) },
        acuteCoronaryDiseaseTag = patientInfo.knAcuteCoronaryDiseaseTag?.let { PatientTag.valueOf(it) },
        cerebralStrokeDiseaseTag = patientInfo.knCerebralStrokeDiseaseTag?.let { PatientTag.valueOf(it) },
        copdDiseaseTag = patientInfo.knCopdDiseaseTag?.let { PatientTag.valueOf(it) },
        height = indicator.knBodyHeight,
        weight = indicator.knBodyWeight,
        waistline = indicator.knWaistline,
        fastingBloodSugar = indicator.knFastingBloodSandalwood,
        diastolicBloodPressure = indicator.knDiastolicBloodPressure,
        systolicPressure = indicator.knSystolicBloodPressure,
        serumTch = indicator.knTotalCholesterol,
        heightDensityProteinTch = indicator.knHighDensityLipoprotein,
        lowDensityProteinTch = indicator.knLowDensityLipoprotein,
        smoking = indicator.knNum?.let { it != 0 },
        drinkWine = patientInfo.knDrinkWine?.let { it == BooleanIntEnum.TRUE.value },
        sports = patientInfo.knSports?.let { it == BooleanIntEnum.TRUE.value },
        saltIntake = patientInfo.knSaltIntake,
        pmhEssentialHypertension = patientInfo.isPmhEssentialHypertension,
        pmhTypeTwoDiabetes = patientInfo.isPmhTypeTwoDiabetes,
        pmhCerebralInfarction = patientInfo.isPmhCerebralInfarction,
        pmhCoronaryHeartDisease = patientInfo.isPmhCoronaryHeartDisease,
        pmhCopd = patientInfo.isPmhCopd,
        pmhDyslipidemiaHyperlipidemia = patientInfo.isPmhDyslipidemiaHyperlipidemia,
        pmhDiabeticNephropathy = patientInfo.isPmhDiabeticNephropathy,
        pmhDiabeticRetinopathy = patientInfo.isPmhDiabeticRetinopathy,
        pmhDiabeticFoot = patientInfo.isPmhDiabeticFoot,
        fhEssentialHypertension = diseaseOption.fhEssentialHypertension,
        fhTypeTwoDiabetes = diseaseOption.fhTypeTwoDiabetes,
        fhCerebralInfarction = diseaseOption.fhCerebralInfarction,
        fhCoronaryHeartDisease = diseaseOption.fhCoronaryHeartDisease,
        fhCopd = diseaseOption.fhCopd,
        symptomDizzy = diseaseOption.symptomDizzy,
        symptomChestPain = diseaseOption.symptomChestPain,
        symptomChronicCough = diseaseOption.symptomChronicCough,
        symptomWeightLoss = diseaseOption.symptomWeightLoss,
        symptomGiddiness = diseaseOption.symptomGiddiness,
        symptomNone = diseaseOption.symptomNone,
        doctor = doctorInfo?.let { dmpDoctorInfoToDoctorInfo(it) },
        bindDateTime = bindDateTime,
        servicePackageUseDays = servicePackageUseDays
    )
}

fun dmpDoctorInfoToDoctorInfo(doctorInfo: DpmDoctorInfo): DoctorInfo {
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
