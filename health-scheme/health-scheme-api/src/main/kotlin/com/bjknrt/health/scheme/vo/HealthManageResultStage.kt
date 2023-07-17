package com.bjknrt.health.scheme.vo

import java.util.Objects
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
 * @param title  阶段名称
 * @param desc  阶段说明
 */
data class HealthManageResultStage(
    
    @field:JsonProperty("title", required = true) val title: kotlin.String,
    
    @field:JsonProperty("desc", required = true) val desc: kotlin.String
) {

}

