package com.bjknrt.health.scheme.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param patientId  
 * @param patientName  患者姓名
 * @param followWay  
 * @param followDate  
 * @param isSymptomNone  无症状
 * @param isSymptomPains  心慌
 * @param isSymptomDizziness  头晕
 * @param isSymptomNausea  恶心
 * @param isSymptomPalpitationsToc  胸口处压榨性疼痛或憋闷感或紧缩感
 * @param isSymptomThroatTight  颈部或喉咙感觉发紧
 * @param signsSbp  
 * @param signsDbp  
 * @param signsWeight  
 * @param signsHeartRate  
 * @param lifeCigarettesPerDay  日吸烟量. 单位: 支
 * @param lifeAlcoholPerDay  
 * @param lifeSportPerWeek  每周运动次数. 单位: 次
 * @param lifeSportPerTime  
 * @param lifeSaltSituation  
 * @param lifeFollowMedicalAdvice  
 * @param drugCompliance  
 * @param id  
 * @param doctorId  
 * @param doctorName  随访医生姓名
 * @param symptomOther  其它
 * @param signsHeight  
 * @param recommendWeight  
 * @param signsBim  
 * @param recommendBim  
 * @param recommendCigarettesPer  建议日吸烟量
 * @param recommendAlcoholPer  
 * @param recommendSportPerWeek  建议每周运动次数
 * @param recommendSportPerTime  
 * @param recommendSaltSituation  
 * @param lifeMentalAdjustment  
 * @param abbreviationsAboutExamination  辅助检查
 * @param isDrugNone  有无药物不良反应
 * @param isDrugMyalgia  肌肉痛
 * @param isDrugMuscleWeakness  肌肉无力
 * @param isDrugSkinYellowness  眼白或皮肤发黄
 * @param drugOther  其他: 药物不良反应描述
 * @param isBleedNone  无:出血
 * @param isBleedNose  鼻出血
 * @param isBleedGums  牙龈出血
 * @param isBleedShit  多次黑褐色大便
 * @param bleedOther  其他: 描述
 * @param visitClass  
 * @param isReferral  是否转诊
 * @param referralReason  转诊原因
 * @param referralAgencies  转诊机构及科别
 * @param nextVisit  
 * @param createdBy  
 * @param createdAt  
 * @param updatedBy  
 * @param updatedAt  
 * @param drugs  
 */
data class AcuteCoronaryVisit(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("patientName", required = true) val patientName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("followWay", required = true) val followWay: FllowWay,
    
    @field:JsonProperty("followDate", required = true) val followDate: java.time.LocalDateTime,
    
    @field:JsonProperty("isSymptomNone", required = true) val isSymptomNone: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomPains", required = true) val isSymptomPains: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomDizziness", required = true) val isSymptomDizziness: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomNausea", required = true) val isSymptomNausea: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomPalpitationsToc", required = true) val isSymptomPalpitationsToc: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomThroatTight", required = true) val isSymptomThroatTight: kotlin.Boolean,
    
    @field:JsonProperty("signsSbp", required = true) val signsSbp: java.math.BigDecimal,
    
    @field:JsonProperty("signsDbp", required = true) val signsDbp: java.math.BigDecimal,
    
    @field:JsonProperty("signsWeight", required = true) val signsWeight: java.math.BigDecimal,
    
    @field:JsonProperty("signsHeartRate", required = true) val signsHeartRate: java.math.BigDecimal,
    
    @field:JsonProperty("lifeCigarettesPerDay", required = true) val lifeCigarettesPerDay: kotlin.Int,
    
    @field:JsonProperty("lifeAlcoholPerDay", required = true) val lifeAlcoholPerDay: java.math.BigDecimal,
    
    @field:JsonProperty("lifeSportPerWeek", required = true) val lifeSportPerWeek: kotlin.Int,
    
    @field:JsonProperty("lifeSportPerTime", required = true) val lifeSportPerTime: java.math.BigDecimal,
    
    @field:Valid
    @field:JsonProperty("lifeSaltSituation", required = true) val lifeSaltSituation: LifeSalt,
    
    @field:Valid
    @field:JsonProperty("lifeFollowMedicalAdvice", required = true) val lifeFollowMedicalAdvice: LifeState,
    
    @field:Valid
    @field:JsonProperty("drugCompliance", required = true) val drugCompliance: DrugCompliance,

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null,

    @field:Valid
    @field:JsonProperty("doctorId") val doctorId: Id? = null,

    @field:JsonProperty("doctorName") val doctorName: kotlin.String? = null,

    @field:JsonProperty("symptomOther") val symptomOther: kotlin.String? = null,

    @field:JsonProperty("signsHeight") val signsHeight: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendWeight") val recommendWeight: java.math.BigDecimal? = null,

    @field:JsonProperty("signsBim") val signsBim: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendBim") val recommendBim: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendCigarettesPer") val recommendCigarettesPer: kotlin.Int? = null,

    @field:JsonProperty("recommendAlcoholPer") val recommendAlcoholPer: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendSportPerWeek") val recommendSportPerWeek: kotlin.Int? = null,

    @field:JsonProperty("recommendSportPerTime") val recommendSportPerTime: java.math.BigDecimal? = null,

    @field:Valid
    @field:JsonProperty("recommendSaltSituation") val recommendSaltSituation: LifeSalt? = null,

    @field:Valid
    @field:JsonProperty("lifeMentalAdjustment") val lifeMentalAdjustment: LifeState? = null,

    @field:JsonProperty("abbreviationsAboutExamination") val abbreviationsAboutExamination: kotlin.String? = null,

    @field:JsonProperty("isDrugNone") val isDrugNone: kotlin.Boolean? = null,

    @field:JsonProperty("isDrugMyalgia") val isDrugMyalgia: kotlin.Boolean? = null,

    @field:JsonProperty("isDrugMuscleWeakness") val isDrugMuscleWeakness: kotlin.Boolean? = null,

    @field:JsonProperty("isDrugSkinYellowness") val isDrugSkinYellowness: kotlin.Boolean? = null,

    @field:JsonProperty("drugOther") val drugOther: kotlin.String? = null,

    @field:JsonProperty("isBleedNone") val isBleedNone: kotlin.Boolean? = null,

    @field:JsonProperty("isBleedNose") val isBleedNose: kotlin.Boolean? = null,

    @field:JsonProperty("isBleedGums") val isBleedGums: kotlin.Boolean? = null,

    @field:JsonProperty("isBleedShit") val isBleedShit: kotlin.Boolean? = null,

    @field:JsonProperty("bleedOther") val bleedOther: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("visitClass") val visitClass: VisitClass? = null,

    @field:JsonProperty("isReferral") val isReferral: kotlin.Boolean? = null,

    @field:JsonProperty("referralReason") val referralReason: kotlin.String? = null,

    @field:JsonProperty("referralAgencies") val referralAgencies: kotlin.String? = null,

    @field:JsonProperty("nextVisit") val nextVisit: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("createdBy") val createdBy: Id? = null,

    @field:JsonProperty("createdAt") val createdAt: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("updatedBy") val updatedBy: Id? = null,

    @field:JsonProperty("updatedAt") val updatedAt: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("drugs") val drugs: kotlin.collections.List<DrugsInner>? = null
) {

}

