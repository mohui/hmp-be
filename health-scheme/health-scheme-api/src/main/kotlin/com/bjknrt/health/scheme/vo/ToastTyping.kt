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
* 脑梗死TOAST分型-LAA,CE,SAA,SOE,SUE
* Values: LAA,CE,SAA,SOE,SUE
*/
enum class ToastTyping(val value: kotlin.String) {

    /**
     * LAA分型
     */
    @JsonProperty("LAA") LAA("LAA"),
    
    /**
     * CE分型
     */
    @JsonProperty("CE") CE("CE"),
    
    /**
     * SAA分型
     */
    @JsonProperty("SAA") SAA("SAA"),
    
    /**
     * SOE分型
     */
    @JsonProperty("SOE") SOE("SOE"),
    
    /**
     * SUE分型
     */
    @JsonProperty("SUE") SUE("SUE")
    
}

