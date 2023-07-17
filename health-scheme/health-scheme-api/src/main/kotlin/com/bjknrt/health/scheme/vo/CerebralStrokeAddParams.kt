package com.bjknrt.health.scheme.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * cerebralStrokeAddParams
 * @param patientId  
 * @param signsSbp  血压 - 收缩压. 单位: mmHg
 * @param signsDbp  血压 - 舒张压. 单位: mmHg
 * @param patientName  患者姓名
 * @param doctorId  
 * @param doctorName  随访医生姓名
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
 * @param symptomOther  其它
 * @param signsHeight  身高. 单位: cm
 * @param signsWeight  体重. 单位: kg
 * @param signsHeartRate  心率. 单位: 次/分钟
 * @param signsBim  体质指数. 单位: kg/m²
 * @param signsOther  体征 - 其他
 * @param lifeCigarettesPerDay  日吸烟量. 单位: 支
 * @param lifeAlcoholPerDay  日饮酒量. 单位: 两
 * @param lifeSportPerWeek  每周运动次数. 单位: 次
 * @param lifeSportPerTime  每次运动时间. 单位: 分钟
 * @param lifeSaltSituation  
 * @param lifeMentalAdjustment  
 * @param lifeFollowMedicalAdvice  
 * @param abbreviationsAboutExamination  辅助检查
 * @param drugCompliance  
 * @param isAdr  有无药物不良反应
 * @param adrDesc  药物不良反应描述
 * @param visitClass  
 * @param isReferral  是否转诊
 * @param referralReason  转诊原因
 * @param referralAgencies  转诊机构及科别
 * @param nextVisit  
 * @param drugs  
 * @param recommendWeight  建议体重
 * @param recommendBim  建议体质指数
 * @param recommendCigarettesPer  建议日吸烟量
 * @param recommendAlcoholPer  建议日饮酒量
 * @param recommendSportPerWeek  建议每周运动次数
 * @param recommendSportPerTime  建议每次运动时间
 * @param recommendSaltSituation  
 */
data class CerebralStrokeAddParams(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("signsSbp", required = true) val signsSbp: java.math.BigDecimal,
    
    @field:JsonProperty("signsDbp", required = true) val signsDbp: java.math.BigDecimal,

    @field:JsonProperty("patientName") val patientName: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("doctorId") val doctorId: Id? = null,

    @field:JsonProperty("doctorName") val doctorName: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("followWay") val followWay: FllowWay? = null,

    @field:JsonProperty("followDate") val followDate: java.time.LocalDateTime? = null,

    @field:JsonProperty("isSymptomNone") val isSymptomNone: kotlin.Boolean? = null,

    @field:JsonProperty("isSymptomDizzinessHeadache") val isSymptomDizzinessHeadache: kotlin.Boolean? = null,

    @field:JsonProperty("isSymptomNauseaVomiting") val isSymptomNauseaVomiting: kotlin.Boolean? = null,

    @field:JsonProperty("isSymptomBlurredTinnitus") val isSymptomBlurredTinnitus: kotlin.Boolean? = null,

    @field:JsonProperty("isSymptomBreathing") val isSymptomBreathing: kotlin.Boolean? = null,

    @field:JsonProperty("isSymptomPalpitationsToc") val isSymptomPalpitationsToc: kotlin.Boolean? = null,

    @field:JsonProperty("isSymptomNoseBleed") val isSymptomNoseBleed: kotlin.Boolean? = null,

    @field:JsonProperty("isSymptomNumbness") val isSymptomNumbness: kotlin.Boolean? = null,

    @field:JsonProperty("isSymptomLowerExtremityEdema") val isSymptomLowerExtremityEdema: kotlin.Boolean? = null,

    @field:JsonProperty("symptomOther") val symptomOther: kotlin.String? = null,

    @field:JsonProperty("signsHeight") val signsHeight: java.math.BigDecimal? = null,

    @field:JsonProperty("signsWeight") val signsWeight: java.math.BigDecimal? = null,

    @field:JsonProperty("signsHeartRate") val signsHeartRate: java.math.BigDecimal? = null,

    @field:JsonProperty("signsBim") val signsBim: java.math.BigDecimal? = null,

    @field:JsonProperty("signsOther") val signsOther: kotlin.String? = null,

    @field:JsonProperty("lifeCigarettesPerDay") val lifeCigarettesPerDay: java.math.BigDecimal? = null,

    @field:JsonProperty("lifeAlcoholPerDay") val lifeAlcoholPerDay: java.math.BigDecimal? = null,

    @field:JsonProperty("lifeSportPerWeek") val lifeSportPerWeek: java.math.BigDecimal? = null,

    @field:JsonProperty("lifeSportPerTime") val lifeSportPerTime: java.math.BigDecimal? = null,

    @field:Valid
    @field:JsonProperty("lifeSaltSituation") val lifeSaltSituation: LifeSalt? = null,

    @field:Valid
    @field:JsonProperty("lifeMentalAdjustment") val lifeMentalAdjustment: LifeState? = null,

    @field:Valid
    @field:JsonProperty("lifeFollowMedicalAdvice") val lifeFollowMedicalAdvice: LifeState? = null,

    @field:JsonProperty("abbreviationsAboutExamination") val abbreviationsAboutExamination: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("drugCompliance") val drugCompliance: DrugCompliance? = null,

    @field:JsonProperty("isAdr") val isAdr: kotlin.Boolean? = null,

    @field:JsonProperty("adrDesc") val adrDesc: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("visitClass") val visitClass: VisitClass? = null,

    @field:JsonProperty("isReferral") val isReferral: kotlin.Boolean? = null,

    @field:JsonProperty("referralReason") val referralReason: kotlin.String? = null,

    @field:JsonProperty("referralAgencies") val referralAgencies: kotlin.String? = null,

    @field:JsonProperty("nextVisit") val nextVisit: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("drugs") val drugs: kotlin.collections.List<DrugsInner>? = null,

    @field:JsonProperty("recommendWeight") val recommendWeight: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendBim") val recommendBim: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendCigarettesPer") val recommendCigarettesPer: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendAlcoholPer") val recommendAlcoholPer: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendSportPerWeek") val recommendSportPerWeek: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendSportPerTime") val recommendSportPerTime: java.math.BigDecimal? = null,

    @field:Valid
    @field:JsonProperty("recommendSaltSituation") val recommendSaltSituation: LifeSalt? = null
) {

}

