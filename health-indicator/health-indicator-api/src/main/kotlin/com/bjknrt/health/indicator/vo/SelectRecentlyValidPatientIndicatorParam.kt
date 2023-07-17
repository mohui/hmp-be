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
 * SelectRecentlyValidPatientIndicatorParam
 * @param patientId  
 * @param indicatorList 指标枚举集合 
 */
data class SelectRecentlyValidPatientIndicatorParam(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:Valid
    @field:JsonProperty("indicatorList", required = true) val indicatorList: kotlin.collections.List<IndicatorEnum>
) {

}

