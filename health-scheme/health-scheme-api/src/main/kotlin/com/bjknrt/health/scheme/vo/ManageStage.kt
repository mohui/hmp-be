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
* Values: INITIAL_STAGE,STABLE_STAGE,METAPHASE_STABLE_STAGE,SECULAR_STABLE_STAGE
*/
enum class ManageStage(val value: kotlin.String) {

    /**
     * 初始监测阶段
     */
    @JsonProperty("INITIAL_STAGE") INITIAL_STAGE("INITIAL_STAGE"),
    
    /**
     * 短期稳定阶段
     */
    @JsonProperty("STABLE_STAGE") STABLE_STAGE("STABLE_STAGE"),
    
    /**
     * 中期稳定阶段
     */
    @JsonProperty("METAPHASE_STABLE_STAGE") METAPHASE_STABLE_STAGE("METAPHASE_STABLE_STAGE"),
    
    /**
     * 长期稳定阶段
     */
    @JsonProperty("SECULAR_STABLE_STAGE") SECULAR_STABLE_STAGE("SECULAR_STABLE_STAGE")
    
}

