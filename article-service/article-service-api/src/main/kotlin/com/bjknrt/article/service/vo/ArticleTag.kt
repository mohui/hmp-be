package com.bjknrt.article.service.vo

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
* Values: HYPERTENSION,DIABETES,ACUTE_CORONARY,CEREBRAL_STROKE,COPD
*/
enum class ArticleTag(val value: kotlin.String) {

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
    @JsonProperty("ACUTE_CORONARY") ACUTE_CORONARY("ACUTE_CORONARY"),
    
    /**
     * 脑卒中
     */
    @JsonProperty("CEREBRAL_STROKE") CEREBRAL_STROKE("CEREBRAL_STROKE"),
    
    /**
     * 慢阻肺
     */
    @JsonProperty("COPD") COPD("COPD")
    
}

