package com.bjknrt.health.scheme.transfer

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.HsCerebralStrokeVisit
import com.bjknrt.health.scheme.vo.CerebralStrokeVisit
import com.bjknrt.health.scheme.vo.CerebralStrokeVisitInfo
import com.bjknrt.health.scheme.vo.FllowWay
import com.bjknrt.health.scheme.vo.PreventionDrug
import com.bjknrt.security.client.AppSecurityUtil


fun toHsCerebralStrokeVisit(cerebralStrokeVisit: CerebralStrokeVisit): HsCerebralStrokeVisit {
    return HsCerebralStrokeVisit.builder()
        .setId(AppIdUtil.nextId())
        .setPatientId(cerebralStrokeVisit.patientId)
        .setPatientName(cerebralStrokeVisit.patientName)
        .setFollowWay(cerebralStrokeVisit.followWay.name)
        .setFollowDate(cerebralStrokeVisit.followDate)
        .setMrsScore(cerebralStrokeVisit.mrsScore)
        .setBarthelScore(cerebralStrokeVisit.barthelScore)
        .setEq5dScore(cerebralStrokeVisit.eq5dScore)
        .setPreventionDrug(cerebralStrokeVisit.preventionDrug.name)
        .setIsLipidLoweringTherapy(cerebralStrokeVisit.isLipidLoweringTherapy)
        .setIsRegularMedication(cerebralStrokeVisit.isRegularMedication)
        .setIsHighRiskFactorsStandard(cerebralStrokeVisit.isHighRiskFactorsStandard)
        .setIsPainCauseHead(cerebralStrokeVisit.isPainCauseHead)
        .setIsPainCauseArthrosis(cerebralStrokeVisit.isPainCauseArthrosis)
        .setIsPainCauseShoulder(cerebralStrokeVisit.isPainCauseShoulder)
        .setIsPainCauseNerve(cerebralStrokeVisit.isPainCauseNerve)
        .setIsPainCauseNone(cerebralStrokeVisit.isPainCauseNone)
        .setIsFall(cerebralStrokeVisit.isFall)
        .setNormalRecoverDays(cerebralStrokeVisit.normalRecoverDays)
        .setNormalRecoverFrequency(cerebralStrokeVisit.normalRecoverFrequency)
        .setNormalRecoverTime(cerebralStrokeVisit.normalRecoverTime)
        .setIsNormalRecoverSport(cerebralStrokeVisit.isNormalRecoverSport)
        .setIsNormalRecoverWork(cerebralStrokeVisit.isNormalRecoverWork)
        .setIsNormalRecoverAcknowledge(cerebralStrokeVisit.isNormalRecoverAcknowledge)
        .setIsNormalRecoverParole(cerebralStrokeVisit.isNormalRecoverParole)
        .setIsNormalRecoverSwallow(cerebralStrokeVisit.isNormalRecoverSwallow)
        .setIsNormalRecoverNone(cerebralStrokeVisit.isNormalRecoverNone)
        .setIntelligentRecoverDays(cerebralStrokeVisit.intelligentRecoverDays)
        .setIntelligentRecoverFrequency(cerebralStrokeVisit.intelligentRecoverFrequency)
        .setIntelligentRecoverTime(cerebralStrokeVisit.intelligentRecoverTime)
        .setIsIntelligentRecoverNone(cerebralStrokeVisit.isIntelligentRecoverNone)
        .setCreatedBy(AppSecurityUtil.currentUserIdWithDefault())
        .setIsIntelligentRecoverBci(cerebralStrokeVisit.isIntelligentRecoverBci)
        .setIsIntelligentRecoverRobot(cerebralStrokeVisit.isIntelligentRecoverRobot)
        .setIsIntelligentRecoverBalance(cerebralStrokeVisit.isIntelligentRecoverBalance)
        .setIsIntelligentRecoverVirtualReality(cerebralStrokeVisit.isIntelligentRecoverVirtualReality)
        .setIsIntelligentRecoverOther(cerebralStrokeVisit.isIntelligentRecoverOther)
        .setDoctorId(cerebralStrokeVisit.doctorId)
        .build()
}

fun toCerebralStrokeVisitInfo(cerebralStrokeVisit: HsCerebralStrokeVisit): CerebralStrokeVisitInfo {
    return CerebralStrokeVisitInfo(
        id = cerebralStrokeVisit.id,
        createdBy = cerebralStrokeVisit.createdBy,
        createdAt = cerebralStrokeVisit.createdAt,
        patientId = cerebralStrokeVisit.patientId,
        patientName = cerebralStrokeVisit.patientName,
        followWay = FllowWay.valueOf(cerebralStrokeVisit.followWay),
        followDate = cerebralStrokeVisit.followDate,
        mrsScore = cerebralStrokeVisit.mrsScore,
        barthelScore = cerebralStrokeVisit.barthelScore,
        eq5dScore = cerebralStrokeVisit.eq5dScore,
        preventionDrug = PreventionDrug.valueOf(cerebralStrokeVisit.preventionDrug),
        isLipidLoweringTherapy = cerebralStrokeVisit.isLipidLoweringTherapy,
        isRegularMedication = cerebralStrokeVisit.isRegularMedication,
        isHighRiskFactorsStandard = cerebralStrokeVisit.isHighRiskFactorsStandard,
        isPainCauseHead = cerebralStrokeVisit.isPainCauseHead,
        isPainCauseArthrosis = cerebralStrokeVisit.isPainCauseArthrosis,
        isPainCauseShoulder = cerebralStrokeVisit.isPainCauseShoulder,
        isPainCauseNerve = cerebralStrokeVisit.isPainCauseNerve,
        isPainCauseNone = cerebralStrokeVisit.isPainCauseNone,
        isFall = cerebralStrokeVisit.isFall,
        normalRecoverDays = cerebralStrokeVisit.normalRecoverDays,
        normalRecoverFrequency = cerebralStrokeVisit.normalRecoverFrequency,
        normalRecoverTime = cerebralStrokeVisit.normalRecoverTime,
        isNormalRecoverSport = cerebralStrokeVisit.isNormalRecoverSport,
        isNormalRecoverWork = cerebralStrokeVisit.isNormalRecoverWork,
        isNormalRecoverAcknowledge = cerebralStrokeVisit.isNormalRecoverAcknowledge,
        isNormalRecoverParole = cerebralStrokeVisit.isNormalRecoverParole,
        isNormalRecoverSwallow = cerebralStrokeVisit.isNormalRecoverSwallow,
        isNormalRecoverNone = cerebralStrokeVisit.isNormalRecoverNone,
        intelligentRecoverDays = cerebralStrokeVisit.intelligentRecoverDays,
        intelligentRecoverFrequency = cerebralStrokeVisit.intelligentRecoverFrequency,
        intelligentRecoverTime = cerebralStrokeVisit.intelligentRecoverTime,
        isIntelligentRecoverBci = cerebralStrokeVisit.isIntelligentRecoverBci,
        isIntelligentRecoverRobot = cerebralStrokeVisit.isIntelligentRecoverRobot,
        isIntelligentRecoverVirtualReality = cerebralStrokeVisit.isIntelligentRecoverVirtualReality,
        isIntelligentRecoverBalance = cerebralStrokeVisit.isIntelligentRecoverBalance,
        isIntelligentRecoverOther = cerebralStrokeVisit.isIntelligentRecoverOther,
        isIntelligentRecoverNone = cerebralStrokeVisit.isIntelligentRecoverNone,
        doctorId = cerebralStrokeVisit.doctorId,
        doctorName = cerebralStrokeVisit.doctorName
    )
}
