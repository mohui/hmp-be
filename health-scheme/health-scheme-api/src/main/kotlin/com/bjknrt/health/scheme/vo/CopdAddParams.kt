package com.bjknrt.health.scheme.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * copdAddParams
 * @param patientId  
 * @param patientName  患者姓名
 * @param followWay  
 * @param followDate  
 * @param isWheezingEffort  费力才喘
 * @param isWheezingWalkFast  走快会喘
 * @param isWheezingStroll  平路会喘
 * @param isWheezingHundredMeters  百米会喘
 * @param isWheezingLittle  稍动就喘
 * @param isSymptomCough  咳嗽、咳痰比平时多
 * @param isSymptomPurulentSputum  咳脓痰
 * @param isSymptomInappetence  食欲不振
 * @param isSymptomAbdominalDistention  腹胀
 * @param isSymptomBreathing  活动后呼吸困难
 * @param isSymptomSystemicEdema  下肢或全身浮肿
 * @param isSymptomNone  无以上症状
 * @param pulseOxygenSaturation  
 * @param pulse  
 * @param signsSbp  
 * @param signsDbp  
 * @param signsHeight  
 * @param signsWeight  
 * @param isCigarettesPer  不吸烟
 * @param lifeCigarettesPerDay  日吸烟量. 单位: 支
 * @param isAlcoholPer  不饮酒
 * @param lifeAlcoholPerDay  
 * @param lifeSportPerWeek  每周运动次数. 单位: 次
 * @param lifeSportPerTime  
 * @param lifeSaltSituation  
 * @param lifeFollowMedicalAdvice  
 * @param drugCompliance  
 * @param pulmonaryFunction  
 * @param id  
 * @param doctorId  
 * @param doctorName  随访医生姓名
 * @param symptomOther  其它
 * @param recommendCigarettesPer  建议日吸烟量
 * @param recommendAlcoholPer  
 * @param recommendSportPerWeek  建议每周运动次数
 * @param recommendSportPerTime  
 * @param isReactionsPains  心跳过快或心慌
 * @param isReactionsConvulsion  手颤或其他抽搐
 * @param isReactionsDizzinessHeadache  头痛头晕
 * @param isReactionsNauseaVomiting  恶心呕吐
 * @param isReactionsNone  无以上症状
 * @param reactionsOther  其它
 * @param drugs  
 */
data class CopdAddParams(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("patientName", required = true) val patientName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("followWay", required = true) val followWay: FllowWay,
    
    @field:JsonProperty("followDate", required = true) val followDate: java.time.LocalDateTime,
    
    @field:JsonProperty("isWheezingEffort", required = true) val isWheezingEffort: kotlin.Boolean,
    
    @field:JsonProperty("isWheezingWalkFast", required = true) val isWheezingWalkFast: kotlin.Boolean,
    
    @field:JsonProperty("isWheezingStroll", required = true) val isWheezingStroll: kotlin.Boolean,
    
    @field:JsonProperty("isWheezingHundredMeters", required = true) val isWheezingHundredMeters: kotlin.Boolean,
    
    @field:JsonProperty("isWheezingLittle", required = true) val isWheezingLittle: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomCough", required = true) val isSymptomCough: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomPurulentSputum", required = true) val isSymptomPurulentSputum: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomInappetence", required = true) val isSymptomInappetence: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomAbdominalDistention", required = true) val isSymptomAbdominalDistention: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomBreathing", required = true) val isSymptomBreathing: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomSystemicEdema", required = true) val isSymptomSystemicEdema: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomNone", required = true) val isSymptomNone: kotlin.Boolean,
    
    @field:JsonProperty("pulseOxygenSaturation", required = true) val pulseOxygenSaturation: java.math.BigDecimal,
    
    @field:JsonProperty("pulse", required = true) val pulse: java.math.BigDecimal,
    
    @field:JsonProperty("signsSbp", required = true) val signsSbp: java.math.BigDecimal,
    
    @field:JsonProperty("signsDbp", required = true) val signsDbp: java.math.BigDecimal,
    
    @field:JsonProperty("signsHeight", required = true) val signsHeight: java.math.BigDecimal,
    
    @field:JsonProperty("signsWeight", required = true) val signsWeight: java.math.BigDecimal,
    
    @field:JsonProperty("isCigarettesPer", required = true) val isCigarettesPer: kotlin.Boolean,
    
    @field:JsonProperty("lifeCigarettesPerDay", required = true) val lifeCigarettesPerDay: kotlin.Int,
    
    @field:JsonProperty("isAlcoholPer", required = true) val isAlcoholPer: kotlin.Boolean,
    
    @field:JsonProperty("lifeAlcoholPerDay", required = true) val lifeAlcoholPerDay: java.math.BigDecimal,
    
    @field:JsonProperty("lifeSportPerWeek", required = true) val lifeSportPerWeek: kotlin.Int,
    
    @field:JsonProperty("lifeSportPerTime", required = true) val lifeSportPerTime: java.math.BigDecimal,
    
    @field:Valid
    @field:JsonProperty("lifeSaltSituation", required = true) val lifeSaltSituation: LifeSalt,
    
    @field:Valid
    @field:JsonProperty("lifeFollowMedicalAdvice", required = true) val lifeFollowMedicalAdvice: LifeState,
    
    @field:Valid
    @field:JsonProperty("drugCompliance", required = true) val drugCompliance: DrugCompliance,
    
    @field:JsonProperty("pulmonaryFunction", required = true) val pulmonaryFunction: java.math.BigDecimal,

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null,

    @field:Valid
    @field:JsonProperty("doctorId") val doctorId: Id? = null,

    @field:JsonProperty("doctorName") val doctorName: kotlin.String? = null,

    @field:JsonProperty("symptomOther") val symptomOther: kotlin.String? = null,

    @field:JsonProperty("recommendCigarettesPer") val recommendCigarettesPer: kotlin.Int? = null,

    @field:JsonProperty("recommendAlcoholPer") val recommendAlcoholPer: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendSportPerWeek") val recommendSportPerWeek: kotlin.Int? = null,

    @field:JsonProperty("recommendSportPerTime") val recommendSportPerTime: java.math.BigDecimal? = null,

    @field:JsonProperty("isReactionsPains") val isReactionsPains: kotlin.Boolean? = null,

    @field:JsonProperty("isReactionsConvulsion") val isReactionsConvulsion: kotlin.Boolean? = null,

    @field:JsonProperty("isReactionsDizzinessHeadache") val isReactionsDizzinessHeadache: kotlin.Boolean? = null,

    @field:JsonProperty("isReactionsNauseaVomiting") val isReactionsNauseaVomiting: kotlin.Boolean? = null,

    @field:JsonProperty("isReactionsNone") val isReactionsNone: kotlin.Boolean? = null,

    @field:JsonProperty("reactionsOther") val reactionsOther: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("drugs") val drugs: kotlin.collections.List<DrugsInner>? = null
) {

}

