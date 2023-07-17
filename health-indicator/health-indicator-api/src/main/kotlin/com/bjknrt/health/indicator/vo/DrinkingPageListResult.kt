package com.bjknrt.health.indicator.vo

import java.util.Objects
import com.bjknrt.health.indicator.vo.DrinkingResult
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
 * DrinkingPageListResult
 * @param totalPage 总页数 
 * @param pageSize 每页显示条数 
 * @param pageNo 当前数据页码 
 * @param total 总共数据条数 
 * @param _data  
 */
data class DrinkingPageListResult(
    
    @field:JsonProperty("totalPage", required = true) val totalPage: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("total", required = true) val total: kotlin.Long,

    @field:Valid
    @field:JsonProperty("data") val _data: kotlin.collections.List<DrinkingResult>? = null
) {

}

