package com.bjknrt.operation.log.vo

import java.util.Objects
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
 * OperationLogPageParam
 * @param startTime  
 * @param endTime  
 * @param pageNo  页码
 * @param pageSize  每页条数
 * @param loginName 登录名 
 */
data class OperationLogPageParam(
    
    @field:JsonProperty("startTime", required = true) val startTime: java.time.LocalDateTime,
    
    @field:JsonProperty("endTime", required = true) val endTime: java.time.LocalDateTime,
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,

    @field:JsonProperty("loginName") val loginName: kotlin.String? = null
) {

}

