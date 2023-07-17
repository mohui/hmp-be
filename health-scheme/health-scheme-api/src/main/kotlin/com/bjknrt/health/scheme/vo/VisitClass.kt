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
* 此次随访分类. SATISFACTORY: 控制满意; UNSATISFACTORY: 控制不满意; UNTOWARD_EFFECT: 不良反应; COMPLICATION: 并发症
* Values: SATISFACTORY,UNSATISFACTORY,UNTOWARD_EFFECT,COMPLICATION
*/
enum class VisitClass(val value: kotlin.String) {

    /**
     * 控制满意
     */
    @JsonProperty("SATISFACTORY") SATISFACTORY("SATISFACTORY"),
    
    /**
     * 控制不满意
     */
    @JsonProperty("UNSATISFACTORY") UNSATISFACTORY("UNSATISFACTORY"),
    
    /**
     * 不良反应
     */
    @JsonProperty("UNTOWARD_EFFECT") UNTOWARD_EFFECT("UNTOWARD_EFFECT"),
    
    /**
     * 并发症
     */
    @JsonProperty("COMPLICATION") COMPLICATION("COMPLICATION")
    
}

