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
 * @param messageStatus 消息类型 
 * @param messageNum 消息数量 
 * @param toDoStatusList 代办事项 
 * @param servicePackageList  
 * @param bindDatetime  
 * @param doctorId  
 * @param doctorName 医生姓名 
 * @param doctorHospitalId  
 * @param doctorHospitalName 医生机构名称 
 * @param doctorDeptName 医生科室 
 * @param doctorGender  
 * @param doctorPhone 医生电话 
 * @param pmhEssentialHypertension 既往病史-原发性高血压 
 * @param pmhTypeTwoDiabetes 既往病史-2型糖尿病 
 * @param pmhCerebralInfarction 既往病史-缺血性脑卒中 
 * @param pmhCoronaryHeartDisease 既往病史-冠心病 
 * @param pmhCopd 既往病史-慢阻肺 
 * @param pmhDyslipidemiaHyperlipidemia 既往病史-血脂异常 
 * @param pmhDiabeticNephropathy 既往病史-糖尿病肾病 
 * @param pmhDiabeticRetinopathy 既往病史-糖尿病视网膜病变 
 * @param pmhDiabeticFoot 既往病史-糖尿病足 
 * @param synthesisDiseaseTag  
 * @param hypertensionDiseaseTag  
 * @param diabetesDiseaseTag  
 * @param acuteCoronaryDiseaseTag  
 * @param copdDiseaseTag  
 * @param cerebralStrokeDiseaseTag  
 */
data class PatientPageResult(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,

    @field:JsonProperty("phone", required = true) val phone: kotlin.String,

    @field:JsonProperty("idCard", required = true) val idCard: kotlin.String,

    @field:Valid
    @field:JsonProperty("messageStatus", required = true) val messageStatus: MessageStatus,

    @field:JsonProperty("messageNum", required = true) val messageNum: kotlin.Int,

    @field:Valid
    @field:JsonProperty("toDoStatusList", required = true) val toDoStatusList: kotlin.collections.List<ToDoStatus>,

    @field:Valid
    @field:JsonProperty("servicePackageList", required = true) val servicePackageList: kotlin.collections.List<ServicePackageInfo>,

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

    @field:JsonProperty("bindDatetime") val bindDatetime: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("doctorId") val doctorId: java.math.BigInteger? = null,

    @field:JsonProperty("doctorName") val doctorName: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("doctorHospitalId") val doctorHospitalId: java.math.BigInteger? = null,

    @field:JsonProperty("doctorHospitalName") val doctorHospitalName: kotlin.String? = null,

    @field:JsonProperty("doctorDeptName") val doctorDeptName: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("doctorGender") val doctorGender: Gender? = null,

    @field:JsonProperty("doctorPhone") val doctorPhone: kotlin.String? = null,

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

