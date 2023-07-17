package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

/**
 * CerebralStrokeDetailDefaultValue
 * @param mrsScore  
 * @param barthelScore  
 * @param eq5dScore  
 * @param isNormalRecoverSport  常规康复-运动疗法
 * @param isNormalRecoverWork  常规康复-作业疗法
 * @param isNormalRecoverAcknowledge  常规康复-认知训练 
 * @param isNormalRecoverParole  常规康复-言语训练
 * @param isNormalRecoverSwallow  常规康复-吞咽训练 
 * @param isNormalRecoverNone  常规康复-以上都不选
 * @param isIntelligentRecoverBci  智能康复-脑机接口
 * @param isIntelligentRecoverRobot  智能康复-康复机器人
 * @param isIntelligentRecoverBalance  智能康复-平衡反馈训练
 * @param isIntelligentRecoverVirtualReality  智能康复-虚拟现实
 * @param isIntelligentRecoverOther  智能康复-其他智能设备辅助下训练
 * @param isIntelligentRecoverNone  智能康复-以上都不选
 */
data class CerebralStrokeDetailDefaultValue(
    
    @field:JsonProperty("mrsScore", required = true) val mrsScore: java.math.BigDecimal,
    
    @field:JsonProperty("barthelScore", required = true) val barthelScore: java.math.BigDecimal,
    
    @field:JsonProperty("eq5dScore", required = true) val eq5dScore: java.math.BigDecimal,
    
    @field:JsonProperty("isNormalRecoverSport", required = true) val isNormalRecoverSport: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverWork", required = true) val isNormalRecoverWork: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverAcknowledge", required = true) val isNormalRecoverAcknowledge: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverParole", required = true) val isNormalRecoverParole: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverSwallow", required = true) val isNormalRecoverSwallow: kotlin.Boolean,
    
    @field:JsonProperty("isNormalRecoverNone", required = true) val isNormalRecoverNone: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverBci", required = true) val isIntelligentRecoverBci: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverRobot", required = true) val isIntelligentRecoverRobot: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverBalance", required = true) val isIntelligentRecoverBalance: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverVirtualReality", required = true) val isIntelligentRecoverVirtualReality: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverOther", required = true) val isIntelligentRecoverOther: kotlin.Boolean,
    
    @field:JsonProperty("isIntelligentRecoverNone", required = true) val isIntelligentRecoverNone: kotlin.Boolean
) {

}

