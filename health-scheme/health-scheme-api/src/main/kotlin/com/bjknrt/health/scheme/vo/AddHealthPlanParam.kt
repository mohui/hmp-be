package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.bjknrt.health.scheme.vo.AddDrugRemindParams
import com.bjknrt.health.scheme.vo.FrequencyHealthParams
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
 * @param healthPlans  健康计划
 * @param drugPlans  药品健康计划
 */
data class AddHealthPlanParam(
    
    @field:Valid
    @field:JsonProperty("healthPlans", required = true) val healthPlans: kotlin.collections.List<FrequencyHealthParams>,
    
    @field:Valid
    @field:JsonProperty("drugPlans", required = true) val drugPlans: kotlin.collections.List<AddDrugRemindParams>
) {

}

