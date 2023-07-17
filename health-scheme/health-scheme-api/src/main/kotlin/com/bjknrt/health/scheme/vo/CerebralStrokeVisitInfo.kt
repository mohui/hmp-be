package com.bjknrt.health.scheme.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param id  
 * @param createdBy  
 * @param createdAt  
 * @param patientId  
 * @param patientName  患者名称
 * @param followWay  
 * @param followDate  
 * @param mrsScore  
 * @param barthelScore  
 * @param eq5dScore  
 * @param preventionDrug  
 * @param isLipidLoweringTherapy  是否标准降脂治疗
 * @param isRegularMedication  是否遵从医嘱按时规律服药
 * @param isHighRiskFactorsStandard  下列高危因素控制是否达标：高血压、糖代谢异常、血脂异常、心脏病、无症状性颈动脉粥样硬化、生活方式
 * @param isPainCauseHead  疼痛-头痛
 * @param isPainCauseArthrosis  疼痛-关节痛
 * @param isPainCauseShoulder  疼痛-肩痛
 * @param isPainCauseNerve  疼痛-中枢性疼痛（多因触摸、接触水或运动而加重 ）
 * @param isPainCauseNone  疼痛-无 
 * @param isFall  跌倒
 * @param normalRecoverDays  常规康复-发病后多久启动康复(数值输入，整数（天）)
 * @param normalRecoverFrequency  常规康复-训练频次： （次/周）
 * @param normalRecoverTime  
 * @param isNormalRecoverSport  常规康复-运动疗法
 * @param isNormalRecoverWork  常规康复-作业疗法
 * @param isNormalRecoverAcknowledge  常规康复-认知训练 
 * @param isNormalRecoverParole  常规康复-言语训练
 * @param isNormalRecoverSwallow  常规康复-吞咽训练 
 * @param isNormalRecoverNone  常规康复-以上都不选
 * @param intelligentRecoverDays  智能康复-发病后多久启动康复(数值输入，整数（天）) 
 * @param intelligentRecoverFrequency  智能康复-训练频次： （次/周）
 * @param intelligentRecoverTime  
 * @param isIntelligentRecoverBci  智能康复-脑机接口
 * @param isIntelligentRecoverRobot  智能康复-康复机器人
 * @param isIntelligentRecoverBalance  智能康复-平衡反馈训练
 * @param isIntelligentRecoverVirtualReality  智能康复-虚拟现实
 * @param isIntelligentRecoverOther  智能康复-其他智能设备辅助下训练
 * @param isIntelligentRecoverNone  智能康复-以上都不选
 * @param doctorId  
 * @param doctorName  医生名称
 */
data class CerebralStrokeVisitInfo(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("createdBy", required = true) val createdBy: java.math.BigInteger,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("patientName", required = true) val patientName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("followWay", required = true) val followWay: FllowWay,
    
    @field:JsonProperty("followDate", required = true) val followDate: java.time.LocalDateTime,
    
    @field:JsonProperty("mrsScore", required = true) val mrsScore: java.math.BigDecimal,
    
    @field:JsonProperty("barthelScore", required = true) val barthelScore: java.math.BigDecimal,
    
    @field:JsonProperty("eq5dScore", required = true) val eq5dScore: java.math.BigDecimal,
    
    @field:Valid
    @field:JsonProperty("preventionDrug", required = true) val preventionDrug: PreventionDrug,
    
    @field:JsonProperty("isLipidLoweringTherapy", required = true) val isLipidLoweringTherapy: kotlin.Boolean,
    
    @field:JsonProperty("isRegularMedication", required = true) val isRegularMedication: kotlin.Boolean,
    
    @field:JsonProperty("isHighRiskFactorsStandard", required = true) val isHighRiskFactorsStandard: kotlin.Boolean,
    
    @field:JsonProperty("isPainCauseHead", required = true) val isPainCauseHead: kotlin.Boolean,
    
    @field:JsonProperty("isPainCauseArthrosis", required = true) val isPainCauseArthrosis: kotlin.Boolean,
    
    @field:JsonProperty("isPainCauseShoulder", required = true) val isPainCauseShoulder: kotlin.Boolean,
    
    @field:JsonProperty("isPainCauseNerve", required = true) val isPainCauseNerve: kotlin.Boolean,
    
    @field:JsonProperty("isPainCauseNone", required = true) val isPainCauseNone: kotlin.Boolean,
    
    @field:JsonProperty("isFall", required = true) val isFall: kotlin.Boolean,
    
    @field:JsonProperty("normalRecoverDays", required = true) val normalRecoverDays: kotlin.Int,
    
    @field:JsonProperty("normalRecoverFrequency", required = true) val normalRecoverFrequency: kotlin.Int,
    
    @field:JsonProperty("normalRecoverTime", required = true) val normalRecoverTime: java.math.BigDecimal,
    
    @field:JsonProperty("isNormalRecoverSport", required = true) val isNormalRecoverSport: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverWork", required = true) val isNormalRecoverWork: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverAcknowledge", required = true) val isNormalRecoverAcknowledge: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverParole", required = true) val isNormalRecoverParole: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverSwallow", required = true) val isNormalRecoverSwallow: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverNone", required = true) val isNormalRecoverNone: kotlin.Boolean,
    
    @field:JsonProperty("intelligentRecoverDays", required = true) val intelligentRecoverDays: kotlin.Int,
    
    @field:JsonProperty("intelligentRecoverFrequency", required = true) val intelligentRecoverFrequency: kotlin.Int,
    
    @field:JsonProperty("intelligentRecoverTime", required = true) val intelligentRecoverTime: java.math.BigDecimal,
    
    @field:JsonProperty("isIntelligentRecoverBci", required = true) val isIntelligentRecoverBci: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverRobot", required = true) val isIntelligentRecoverRobot: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverBalance", required = true) val isIntelligentRecoverBalance: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverVirtualReality", required = true) val isIntelligentRecoverVirtualReality: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverOther", required = true) val isIntelligentRecoverOther: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverNone", required = true) val isIntelligentRecoverNone: kotlin.Boolean,

    @field:Valid
    @field:JsonProperty("doctorId") val doctorId: Id? = null,

    @field:JsonProperty("doctorName") val doctorName: kotlin.String? = null
) {

}

