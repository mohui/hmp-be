package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * TypeGetListParam
 * @param type  
 */
data class TypeGetListParam(
    
    @field:Valid
    @field:JsonProperty("type", required = true) val type: DietPlanType
) {

}

