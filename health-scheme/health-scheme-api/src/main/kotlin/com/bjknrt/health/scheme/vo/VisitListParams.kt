package com.bjknrt.health.scheme.vo

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
 * visitListParams
 * @param patientId  
 * @param pageNo  
 * @param pageSize  
 */
data class VisitListParams(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long
) {

}

