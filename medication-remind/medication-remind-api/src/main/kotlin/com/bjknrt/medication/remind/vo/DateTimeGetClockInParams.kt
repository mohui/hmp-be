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
 * dateTimeGetClockInParams
 * @param startTime  
 * @param endTime  
 * @param healthPlanId  
 */
data class DateTimeGetClockInParams(
    
    @field:JsonProperty("startTime", required = true) val startTime: java.time.LocalDateTime,
    
    @field:JsonProperty("endTime", required = true) val endTime: java.time.LocalDateTime,
    
    @field:Valid
    @field:JsonProperty("healthPlanId", required = true) val healthPlanId: Id
) {

}

