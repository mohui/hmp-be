package com.bjknrt.question.answering.system.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param id
 * @param patientId
 * @param createdAt
 * @param exerciseResult
 * @param isTakeDrugsUnsuitableForExercise  是否服用有运动禁忌的药物
 * @param isIndicationUnsuitableForExercise  是否患有不适宜运动的禁忌症
 * @param isHoldingModerateIntensityExercise  是否坚持中强度运动
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
 * @param walkResult  步行测试结果
 * @param powerResult  力量测试结果
 * @param flexibilityResult  柔韧性测试结果
 * @param contraindicationToExercise
 * @param whyNotModerateIntensityExercise
 */
data class ExerciseEvaluationOut(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:Valid
    @field:JsonProperty("patient_id", required = true) val patientId: Id,

    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,

    @field:Valid
    @field:JsonProperty("exerciseResult", required = true) val exerciseResult: RiskLevel,

    @field:JsonProperty("isTakeDrugsUnsuitableForExercise", required = true) val isTakeDrugsUnsuitableForExercise: kotlin.Boolean,

    @field:JsonProperty("isIndicationUnsuitableForExercise", required = true) val isIndicationUnsuitableForExercise: kotlin.Boolean,

    @field:JsonProperty("isHoldingModerateIntensityExercise", required = true) val isHoldingModerateIntensityExercise: kotlin.Boolean,

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

    @field:JsonProperty("walkResult") val walkResult: kotlin.String? = null,

    @field:JsonProperty("powerResult") val powerResult: kotlin.String? = null,

    @field:JsonProperty("flexibilityResult") val flexibilityResult: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("contraindicationToExercise") val contraindicationToExercise: kotlin.collections.List<ContraindicationToExercise>? = null,

    @field:JsonProperty("whyNotModerateIntensityExercise") val whyNotModerateIntensityExercise: kotlin.collections.List<kotlin.Int>? = null
) {

}

