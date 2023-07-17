package com.bjknrt.user.permission.centre.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param totalPage  总页数
 * @param pageSize  每页显示条数
 * @param pageNo  当前数据页码
 * @param total  总共数据条数
 * @param _data HealthServiceActivationCode 数据列表
 */
data class HealthServiceActivationCodeListPost200Response(
    
    @field:JsonProperty("totalPage", required = true) val totalPage: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("total", required = true) val total: kotlin.Long,

    @field:Valid
    @field:JsonProperty("data") val _data: kotlin.collections.List<HealthServiceActivationCode>? = null
) {

}

