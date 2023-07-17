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
* 五病综合标签（HIGH-高危,LOW-低危）
* Values: HIGH,LOW
*/
enum class PatientSynthesisTag(val value: kotlin.String) {

    /**
     * 高危
     */
    @JsonProperty("HIGH") HIGH("HIGH"),
    
    /**
     * 低危
     */
    @JsonProperty("LOW") LOW("LOW")
    
}

