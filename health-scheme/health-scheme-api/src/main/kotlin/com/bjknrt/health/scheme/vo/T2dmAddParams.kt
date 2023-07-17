package com.bjknrt.health.scheme.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * t2dmAddParams
 * @param patientId  
 * @param patientName  患者姓名
 * @param followWay  
 * @param followDate  
 * @param isSymptomNone  无症状
 * @param isSymptomDrink  多饮
 * @param isSymptomEat  多食
 * @param isSymptomDiuresis  多尿
 * @param isSymptomBlurred  视力模糊
 * @param isSymptomInfection  感染
 * @param isSymptomHandFeetNumbness  手脚麻木
 * @param isSymptomLowerExtremityEdema  下肢浮肿
 * @param isSymptomWeightLossDiagnosed  体重明显下降
 * @param signsSbp  
 * @param signsDbp  
 * @param signsWeight  
 * @param signsAdpLeft  
 * @param signsAdpRight  
 * @param lifeCigarettesPerDay  日吸烟量. 单位: 支
 * @param lifeAlcoholPerDay  
 * @param lifeSportPerWeek  
 * @param lifeSportPerTime  
 * @param lifeFoodSituation  
 * @param lifeFollowMedicalAdvice  
 * @param glu  
 * @param drugCompliance  
 * @param rhg  
 * @param doctorId  
 * @param doctorName  随访医生姓名
 * @param symptomOther  其它
 * @param signsHeight  
 * @param signsBim  
 * @param lifeMentalAdjustment  
 * @param hbA1c  
 * @param hbA1cDate  
 * @param assistOther  辅助检查其他检查
 * @param isAdr  有无药物不良反应
 * @param adrDesc  药物不良反应描述
 * @param visitClass  
 * @param insulinName  胰岛素名称
 * @param insulinDesc  胰岛素用法用量
 * @param isHasReferral  是否转诊
 * @param referralReason  转诊原因
 * @param referralAgencies  转诊机构及科别
 * @param nextVisit  
 * @param drugs  
 * @param recommendWeight  
 * @param recommendBim  
 * @param recommendCigarettesPer  建议日吸烟量
 * @param recommendAlcoholPer  
 * @param recommendSportPerWeek  
 * @param recommendSportPerTime  
 */
data class T2dmAddParams(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("patientName", required = true) val patientName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("followWay", required = true) val followWay: FllowWay,
    
    @field:JsonProperty("followDate", required = true) val followDate: java.time.LocalDateTime,
    
    @field:JsonProperty("isSymptomNone", required = true) val isSymptomNone: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomDrink", required = true) val isSymptomDrink: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomEat", required = true) val isSymptomEat: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomDiuresis", required = true) val isSymptomDiuresis: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomBlurred", required = true) val isSymptomBlurred: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomInfection", required = true) val isSymptomInfection: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomHandFeetNumbness", required = true) val isSymptomHandFeetNumbness: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomLowerExtremityEdema", required = true) val isSymptomLowerExtremityEdema: kotlin.Boolean,
    
    @field:JsonProperty("isSymptomWeightLossDiagnosed", required = true) val isSymptomWeightLossDiagnosed: kotlin.Boolean,
    
    @field:JsonProperty("signsSbp", required = true) val signsSbp: java.math.BigDecimal,
    
    @field:JsonProperty("signsDbp", required = true) val signsDbp: java.math.BigDecimal,
    
    @field:JsonProperty("signsWeight", required = true) val signsWeight: java.math.BigDecimal,
    
    @field:Valid
    @field:JsonProperty("signsAdpLeft", required = true) val signsAdpLeft: SignsAdp,
    
    @field:Valid
    @field:JsonProperty("signsAdpRight", required = true) val signsAdpRight: SignsAdp,
    
    @field:JsonProperty("lifeCigarettesPerDay", required = true) val lifeCigarettesPerDay: kotlin.Int,
    
    @field:JsonProperty("lifeAlcoholPerDay", required = true) val lifeAlcoholPerDay: java.math.BigDecimal,
    
    @field:JsonProperty("lifeSportPerWeek", required = true) val lifeSportPerWeek: java.math.BigDecimal,
    
    @field:JsonProperty("lifeSportPerTime", required = true) val lifeSportPerTime: java.math.BigDecimal,
    
    @field:JsonProperty("lifeFoodSituation", required = true) val lifeFoodSituation: java.math.BigDecimal,
    
    @field:Valid
    @field:JsonProperty("lifeFollowMedicalAdvice", required = true) val lifeFollowMedicalAdvice: LifeState,
    
    @field:JsonProperty("glu", required = true) val glu: java.math.BigDecimal,
    
    @field:Valid
    @field:JsonProperty("drugCompliance", required = true) val drugCompliance: DrugCompliance,
    
    @field:Valid
    @field:JsonProperty("rhg", required = true) val rhg: Rhg,

    @field:Valid
    @field:JsonProperty("doctorId") val doctorId: Id? = null,

    @field:JsonProperty("doctorName") val doctorName: kotlin.String? = null,

    @field:JsonProperty("symptomOther") val symptomOther: kotlin.String? = null,

    @field:JsonProperty("signsHeight") val signsHeight: java.math.BigDecimal? = null,

    @field:JsonProperty("signsBim") val signsBim: java.math.BigDecimal? = null,

    @field:Valid
    @field:JsonProperty("lifeMentalAdjustment") val lifeMentalAdjustment: LifeState? = null,

    @field:JsonProperty("hbA1c") val hbA1c: java.math.BigDecimal? = null,

    @field:JsonProperty("hbA1cDate") val hbA1cDate: java.time.LocalDateTime? = null,

    @field:JsonProperty("assistOther") val assistOther: kotlin.String? = null,

    @field:JsonProperty("isAdr") val isAdr: kotlin.Boolean? = null,

    @field:JsonProperty("adrDesc") val adrDesc: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("visitClass") val visitClass: VisitClass? = null,

    @field:JsonProperty("insulinName") val insulinName: kotlin.String? = null,

    @field:JsonProperty("insulinDesc") val insulinDesc: kotlin.String? = null,

    @field:JsonProperty("isHasReferral") val isHasReferral: kotlin.Boolean? = null,

    @field:JsonProperty("referralReason") val referralReason: kotlin.String? = null,

    @field:JsonProperty("referralAgencies") val referralAgencies: kotlin.String? = null,

    @field:JsonProperty("nextVisit") val nextVisit: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("drugs") val drugs: kotlin.collections.List<DrugsInner>? = null,

    @field:JsonProperty("recommendWeight") val recommendWeight: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendBim") val recommendBim: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendCigarettesPer") val recommendCigarettesPer: kotlin.Int? = null,

    @field:JsonProperty("recommendAlcoholPer") val recommendAlcoholPer: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendSportPerWeek") val recommendSportPerWeek: java.math.BigDecimal? = null,

    @field:JsonProperty("recommendSportPerTime") val recommendSportPerTime: java.math.BigDecimal? = null
) {

}

