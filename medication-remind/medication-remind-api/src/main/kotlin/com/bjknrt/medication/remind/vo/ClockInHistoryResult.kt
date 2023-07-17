package com.bjknrt.medication.remind.vo

import java.util.Objects
import com.bjknrt.framework.api.vo.Id
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
 * @param healthPlanId  
 * @param healthPlanName  健康计划名称
 * @param clockInAt  
 */
data class ClockInHistoryResult(
    
    @field:Valid
    @field:JsonProperty("healthPlanId", required = true) val healthPlanId: Id,
    
    @field:JsonProperty("healthPlanName", required = true) val healthPlanName: kotlin.String,
    
    @field:JsonProperty("clockInAt", required = true) val clockInAt: java.time.LocalDateTime
) {

}

