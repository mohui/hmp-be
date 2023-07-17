package com.bjknrt.doctor.patient.management.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param id
 * @param name 姓名
 * @param gender
 * @param phone 手机号
 * @param idCard 身份证号
 * @param birthday
 * @param age 年龄
 * @param provincialCode 省编码
 * @param municipalCode 市编码
 * @param countyCode 县编码
 * @param townshipCode 乡镇编码
 * @param regionAddress 行政区域地址（省-市-县-乡镇）
 * @param address 详细地址
 * @param synthesisDiseaseTag
 * @param hypertensionDiseaseTag
 * @param diabetesDiseaseTag
 * @param acuteCoronaryDiseaseTag
 * @param cerebralStrokeDiseaseTag
 * @param copdDiseaseTag
 * @param messageStatus
 * @param messageNum 消息数量
 * @param height
 * @param weight
 * @param waistline
 * @param fastingBloodSugar
 * @param diastolicBloodPressure
 * @param systolicPressure
 * @param serumTch
 * @param heightDensityProteinTch
 * @param lowDensityProteinTch
 * @param smoking 是否吸烟
 * @param drinkWine 是否饮酒
 * @param sports 是否运动
 * @param saltIntake
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
 * @param symptomDizzy  症状-头晕，头疼症状
 * @param symptomChestPain  症状-体力劳动、精神紧张或激动时出现胸痛症状，休息后逐渐缓解
 * @param symptomChronicCough  症状-呼吸困难或慢性咳嗽
 * @param symptomWeightLoss  症状-多饮、多尿、多食、不明原因体重下降
 * @param symptomGiddiness  症状-一过性黑曚、眩晕
 * @param symptomNone  症状-无以上症状
 * @param doctor
 * @param bindDateTime
 * @param servicePackageUseDays
 */
data class PatientInfoResponse(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,

    @field:JsonProperty("phone", required = true) val phone: kotlin.String,

    @field:JsonProperty("idCard", required = true) val idCard: kotlin.String,

    @field:JsonProperty("birthday", required = true) val birthday: java.time.LocalDateTime,

    @field:JsonProperty("age", required = true) val age: kotlin.Int,

    @field:JsonProperty("provincialCode") val provincialCode: kotlin.String? = null,

    @field:JsonProperty("municipalCode") val municipalCode: kotlin.String? = null,

    @field:JsonProperty("countyCode") val countyCode: kotlin.String? = null,

    @field:JsonProperty("townshipCode") val townshipCode: kotlin.String? = null,

    @field:JsonProperty("regionAddress") val regionAddress: kotlin.String? = null,

    @field:JsonProperty("address") val address: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("synthesisDiseaseTag") val synthesisDiseaseTag: PatientSynthesisTag? = null,

    @field:Valid
    @field:JsonProperty("hypertensionDiseaseTag") val hypertensionDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("diabetesDiseaseTag") val diabetesDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("acuteCoronaryDiseaseTag") val acuteCoronaryDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("cerebralStrokeDiseaseTag") val cerebralStrokeDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("copdDiseaseTag") val copdDiseaseTag: PatientTag? = null,

    @field:Valid
    @field:JsonProperty("messageStatus") val messageStatus: MessageStatus? = null,

    @field:JsonProperty("messageNum") val messageNum: kotlin.Int? = null,

    @field:JsonProperty("height") val height: java.math.BigDecimal? = null,

    @field:JsonProperty("weight") val weight: java.math.BigDecimal? = null,

    @field:JsonProperty("waistline") val waistline: java.math.BigDecimal? = null,

    @field:JsonProperty("fastingBloodSugar") val fastingBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("diastolicBloodPressure") val diastolicBloodPressure: java.math.BigDecimal? = null,

    @field:JsonProperty("systolicPressure") val systolicPressure: java.math.BigDecimal? = null,

    @field:JsonProperty("serumTch") val serumTch: java.math.BigDecimal? = null,

    @field:JsonProperty("heightDensityProteinTch") val heightDensityProteinTch: java.math.BigDecimal? = null,

    @field:JsonProperty("lowDensityProteinTch") val lowDensityProteinTch: java.math.BigDecimal? = null,

    @field:JsonProperty("smoking") val smoking: kotlin.Boolean? = null,

    @field:JsonProperty("drinkWine") val drinkWine: kotlin.Boolean? = null,

    @field:JsonProperty("sports") val sports: kotlin.Boolean? = null,

    @field:JsonProperty("saltIntake") val saltIntake: java.math.BigDecimal? = null,

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

    @field:JsonProperty("symptomDizzy") val symptomDizzy: kotlin.Boolean? = null,

    @field:JsonProperty("symptomChestPain") val symptomChestPain: kotlin.Boolean? = null,

    @field:JsonProperty("symptomChronicCough") val symptomChronicCough: kotlin.Boolean? = null,

    @field:JsonProperty("symptomWeightLoss") val symptomWeightLoss: kotlin.Boolean? = null,

    @field:JsonProperty("symptomGiddiness") val symptomGiddiness: kotlin.Boolean? = null,

    @field:JsonProperty("symptomNone") val symptomNone: kotlin.Boolean? = null,

    @field:Valid
    @field:JsonProperty("doctor") val doctor: DoctorInfo? = null,

    @field:JsonProperty("bindDateTime") val bindDateTime: java.time.LocalDateTime? = null,

    @field:JsonProperty("servicePackageUseDays") val servicePackageUseDays: kotlin.Long? = null
) {

}

