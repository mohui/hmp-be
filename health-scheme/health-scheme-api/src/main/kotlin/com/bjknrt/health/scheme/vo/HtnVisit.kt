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
 * @param isSymptomDizzinessHeadache  头痛头晕
 * @param isSymptomNauseaVomiting  恶心呕吐
 * @param isSymptomBlurredTinnitus  眼花耳鸣
 * @param isSymptomBreathing  呼吸困难
 * @param isSymptomPalpitationsToc  心悸胸闷
 * @param isSymptomNoseBleed  鼻衄出血不止
 * @param isSymptomNumbness  四肢发麻
 * @param isSymptomLowerExtremityEdema  下肢水肿
 * @param signsSbp  血压 - 收缩压. 单位: mmHg
 * @param signsDbp  血压 - 舒张压. 单位: mmHg
 * @param signsWeight  体重. 单位: kg
 * @param signsHeartRate  心率. 单位: 次/分钟
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
 * @param signsHeight  身高. 单位: cm
 * @param signsBim  体质指数. 单位: kg/m²
 * @param signsOther  体征 - 其他
 * @param lifeMentalAdjustment  
 * @param abbreviationsAboutExamination  辅助检查
 * @param isAdr  有无药物不良反应
 * @param adrDesc  药物不良反应描述
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
 * @param recommendWeight  
 * @param recommendBim  
 * @param recommendCigarettesPer  建议日吸烟量
 * @param recommendAlcoholPer  
 * @param recommendSportPerWeek  建议每周运动次数
 * @param recommendSportPerTime  
 * @param recommendSaltSituation  
 */
data class HtnVisit(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("patientName", required = true) val patientName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("followWay", required = true) val followWay: FllowWay,
    
    @field:JsonProperty("followDate", required = true) val followDate: java.time.LocalDateTime,
    
    @field:JsonProperty("isSymptomNone", required = true) val isSymptomNone: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomDizzinessHeadache", required = true) val isSymptomDizzinessHeadache: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomNauseaVomiting", required = true) val isSymptomNauseaVomiting: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomBlurredTinnitus", required = true) val isSymptomBlurredTinnitus: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomBreathing", required = true) val isSymptomBreathing: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomPalpitationsToc", required = true) val isSymptomPalpitationsToc: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomNoseBleed", required = true) val isSymptomNoseBleed: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomNumbness", required = true) val isSymptomNumbness: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomLowerExtremityEdema", required = true) val isSymptomLowerExtremityEdema: kotlin.Boolean,
    
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

    @field:JsonProperty("signsBim") val signsBim: java.math.BigDecimal? = null,

    @field:JsonProperty("signsOther") val signsOther: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("lifeMentalAdjustment") val lifeMentalAdjustment: LifeState? = null,

    @field:JsonProperty("abbreviationsAboutExamination") val abbreviationsAboutExamination: kotlin.String? = null,

    @field:JsonProperty("isAdr") val isAdr: kotlin.Boolean? = null,

    @field:JsonProperty("adrDesc") val adrDesc: kotlin.String? = null,

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
    @field:JsonProperty("drugs") val drugs: kotlin.collections.List<DrugsInner>? = null,

    @field:JsonProperty("recommendWeight") val recommendWeight: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendBim") val recommendBim: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendCigarettesPer") val recommendCigarettesPer: kotlin.Int? = null,

    @field:JsonProperty("recommendAlcoholPer") val recommendAlcoholPer: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendSportPerWeek") val recommendSportPerWeek: kotlin.Int? = null,

    @field:JsonProperty("recommendSportPerTime") val recommendSportPerTime: java.math.BigDecimal? = null,

    @field:Valid
    @field:JsonProperty("recommendSaltSituation") val recommendSaltSituation: LifeSalt? = null
) {

}

