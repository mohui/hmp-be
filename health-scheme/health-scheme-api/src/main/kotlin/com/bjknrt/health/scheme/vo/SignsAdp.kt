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
* NORMAL-正常,RECEDE-减弱,VANISH-消失
* Values: NORMAL,RECEDE,VANISH
*/
enum class SignsAdp(val value: kotlin.String) {

    /**
     * 正常
     */
    @JsonProperty("NORMAL") NORMAL("NORMAL"),
    
    /**
     * 减弱
     */
    @JsonProperty("RECEDE") RECEDE("RECEDE"),
    
    /**
     * 消失
     */
    @JsonProperty("VANISH") VANISH("VANISH")
    
}

