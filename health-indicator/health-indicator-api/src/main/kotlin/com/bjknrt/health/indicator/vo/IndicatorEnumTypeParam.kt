package com.bjknrt.health.indicator.vo

import java.util.Objects
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.health.indicator.vo.IndicatorEnum
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
 * IndicatorEnumTypeParam
 * @param patientId  
 * @param indicatorEnumType  
 * @param pageNo  页码
 * @param pageSize  每页条数
 */
data class IndicatorEnumTypeParam(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:Valid
    @field:JsonProperty("indicatorEnumType", required = true) val indicatorEnumType: IndicatorEnum,
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long
) {

}

