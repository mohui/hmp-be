package com.bjknrt.operation.log.vo

import java.util.Objects
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
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
 * AddOperationLogParam
 * @param loginName 登录名 
 * @param createdBy  
 * @param operatorModel  
 * @param operatorAction  
 * @param operatorSystem 操作服务 
 * @param timeMillis  
 * @param ip ip 
 * @param orgId  
 * @param content  
 */
data class AddOperationLogParam(
    
    @field:JsonProperty("loginName", required = true) val loginName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("createdBy", required = true) val createdBy: Id,
    
    @field:Valid
    @field:JsonProperty("operatorModel", required = true) val operatorModel: LogModule,
    
    @field:Valid
    @field:JsonProperty("operatorAction", required = true) val operatorAction: LogAction,
    
    @field:JsonProperty("operatorSystem", required = true) val operatorSystem: kotlin.String,
    
    @field:JsonProperty("timeMillis", required = true) val timeMillis: kotlin.Long,

    @field:JsonProperty("ip") val ip: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("orgId") val orgId: java.math.BigInteger? = null,

    @field:JsonProperty("content") val content: kotlin.String? = null
) {

}

