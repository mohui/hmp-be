package com.bjknrt.user.permission.centre.vo

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
 * @param id  
 * @param code  
 * @param name  
 * @param duringTime  
 */
data class HealthServiceGetActivationCodePost200ResponseHealthServicesInner(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("code", required = true) val code: kotlin.String,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("duringTime") val duringTime: kotlin.Int? = null
) {

}
