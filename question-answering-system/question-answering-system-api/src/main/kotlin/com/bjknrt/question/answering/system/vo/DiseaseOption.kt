package com.bjknrt.question.answering.system.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DiseaseOption
 * @param symptomDizzy  症状-头晕，头疼症状
 * @param symptomChestPain  症状-体力劳动、精神紧张或激动时出现胸痛症状，休息后逐渐缓解
 * @param symptomChronicCough  症状-呼吸困难或慢性咳嗽
 * @param symptomWeightLoss  症状-多饮、多尿、多食、不明原因体重下降
 * @param symptomGiddiness  症状-一过性黑曚、眩晕
 * @param symptomNone  症状-无以上症状
 * @param pmhEssentialHypertension  既往病史-原发性高血压
 * @param pmhTypeTwoDiabetes  既往病史-2型糖尿病
 * @param pmhCerebralInfarction  既往病史-缺血性脑卒中
 * @param pmhCoronaryHeartDisease  既往病史-冠心病
 * @param pmhCopd  既往病史-慢阻肺
 * @param pmhDyslipidemiaHyperlipidemia  既往病史-血脂异常
 * @param pmhDiabeticNephropathy  既往病史-糖尿病肾病
 * @param pmhDiabeticRetinopathy  既往病史-糖尿病视网膜病变
 * @param pmhDiabeticFoot  既往病史-糖尿病足
 * @param fhEssentialHypertension  家族史-原发性高血压
 * @param fhTypeTwoDiabetes  家族史-2型糖尿病
 * @param fhCerebralInfarction  家族史-缺血性脑卒中
 * @param fhCoronaryHeartDisease  家族史-冠心病
 * @param fhCopd  家族史-慢阻肺
 * @param createdAt
 */
data class DiseaseOption(

    @field:JsonProperty("symptomDizzy") val symptomDizzy: kotlin.Boolean? = null,

    @field:JsonProperty("symptomChestPain") val symptomChestPain: kotlin.Boolean? = null,

    @field:JsonProperty("symptomChronicCough") val symptomChronicCough: kotlin.Boolean? = null,

    @field:JsonProperty("symptomWeightLoss") val symptomWeightLoss: kotlin.Boolean? = null,

    @field:JsonProperty("symptomGiddiness") val symptomGiddiness: kotlin.Boolean? = null,

    @field:JsonProperty("symptomNone") val symptomNone: kotlin.Boolean? = null,

    @field:JsonProperty("pmhEssentialHypertension") val pmhEssentialHypertension: kotlin.Boolean? = null,

    @field:JsonProperty("pmhTypeTwoDiabetes") val pmhTypeTwoDiabetes: kotlin.Boolean? = null,

    @field:JsonProperty("pmhCerebralInfarction") val pmhCerebralInfarction: kotlin.Boolean? = null,

    @field:JsonProperty("pmhCoronaryHeartDisease") val pmhCoronaryHeartDisease: kotlin.Boolean? = null,

    @field:JsonProperty("pmhCopd") val pmhCopd: kotlin.Boolean? = null,

    @field:JsonProperty("pmhDyslipidemiaHyperlipidemia") val pmhDyslipidemiaHyperlipidemia: kotlin.Boolean? = null,

    @field:JsonProperty("pmhDiabeticNephropathy") val pmhDiabeticNephropathy: kotlin.Boolean? = null,

    @field:JsonProperty("pmhDiabeticRetinopathy") val pmhDiabeticRetinopathy: kotlin.Boolean? = null,

    @field:JsonProperty("pmhDiabeticFoot") val pmhDiabeticFoot: kotlin.Boolean? = null,

    @field:JsonProperty("fhEssentialHypertension") val fhEssentialHypertension: kotlin.Boolean? = null,

    @field:JsonProperty("fhTypeTwoDiabetes") val fhTypeTwoDiabetes: kotlin.Boolean? = null,

    @field:JsonProperty("fhCerebralInfarction") val fhCerebralInfarction: kotlin.Boolean? = null,

    @field:JsonProperty("fhCoronaryHeartDisease") val fhCoronaryHeartDisease: kotlin.Boolean? = null,

    @field:JsonProperty("fhCopd") val fhCopd: kotlin.Boolean? = null,

    @field:JsonProperty("createdAt") val createdAt: java.time.LocalDateTime? = null
) {

}

