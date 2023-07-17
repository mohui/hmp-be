package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * ActivationCodeListParam
 * @param pageNo  页码
 * @param pageSize  每页条数
 * @param createdBy  创建人id
 * @param serviceIds  服务包id
 * @param createdDateMin  
 * @param createdDateMax  
 * @param status  
 */
data class ActivationCodeListParam(
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,

    @field:Valid
    @field:JsonProperty("createdBy") val createdBy: kotlin.collections.List<Id>? = null,

    @field:Valid
    @field:JsonProperty("serviceIds") val serviceIds: kotlin.collections.List<Id>? = null,

    @field:JsonProperty("createdDateMin") val createdDateMin: java.time.LocalDateTime? = null,

    @field:JsonProperty("createdDateMax") val createdDateMax: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("status") val status: HealthServiceActivationCodeStatus? = null
) {

}

