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
* 摄盐情况. LIGHT: 轻; MIDDLE: 中; DEEP: 重
* Values: LIGHT,MIDDLE,DEEP
*/
enum class LifeSalt(val value: kotlin.String) {

    /**
     * 轻
     */
    @JsonProperty("LIGHT") LIGHT("LIGHT"),
    
    /**
     * 中
     */
    @JsonProperty("MIDDLE") MIDDLE("MIDDLE"),
    
    /**
     * 重
     */
    @JsonProperty("DEEP") DEEP("DEEP")
    
}

