package com.bjknrt.question.answering.system.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * ExerciseEvaluationInner
 * @param contraindicationToExercise ContraindicationToExercise 是否患有运动禁忌症
 * @param isTakeDrugsUnsuitableForExercise  是否服用不适宜运动的药物
 * @param isIndicationUnsuitableForExercise  是否有不适宜运动的指征
 * @param isHoldingModerateIntensityExercise  是否坚持中强度的运动
 * @param whyNotModerateIntensityExercise  为什么不尽兴中强度运动
 * @param professionalActivities  职业活动
 * @param dailyActivities  日常活动
 * @param outboundTransportation  交通工具
 * @param movementMode  锻炼方式
 * @param patientGender
 * @param patientAge  年龄
 * @param bodyFatRatio  体脂率
 * @param bodyMassIndex  体质指数
 * @param staticHeartRate  静态心率
 * @param numberOfPushUps  俯卧撑个数
 * @param walkingDistance6  6分钟步行距离-m
 * @param sittingBodyForwardBendingDistance  坐位体前屈距离-cm
 * @param patientId
 */
data class ExerciseEvaluationInner(

    @field:Valid
    @field:JsonProperty("contraindicationToExercise", required = true) val contraindicationToExercise: kotlin.collections.List<ContraindicationToExercise>,

    @field:JsonProperty("isTakeDrugsUnsuitableForExercise", required = true) val isTakeDrugsUnsuitableForExercise: kotlin.Boolean,

    @field:JsonProperty("isIndicationUnsuitableForExercise", required = true) val isIndicationUnsuitableForExercise: kotlin.Boolean,

    @field:JsonProperty("isHoldingModerateIntensityExercise", required = true) val isHoldingModerateIntensityExercise: kotlin.Boolean,

    @field:JsonProperty("whyNotModerateIntensityExercise", required = true) val whyNotModerateIntensityExercise: kotlin.collections.List<kotlin.Int>,

    @field:JsonProperty("professionalActivities", required = true) val professionalActivities: kotlin.Int,

    @field:JsonProperty("dailyActivities", required = true) val dailyActivities: kotlin.collections.List<kotlin.Int>,

    @field:JsonProperty("outboundTransportation", required = true) val outboundTransportation: kotlin.collections.List<kotlin.Int>,

    @field:JsonProperty("movementMode", required = true) val movementMode: kotlin.collections.List<kotlin.Int>,

    @field:Valid
    @field:JsonProperty("patientGender", required = true) val patientGender: Gender,

    @field:JsonProperty("patientAge", required = true) val patientAge: kotlin.Int,

    @field:JsonProperty("bodyFatRatio", required = true) val bodyFatRatio: java.math.BigDecimal,

    @field:JsonProperty("bodyMassIndex", required = true) val bodyMassIndex: java.math.BigDecimal,

    @field:JsonProperty("staticHeartRate", required = true) val staticHeartRate: kotlin.Int,

    @field:JsonProperty("numberOfPushUps", required = true) val numberOfPushUps: kotlin.Int,

    @field:JsonProperty("walkingDistance6", required = true) val walkingDistance6: java.math.BigDecimal,

    @field:JsonProperty("sittingBodyForwardBendingDistance", required = true) val sittingBodyForwardBendingDistance: java.math.BigDecimal,

    @field:Valid
    @field:JsonProperty("patientId") val patientId: Id? = null
) {

}

