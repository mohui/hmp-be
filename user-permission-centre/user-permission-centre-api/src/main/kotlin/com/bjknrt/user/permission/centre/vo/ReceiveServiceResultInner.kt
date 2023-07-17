package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param id  
 * @param healthServiceId  
 * @param healthServiceCode  
 * @param healthServiceName  
 * @param expireDate  
 */
data class ReceiveServiceResultInner(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:Valid
    @field:JsonProperty("healthServiceId", required = true) val healthServiceId: Id,
    
    @field:JsonProperty("healthServiceCode", required = true) val healthServiceCode: kotlin.String,
    
    @field:JsonProperty("healthServiceName", required = true) val healthServiceName: kotlin.String,

    @field:JsonProperty("expireDate") val expireDate: java.time.LocalDateTime? = null
) {

}

