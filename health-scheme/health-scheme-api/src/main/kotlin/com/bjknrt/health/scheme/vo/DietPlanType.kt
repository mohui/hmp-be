package com.bjknrt.health.scheme.vo

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
* 
* Values: HYPERTENSION,DIABETES,COPD,ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE,HYPERTENSION_DIABETES,HYPERTENSION_COPD,HYPERTENSION_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE,DIABETES_COPD,DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE,COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE,HYPERTENSION_DIABETES_COPD,HYPERTENSION_DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE,HYPERTENSION_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE,DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE,HYPERTENSION_DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE
*/
enum class DietPlanType(val value: kotlin.String) {

    /**
     * 高血压饮食
     */
    @JsonProperty("HYPERTENSION") HYPERTENSION("HYPERTENSION"),
    
    /**
     * 糖尿病饮食
     */
    @JsonProperty("DIABETES") DIABETES("DIABETES"),
    
    /**
     * 慢阻肺饮食
     */
    @JsonProperty("COPD") COPD("COPD"),
    
    /**
     * 冠心病/脑卒中饮食
     */
    @JsonProperty("ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE") ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE("ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE"),
    
    /**
     * 高血压+糖尿病饮食
     */
    @JsonProperty("HYPERTENSION_DIABETES") HYPERTENSION_DIABETES("HYPERTENSION_DIABETES"),
    
    /**
     * 高血压+慢阻肺饮食
     */
    @JsonProperty("HYPERTENSION_COPD") HYPERTENSION_COPD("HYPERTENSION_COPD"),
    
    /**
     * 高血压+冠心病/脑卒中饮食
     */
    @JsonProperty("HYPERTENSION_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE") HYPERTENSION_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE("HYPERTENSION_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE"),
    
    /**
     * 糖尿病+慢阻肺饮食
     */
    @JsonProperty("DIABETES_COPD") DIABETES_COPD("DIABETES_COPD"),
    
    /**
     * 糖尿病+冠心病/脑卒中饮食
     */
    @JsonProperty("DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE") DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE("DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE"),
    
    /**
     * 慢阻肺+冠心病/脑卒中饮食
     */
    @JsonProperty("COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE") COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE("COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE"),
    
    /**
     *  高血压+糖尿病+慢阻肺饮食
     */
    @JsonProperty("HYPERTENSION_DIABETES_COPD") HYPERTENSION_DIABETES_COPD("HYPERTENSION_DIABETES_COPD"),
    
    /**
     *  高血压+糖尿病+冠心病/脑卒中饮食
     */
    @JsonProperty("HYPERTENSION_DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE") HYPERTENSION_DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE("HYPERTENSION_DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE"),
    
    /**
     * 高血压+慢阻肺+冠心病/脑卒中饮食
     */
    @JsonProperty("HYPERTENSION_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE") HYPERTENSION_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE("HYPERTENSION_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE"),
    
    /**
     * 糖尿病+慢阻肺+冠心病/脑卒中
     */
    @JsonProperty("DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE") DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE("DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE"),
    
    /**
     * 高血压+糖尿病+慢阻肺+冠心病/脑卒中饮食
     */
    @JsonProperty("HYPERTENSION_DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE") HYPERTENSION_DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE("HYPERTENSION_DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE")
    
}

