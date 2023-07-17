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
* 状态:GOOD: 良好; ORDINARY: 一般; BAD: 差
* Values: GOOD,ORDINARY,BAD
*/
enum class LifeState(val value: kotlin.String) {

    /**
     * 良好
     */
    @JsonProperty("GOOD") GOOD("GOOD"),
    
    /**
     * 一般
     */
    @JsonProperty("ORDINARY") ORDINARY("ORDINARY"),
    
    /**
     * 差
     */
    @JsonProperty("BAD") BAD("BAD")
    
}

