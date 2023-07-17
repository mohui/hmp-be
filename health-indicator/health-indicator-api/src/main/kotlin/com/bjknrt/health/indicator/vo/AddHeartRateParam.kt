package com.bjknrt.health.indicator.vo

import java.util.Objects
import com.bjknrt.health.indicator.vo.FromTag
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
 * AddHeartRateParam
 * @param knPatientId  
 * @param knHeartRate 心率/次/分 
 * @param fromTag  
 * @param knMeasureAt  
 */
data class AddHeartRateParam(
    
    @field:Valid
    @field:JsonProperty("knPatientId", required = true) val knPatientId: Id,
    
    @field:JsonProperty("knHeartRate", required = true) val knHeartRate: kotlin.Int,
    
    @field:Valid
    @field:JsonProperty("fromTag", required = true) val fromTag: FromTag,

    @field:JsonProperty("knMeasureAt") val knMeasureAt: java.time.LocalDateTime? = null
) {

}

