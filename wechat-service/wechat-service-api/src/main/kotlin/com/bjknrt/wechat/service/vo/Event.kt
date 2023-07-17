package com.bjknrt.wechat.service.vo

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
* Values: SUBSCRIBE,UNSUBSCRIBE
*/
enum class Event(val value: kotlin.String) {

    /**
     * 
     */
    @JsonProperty("subscribe") SUBSCRIBE("subscribe"),
    
    /**
     * 
     */
    @JsonProperty("unsubscribe") UNSUBSCRIBE("unsubscribe")
    
}

