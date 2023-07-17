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
* 梗死部位-大脑前动脉供血区:BRAIN_BEFORE,大脑中动脉供血区:BRAIN_MIDDLE,后循环多发梗死:AFTER_CYCLE,大面积脑梗死:LARGE_AREA_BRAIN_INFARCT
* Values: BRAIN_BEFORE,BRAIN_MIDDLE,AFTER_CYCLE,LARGE_AREA_BRAIN_INFARCT
*/
enum class InfarctionSite(val value: kotlin.String) {

    /**
     * 大脑前动脉供血区
     */
    @JsonProperty("BRAIN_BEFORE") BRAIN_BEFORE("BRAIN_BEFORE"),
    
    /**
     * 大脑中动脉供血区
     */
    @JsonProperty("BRAIN_MIDDLE") BRAIN_MIDDLE("BRAIN_MIDDLE"),
    
    /**
     * 后循环多发梗死
     */
    @JsonProperty("AFTER_CYCLE") AFTER_CYCLE("AFTER_CYCLE"),
    
    /**
     * 大面积脑梗死
     */
    @JsonProperty("LARGE_AREA_BRAIN_INFARCT") LARGE_AREA_BRAIN_INFARCT("LARGE_AREA_BRAIN_INFARCT")
    
}

