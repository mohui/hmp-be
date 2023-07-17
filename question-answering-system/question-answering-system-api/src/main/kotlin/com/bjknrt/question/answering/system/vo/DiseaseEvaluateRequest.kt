package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * DiseaseEvaluateRequest
 * @param examinationPaperId
 * @param examinationPaperCode 问卷表编码
 * @param patientId
 * @param age 患者年龄
 * @param gender
 * @param patientHeight
 * @param patientWeight
 * @param patientWaistline
 * @param isSmoking 是否吸烟
 * @param isPmhEssentialHypertension 既往病史-原发性高血压
 * @param isPmhTypeTwoDiabetes 既往病史-2型糖尿病
 * @param isPmhCerebralInfarction 既往病史-缺血性脑卒中（脑梗死）
 * @param isPmhCoronaryHeartDisease 既往病史-冠心病
 * @param isPmhCopd 既往病史-慢阻肺
 * @param isPmhDyslipidemiaHyperlipidemia 既往病史-血脂异常
 * @param isPmhDiabeticNephropathy 既往病史-糖尿病肾病
 * @param isPmhDiabeticRetinopathy 既往病史-糖尿病视网膜病变
 * @param isPmhDiabeticFoot 既往病史-糖尿病足
 * @param isFhEssentialHypertension 家族史-原发性高血压
 * @param isFhTypeTwoDiabetes 家族史-2型糖尿病
 * @param isFhCerebralInfarction 家族史-缺血性脑卒中（脑梗死）
 * @param isFhCoronaryHeartDisease 家族史-冠心病
 * @param isFhCopd 家族史-慢阻肺
 * @param isSymptomDizzy 症状-头晕，头疼症状
 * @param isSymptomChestPain 症状-体力劳动、精神紧张或激动时出现胸痛症状，休息后逐渐缓解
 * @param isSymptomChronicCough 症状-呼吸困难或慢性咳嗽
 * @param isSymptomWeightLoss 症状-多饮、多尿、多食、不明原因体重下降
 * @param isSymptomGiddiness 症状-一过性黑曚、眩晕
 * @param isSymptomNone 症状-无以上症状
 * @param systolicPressure
 * @param diastolicBloodPressure
 * @param fastingBloodSugar
 * @param serumTch
 */
data class DiseaseEvaluateRequest(

    @field:Valid
    @field:JsonProperty("examinationPaperId", required = true) val examinationPaperId: java.math.BigInteger,

    @field:JsonProperty("examinationPaperCode", required = true) val examinationPaperCode: kotlin.String,

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,

    @field:JsonProperty("age", required = true) val age: kotlin.Int,

    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,

    @field:JsonProperty("patientHeight", required = true) val patientHeight: java.math.BigDecimal,

    @field:JsonProperty("patientWeight", required = true) val patientWeight: java.math.BigDecimal,

    @field:JsonProperty("patientWaistline", required = true) val patientWaistline: java.math.BigDecimal,

    @field:JsonProperty("isSmoking", required = true) val isSmoking: kotlin.Boolean,

    @field:JsonProperty("isPmhEssentialHypertension", required = true) val isPmhEssentialHypertension: kotlin.Boolean,

    @field:JsonProperty("isPmhTypeTwoDiabetes", required = true) val isPmhTypeTwoDiabetes: kotlin.Boolean,

    @field:JsonProperty("isPmhCerebralInfarction", required = true) val isPmhCerebralInfarction: kotlin.Boolean,

    @field:JsonProperty("isPmhCoronaryHeartDisease", required = true) val isPmhCoronaryHeartDisease: kotlin.Boolean,

    @field:JsonProperty("isPmhCopd", required = true) val isPmhCopd: kotlin.Boolean,

    @field:JsonProperty("isPmhDyslipidemiaHyperlipidemia", required = true) val isPmhDyslipidemiaHyperlipidemia: kotlin.Boolean,

    @field:JsonProperty("isPmhDiabeticNephropathy", required = true) val isPmhDiabeticNephropathy: kotlin.Boolean,

    @field:JsonProperty("isPmhDiabeticRetinopathy", required = true) val isPmhDiabeticRetinopathy: kotlin.Boolean,

    @field:JsonProperty("isPmhDiabeticFoot", required = true) val isPmhDiabeticFoot: kotlin.Boolean,

    @field:JsonProperty("isFhEssentialHypertension", required = true) val isFhEssentialHypertension: kotlin.Boolean,

    @field:JsonProperty("isFhTypeTwoDiabetes", required = true) val isFhTypeTwoDiabetes: kotlin.Boolean,

    @field:JsonProperty("isFhCerebralInfarction", required = true) val isFhCerebralInfarction: kotlin.Boolean,

    @field:JsonProperty("isFhCoronaryHeartDisease", required = true) val isFhCoronaryHeartDisease: kotlin.Boolean,

    @field:JsonProperty("isFhCopd", required = true) val isFhCopd: kotlin.Boolean,

    @field:JsonProperty("isSymptomDizzy", required = true) val isSymptomDizzy: kotlin.Boolean,

    @field:JsonProperty("isSymptomChestPain", required = true) val isSymptomChestPain: kotlin.Boolean,

    @field:JsonProperty("isSymptomChronicCough", required = true) val isSymptomChronicCough: kotlin.Boolean,

    @field:JsonProperty("isSymptomWeightLoss", required = true) val isSymptomWeightLoss: kotlin.Boolean,

    @field:JsonProperty("isSymptomGiddiness", required = true) val isSymptomGiddiness: kotlin.Boolean,

    @field:JsonProperty("isSymptomNone", required = true) val isSymptomNone: kotlin.Boolean,

    @field:JsonProperty("systolicPressure") val systolicPressure: java.math.BigDecimal? = null,

    @field:JsonProperty("diastolicBloodPressure") val diastolicBloodPressure: java.math.BigDecimal? = null,

    @field:JsonProperty("fastingBloodSugar") val fastingBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("serumTch") val serumTch: java.math.BigDecimal? = null
) {

}

