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
* Values: SAA,DA,AA,UM
*/
enum class PreventionDrug(val value: kotlin.String) {

    /**
     * 单药抗血小板
     */
    @JsonProperty("SAA") SAA("SAA"),
    
    /**
     * 双联抗血小板
     */
    @JsonProperty("DA") DA("DA"),
    
    /**
     * 抗凝抗血小板+抗凝
     */
    @JsonProperty("AA") AA("AA"),
    
    /**
     * 未标准用药
     */
    @JsonProperty("UM") UM("UM")
    
}

