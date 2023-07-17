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
* 随访方式. SELF. 患者自填. OUTPATIENT. 门诊; FAMILY. 家庭; PHONE. 电话,ONLINE:线上
* Values: SELF,OUTPATIENT,FAMILY,PHONE,ONLINE
*/
enum class FllowWay(val value: kotlin.String) {

    /**
     * 患者自填
     */
    @JsonProperty("SELF") SELF("SELF"),
    
    /**
     * 门诊
     */
    @JsonProperty("OUTPATIENT") OUTPATIENT("OUTPATIENT"),
    
    /**
     * 家庭
     */
    @JsonProperty("FAMILY") FAMILY("FAMILY"),
    
    /**
     * 电话
     */
    @JsonProperty("PHONE") PHONE("PHONE"),
    
    /**
     * 线上
     */
    @JsonProperty("ONLINE") ONLINE("ONLINE")
    
}

