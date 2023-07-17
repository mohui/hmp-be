package com.bjknrt.medication.remind.vo

import java.util.Objects
import com.bjknrt.medication.remind.vo.HealthPlanType
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
 * batchTypeClockInParams
 * @param type  
 * @param patientId  
 * @param clockAt  
 */
data class BatchTypeClockInParams(
    
    @field:Valid
    @field:JsonProperty("type", required = true) val type: HealthPlanType,
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:JsonProperty("clockAt") val clockAt: java.time.LocalDateTime? = null
) {

}

