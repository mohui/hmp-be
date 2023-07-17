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
* 低血糖反应. NO: 无; OCCASIONALLY: 偶尔; FREQUENTLY: 频繁
* Values: NO,OCCASIONALLY,FREQUENTLY
*/
enum class Rhg(val value: kotlin.String) {

    /**
     * 无
     */
    @JsonProperty("NO") NO("NO"),
    
    /**
     * 偶尔
     */
    @JsonProperty("OCCASIONALLY") OCCASIONALLY("OCCASIONALLY"),
    
    /**
     * 频繁
     */
    @JsonProperty("FREQUENTLY") FREQUENTLY("FREQUENTLY")
    
}

