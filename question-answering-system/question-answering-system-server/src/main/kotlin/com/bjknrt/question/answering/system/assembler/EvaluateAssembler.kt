package com.bjknrt.question.answering.system.assembler

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.question.answering.system.QasDiseaseEvaluate
import com.bjknrt.question.answering.system.vo.*
import com.bjknrt.security.client.AppSecurityUtil
import java.time.LocalDateTime


fun addEvaluateRequestToDiseaseEvaluate(request: DiseaseEvaluateRequest): QasDiseaseEvaluate {
    return QasDiseaseEvaluate.builder()
        .setId(AppIdUtil.nextId())
        .setPatientId(request.patientId)
        .setSynthesisDiseaseTag(PatientSynthesisTag.LOW.name)
        .setHypertensionDiseaseTag(PatientTag.LOW.name)
        .setDiabetesDiseaseTag(PatientTag.LOW.name)
        .setAcuteCoronaryDiseaseTag(PatientTag.LOW.name)
        .setCerebralStrokeDiseaseTag(PatientTag.LOW.name)
        .setCopdDiseaseTag(PatientTag.LOW.name)
        .setPatientHeight(request.patientHeight)
        .setPatientWeight(request.patientWeight)
        .setPatientWaistline(request.patientWaistline)
        .setIsSmoking(request.isSmoking)
        .setIsPmhEssentialHypertension(request.isPmhEssentialHypertension)
        .setIsPmhTypeTwoDiabetes(request.isPmhTypeTwoDiabetes)
        .setIsPmhCerebralInfarction(request.isPmhCerebralInfarction)
        .setIsPmhCoronaryHeartDisease(request.isPmhCoronaryHeartDisease)
        .setIsPmhCopd(request.isPmhCopd)
        .setIsPmhDyslipidemiaHyperlipidemia(request.isPmhDyslipidemiaHyperlipidemia)
        .setIsFhEssentialHypertension(request.isFhEssentialHypertension)
        .setIsFhTypeTwoDiabetes(request.isFhTypeTwoDiabetes)
        .setIsFhCerebralInfarction(request.isFhCerebralInfarction)
        .setIsFhCoronaryHeartDisease(request.isFhCoronaryHeartDisease)
        .setIsFhCopd(request.isFhCopd)
        .setIsSymptomDizzy(request.isSymptomDizzy)
        .setIsSymptomChestPain(request.isSymptomChestPain)
        .setIsSymptomChronicCough(request.isSymptomChronicCough)
        .setIsSymptomWeightLoss(request.isSymptomWeightLoss)
        .setIsSymptomGiddiness(request.isSymptomGiddiness)
        .setIsSymptomNone(request.isSymptomNone)
        .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
        .setCreatedAt(LocalDateTime.now())
        .setFastingBloodSugar(request.fastingBloodSugar)
        .setSystolicPressure(request.systolicPressure)
        .setDiastolicBloodPressure(request.diastolicBloodPressure)
        .setSerumTch(request.serumTch)
        .setIsPmhDiabeticFoot(request.isPmhDiabeticFoot)
        .setIsPmhDiabeticNephropathy(request.isPmhDiabeticNephropathy)
        .setIsPmhDiabeticRetinopathy(request.isPmhDiabeticRetinopathy)
        .build()
}

fun diseaseEvaluateToDiseaseEvaluateResponse(diseaseEvaluate: QasDiseaseEvaluate): DiseaseEvaluateResponse {
    return DiseaseEvaluateResponse(
        patientId = diseaseEvaluate.patientId,
        synthesisDiseaseTag = PatientSynthesisTag.valueOf(diseaseEvaluate.synthesisDiseaseTag),
        hypertensionDiseaseTag = PatientTag.valueOf(diseaseEvaluate.hypertensionDiseaseTag),
        diabetesDiseaseTag = PatientTag.valueOf(diseaseEvaluate.diabetesDiseaseTag),
        acuteCoronaryDiseaseTag = PatientTag.valueOf(diseaseEvaluate.acuteCoronaryDiseaseTag),
        cerebralStrokeDiseaseTag = PatientTag.valueOf(diseaseEvaluate.cerebralStrokeDiseaseTag),
        copdDiseaseTag = PatientTag.valueOf(diseaseEvaluate.copdDiseaseTag),
        evaluateDatetime = diseaseEvaluate.createdAt
    )
}

fun diseaseEvaluateToDiseaseOptionResponse(diseaseEvaluate: QasDiseaseEvaluate): DiseaseOption {
    return DiseaseOption(
        diseaseEvaluate.isSymptomDizzy,
        diseaseEvaluate.isSymptomChestPain,
        diseaseEvaluate.isSymptomChronicCough,
        diseaseEvaluate.isSymptomWeightLoss,
        diseaseEvaluate.isSymptomGiddiness,
        diseaseEvaluate.isSymptomNone,
        diseaseEvaluate.isPmhEssentialHypertension,
        diseaseEvaluate.isPmhTypeTwoDiabetes,
        diseaseEvaluate.isPmhCerebralInfarction,
        diseaseEvaluate.isPmhCoronaryHeartDisease,
        diseaseEvaluate.isPmhCopd,
        diseaseEvaluate.isPmhDyslipidemiaHyperlipidemia,
        diseaseEvaluate.isPmhDiabeticNephropathy,
        diseaseEvaluate.isPmhDiabeticRetinopathy,
        diseaseEvaluate.isPmhDiabeticFoot,
        diseaseEvaluate.isFhEssentialHypertension,
        diseaseEvaluate.isFhTypeTwoDiabetes,
        diseaseEvaluate.isFhCerebralInfarction,
        diseaseEvaluate.isFhCoronaryHeartDisease,
        diseaseEvaluate.isFhCopd,
        diseaseEvaluate.createdAt,

    )
}
