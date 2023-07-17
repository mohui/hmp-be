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
 * HealthManagementItem
 * @param healthItemId  
 * @param healthItemCode  
 * @param healthItemName  
 */
data class HealthManagementItem(
    
    @field:Valid
    @field:JsonProperty("healthItemId", required = true) val healthItemId: Id,
    
    @field:JsonProperty("healthItemCode", required = true) val healthItemCode: kotlin.String,
    
    @field:JsonProperty("healthItemName", required = true) val healthItemName: kotlin.String
) {

}

