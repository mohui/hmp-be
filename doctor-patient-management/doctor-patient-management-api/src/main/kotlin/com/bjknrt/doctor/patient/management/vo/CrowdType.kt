package com.bjknrt.doctor.patient.management.vo

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonValue
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
* 人群分类（HYPERTENSION-高血压,DIABETES-糖尿病,ACUTE_CORONARY_DISEASE-冠心病,CEREBRAL_STROKE-脑卒中,COPD-慢阻肺）
* Values: HYPERTENSION,DIABETES,ACUTE_CORONARY_DISEASE,CEREBRAL_STROKE,COPD
*/
enum class CrowdType(val value: kotlin.String) {

    /**
     * 高血压
     */
    @JsonProperty("HYPERTENSION") HYPERTENSION("HYPERTENSION"),
    
    /**
     * 糖尿病
     */
    @JsonProperty("DIABETES") DIABETES("DIABETES"),
    
    /**
     * 冠心病
     */
    @JsonProperty("ACUTE_CORONARY_DISEASE") ACUTE_CORONARY_DISEASE("ACUTE_CORONARY_DISEASE"),
    
    /**
     * 脑卒中
     */
    @JsonProperty("CEREBRAL_STROKE") CEREBRAL_STROKE("CEREBRAL_STROKE"),
    
    /**
     * 慢阻肺
     */
    @JsonProperty("COPD") COPD("COPD")
    
}

