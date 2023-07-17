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
 * 
 * @param id  
 * @param type  
 * @param cycleStartTime  
 * @param cycleEndTime  
 * @param frequencyIds  
 */
data class UpsertHealthFrequencyResult(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:Valid
    @field:JsonProperty("type", required = true) val type: HealthPlanType,
    
    @field:JsonProperty("cycleStartTime", required = true) val cycleStartTime: java.time.LocalDateTime,

    @field:JsonProperty("cycleEndTime") val cycleEndTime: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("frequencyIds") val frequencyIds: kotlin.collections.List<Id>? = null
) {

}

