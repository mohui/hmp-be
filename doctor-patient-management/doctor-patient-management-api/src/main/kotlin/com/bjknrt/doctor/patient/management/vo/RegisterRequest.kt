package com.bjknrt.doctor.patient.management.vo

import java.util.Objects
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.framework.api.vo.Id
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
 * 
 * @param id  
 * @param name 姓名 
 * @param gender  
 * @param birthday  
 * @param phone 电话号码 
 * @param idCard 身份证号码 
 * @param pmhEssentialHypertension 既往病史-原发性高血压 
 * @param pmhTypeTwoDiabetes 既往病史-2型糖尿病 
 * @param pmhCerebralInfarction 既往病史-缺血性脑卒中 
 * @param pmhCoronaryHeartDisease 既往病史-冠心病 
 * @param pmhCopd 既往病史-慢阻肺 
 * @param pmhDyslipidemiaHyperlipidemia 既往病史-血脂异常 
 * @param pmhDiabeticNephropathy 既往病史-糖尿病肾病 
 * @param pmhDiabeticRetinopathy 既往病史-糖尿病视网膜病变 
 * @param pmhDiabeticFoot 既往病史-糖尿病足 
 */
data class RegisterRequest(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,

    @field:JsonProperty("birthday", required = true) val birthday: java.time.LocalDateTime,

    @field:JsonProperty("phone", required = true) val phone: kotlin.String,
    
    @field:JsonProperty("idCard", required = true) val idCard: kotlin.String,

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

