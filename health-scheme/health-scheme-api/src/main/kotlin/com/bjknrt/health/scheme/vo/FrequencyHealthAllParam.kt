package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.bjknrt.health.scheme.vo.AddDrugRemindParams
import com.bjknrt.health.scheme.vo.FrequencyHealthParams
import com.bjknrt.health.scheme.vo.HealthPlanType
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
 * frequencyHealthAllParam
 * @param patientId  
 * @param healthPlans  健康计划
 * @param type  
 * @param drugPlans  药品健康计划
 */
data class FrequencyHealthAllParam(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("healthPlans", required = true) val healthPlans: kotlin.collections.List<FrequencyHealthParams>,

    @field:Valid
    @field:JsonProperty("type") val type: HealthPlanType? = null,

    @field:Valid
    @field:JsonProperty("drugPlans") val drugPlans: kotlin.collections.List<AddDrugRemindParams>? = null
) {

}

