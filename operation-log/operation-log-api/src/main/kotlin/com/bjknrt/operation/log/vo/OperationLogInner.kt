package com.bjknrt.operation.log.vo

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
 * @param loginName 登录名 
 * @param operatorModel 操作模块 
 * @param operatorAction 操作行为 
 * @param operatorSystem 操作服务 
 * @param timeMillis  
 * @param id  
 * @param createdAt  
 * @param ip ip地址 
 * @param context  操作内容
 */
data class OperationLogInner(
    
    @field:JsonProperty("loginName", required = true) val loginName: kotlin.String,
    
    @field:JsonProperty("operatorModel", required = true) val operatorModel: kotlin.String,
    
    @field:JsonProperty("operatorAction", required = true) val operatorAction: kotlin.String,
    
    @field:JsonProperty("operatorSystem", required = true) val operatorSystem: kotlin.String,
    
    @field:JsonProperty("timeMillis", required = true) val timeMillis: kotlin.Long,
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,

    @field:JsonProperty("ip") val ip: kotlin.String? = null,

    @field:JsonProperty("context") val context: kotlin.String? = null
) {

}

