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
 * @param provincialCode 省编码
 * @param municipalCode 市编码
 * @param countyCode 县编码
 * @param townshipCode 乡镇编码
 * @param familyHistory 家族史，多选逗号分割
 * @param symptom 症状，多选逗号分割
 * @param smoking 是否吸烟
 * @param drinkWine 是否饮酒
 * @param sports 是否运动
 * @param saltIntake
 * @param birthday
 * @param address 详细地址
 * @param synthesisDiseaseTag
 * @param hypertensionDiseaseTag
 * @param diabetesDiseaseTag
 * @param acuteCoronaryDiseaseTag
 * @param cerebralStrokeDiseaseTag
 * @param copdDiseaseTag
 * @param pmhEssentialHypertension  既往病史-原发性高血压
 * @param pmhTypeTwoDiabetes  既往病史-2型糖尿病
 * @param pmhCerebralInfarction  既往病史-缺血性脑卒中
 * @param pmhCoronaryHeartDisease  既往病史-冠心病
 * @param pmhCopd  既往病史-慢阻肺
 * @param pmhDyslipidemiaHyperlipidemia  既往病史-血脂异常
 * @param pmhDiabeticNephropathy  既往病史-糖尿病肾病
 * @param pmhDiabeticRetinopathy  既往病史-糖尿病视网膜病变
 * @param pmhDiabeticFoot  既往病史-糖尿病足
 */
data class EditRequest(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,

    @field:JsonProperty("name") val name: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("gender") val gender: Gender? = null,

    @field:JsonProperty("phone") val phone: kotlin.String? = null,

    @field:JsonProperty("idCard") val idCard: kotlin.String? = null,

    @field:JsonProperty("provincialCode") val provincialCode: kotlin.String? = null,

    @field:JsonProperty("municipalCode") val municipalCode: kotlin.String? = null,

    @field:JsonProperty("countyCode") val countyCode: kotlin.String? = null,

    @field:JsonProperty("townshipCode") val townshipCode: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("familyHistory") val familyHistory: kotlin.collections.Set<FamilyHistory>? = null,

    @field:Valid
    @field:JsonProperty("symptom") val symptom: kotlin.collections.Set<PatientSymptom>? = null,

    @field:JsonProperty("smoking") val smoking: kotlin.Boolean? = null,

    @field:JsonProperty("drinkWine") val drinkWine: kotlin.Boolean? = null,

    @field:JsonProperty("sports") val sports: kotlin.Boolean? = null,

    @field:JsonProperty("saltIntake") val saltIntake: java.math.BigDecimal? = null,

    @field:JsonProperty("birthday") val birthday: java.time.LocalDateTime? = null,

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

    @field:JsonProperty("pmhEssentialHypertension") val pmhEssentialHypertension: kotlin.Boolean? = null,

    @field:JsonProperty("pmhTypeTwoDiabetes") val pmhTypeTwoDiabetes: kotlin.Boolean? = null,

    @field:JsonProperty("pmhCerebralInfarction") val pmhCerebralInfarction: kotlin.Boolean? = null,

    @field:JsonProperty("pmhCoronaryHeartDisease") val pmhCoronaryHeartDisease: kotlin.Boolean? = null,

    @field:JsonProperty("pmhCopd") val pmhCopd: kotlin.Boolean? = null,

    @field:JsonProperty("pmhDyslipidemiaHyperlipidemia") val pmhDyslipidemiaHyperlipidemia: kotlin.Boolean? = null,

    @field:JsonProperty("pmhDiabeticNephropathy") val pmhDiabeticNephropathy: kotlin.Boolean? = null,

    @field:JsonProperty("pmhDiabeticRetinopathy") val pmhDiabeticRetinopathy: kotlin.Boolean? = null,

    @field:JsonProperty("pmhDiabeticFoot") val pmhDiabeticFoot: kotlin.Boolean? = null
) {

}

