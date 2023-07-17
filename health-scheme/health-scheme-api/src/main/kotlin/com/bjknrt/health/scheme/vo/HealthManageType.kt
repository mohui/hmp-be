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
* Values: HYPERTENSION,NONE,DIABETES,CEREBRAL_STROKE,COPD,ACUTE_CORONARY_DISEASE
*/
enum class HealthManageType(val value: kotlin.String) {

    /**
     * 高血压
     */
    @JsonProperty("HYPERTENSION") HYPERTENSION("HYPERTENSION"),
    
    /**
     * 无
     */
    @JsonProperty("NONE") NONE("NONE"),
    
    /**
     * 糖尿病
     */
    @JsonProperty("DIABETES") DIABETES("DIABETES"),
    
    /**
     * 脑卒中
     */
    @JsonProperty("CEREBRAL_STROKE") CEREBRAL_STROKE("CEREBRAL_STROKE"),
    
    /**
     * 慢阻肺
     */
    @JsonProperty("COPD") COPD("COPD"),
    
    /**
     * 冠心病
     */
    @JsonProperty("ACUTE_CORONARY_DISEASE") ACUTE_CORONARY_DISEASE("ACUTE_CORONARY_DISEASE")
    
}

