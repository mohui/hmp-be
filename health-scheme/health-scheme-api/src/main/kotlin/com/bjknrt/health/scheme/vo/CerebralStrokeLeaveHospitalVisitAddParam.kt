package com.bjknrt.health.scheme.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * CerebralStrokeLeaveHospitalVisitAddParam
 * @param patientId  
 * @param patientName  患者名称
 * @param doctorId  
 * @param followWay  
 * @param followDate  
 * @param knGender  
 * @param knAge  年龄
 * @param knMedicalRecordNumber  病案号
 * @param knImageNo  影像号
 * @param knTelephone  联系电话
 * @param knAdmissionDate  
 * @param knLeaveHospitalDate  
 * @param knOaSoiBrainInfarctionSite  
 * @param knOaBrainInfarctionToast  
 * @param knOaPreMorbidMrsScore  入院时情况-发病前mRS评分-数值填空
 * @param knOaMrsScore  入院时情况-入院时mRS评分-数值填空
 * @param knOaNiHssScore  入院时情况-NIHSS评分-数值填空
 * @param knOaBarthelScore  入院时情况-改良Barthel评分-数值填空
 * @param knOaEq5dScore  入院时情况-EQ-5D评分-数值填空
 * @param knLhIsStandardMedication  
 * @param knLhIsStandardLipidLoweringTreat  出院时情况-标准用药-是否标准降脂治疗（true-是，false-否）
 * @param knLhIsThrombolytic  出院时情况-标准用药-是否溶栓或取栓（true-是，false-否）
 * @param knLhAcuteStageCerebralInfarction  出院时情况-急性期脑梗死是否进展（true-是，false-否）
 * @param knLhIsMergeSeriousIllness  出院时情况-是否合并一下严重疾病：下肢静脉血栓、感染、心脏疾病、肿瘤、呼吸系统疾病，如哮喘、慢阻肺、呼吸衰竭、肺栓塞等、消化道出血等（true-是，false-否）
 * @param knLhIsStandardHighRiskElementControl  出院时情况-高危因素控制是否达标：高血压、糖代谢异常、血脂异常、心脏病、无症状性颈动脉粥样硬化、生活方式（true-是，false-否）
 * @param knLhTotalRehabilitationTime  出院时情况-康复评定-住院期间康复总时长（h）-数值填空
 * @param knLhMrsScore  出院时情况-康复评定-mRS评分-数值填空
 * @param knLhBarthelScore  出院时情况-康复评定-改良Barthel评分-数值填空
 * @param knLhEq5dScore  出院时情况-康复评定-EQ-5D评分-数值填空
 * @param knDysfunctionSportObstacle  功能障碍-运动障碍（true-是，false-否）
 * @param knDysfunctionCognitionObstacle  功能障碍-认知障碍（true-是，false-否）
 * @param knDysfunctionSpeechObstacle  功能障碍-言语障碍（true-是，false-否）
 * @param knDysfunctionSwallowObstacle  功能障碍-吞咽障碍（true-是，false-&#x60;否）
 * @param doctorName  医生名称
 */
data class CerebralStrokeLeaveHospitalVisitAddParam(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("patientName", required = true) val patientName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("doctorId", required = true) val doctorId: Id,
    
    @field:Valid
    @field:JsonProperty("followWay", required = true) val followWay: FllowWay,
    
    @field:JsonProperty("followDate", required = true) val followDate: java.time.LocalDateTime,
    
    @field:Valid
    @field:JsonProperty("knGender", required = true) val knGender: Gender,
    
    @field:JsonProperty("knAge", required = true) val knAge: kotlin.Int,
    
    @field:JsonProperty("KnMedicalRecordNumber", required = true) val knMedicalRecordNumber: kotlin.String,
    
    @field:JsonProperty("KnImageNo", required = true) val knImageNo: kotlin.String,
    
    @field:JsonProperty("KnTelephone", required = true) val knTelephone: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("KnAdmissionDate", required = true) val knAdmissionDate: java.time.LocalDate,
    
    @field:Valid
    @field:JsonProperty("KnLeaveHospitalDate", required = true) val knLeaveHospitalDate: java.time.LocalDate,
    
    @field:Valid
    @field:JsonProperty("KnOaSoiBrainInfarctionSite", required = true) val knOaSoiBrainInfarctionSite: InfarctionSite,
    
    @field:Valid
    @field:JsonProperty("KnOaBrainInfarctionToast", required = true) val knOaBrainInfarctionToast: ToastTyping,
    
    @field:JsonProperty("KnOaPreMorbidMrsScore", required = true) val knOaPreMorbidMrsScore: java.math.BigDecimal,
    
    @field:JsonProperty("KnOaMrsScore", required = true) val knOaMrsScore: java.math.BigDecimal,
    
    @field:JsonProperty("KnOaNiHssScore", required = true) val knOaNiHssScore: java.math.BigDecimal,
    
    @field:JsonProperty("KnOaBarthelScore", required = true) val knOaBarthelScore: java.math.BigDecimal,
    
    @field:JsonProperty("KnOaEq5dScore", required = true) val knOaEq5dScore: java.math.BigDecimal,
    
    @field:Valid
    @field:JsonProperty("KnLhIsStandardMedication", required = true) val knLhIsStandardMedication: StandardSecondaryPrevention,
    
    @field:JsonProperty("KnLhIsStandardLipidLoweringTreat", required = true) val knLhIsStandardLipidLoweringTreat: kotlin.Boolean,
    
    @field:JsonProperty("KnLhIsThrombolytic", required = true) val knLhIsThrombolytic: kotlin.Boolean,
    
    @field:JsonProperty("KnLhAcuteStageCerebralInfarction", required = true) val knLhAcuteStageCerebralInfarction: kotlin.Boolean,
    
    @field:JsonProperty("KnLhIsMergeSeriousIllness", required = true) val knLhIsMergeSeriousIllness: kotlin.Boolean,
    
    @field:JsonProperty("KnLhIsStandardHighRiskElementControl", required = true) val knLhIsStandardHighRiskElementControl: kotlin.Boolean,
    
    @field:JsonProperty("KnLhTotalRehabilitationTime", required = true) val knLhTotalRehabilitationTime: java.math.BigDecimal,
    
    @field:JsonProperty("KnLhMrsScore", required = true) val knLhMrsScore: java.math.BigDecimal,
    
    @field:JsonProperty("KnLhBarthelScore", required = true) val knLhBarthelScore: java.math.BigDecimal,
    
    @field:JsonProperty("KnLhEq5dScore", required = true) val knLhEq5dScore: java.math.BigDecimal,
    
    @field:JsonProperty("KnDysfunctionSportObstacle", required = true) val knDysfunctionSportObstacle: kotlin.Boolean,
    
    @field:JsonProperty("KnDysfunctionCognitionObstacle", required = true) val knDysfunctionCognitionObstacle: kotlin.Boolean,
    
    @field:JsonProperty("KnDysfunctionSpeechObstacle", required = true) val knDysfunctionSpeechObstacle: kotlin.Boolean,
    
    @field:JsonProperty("KnDysfunctionSwallowObstacle", required = true) val knDysfunctionSwallowObstacle: kotlin.Boolean,

    @field:JsonProperty("doctorName") val doctorName: kotlin.String? = null
) {

}

