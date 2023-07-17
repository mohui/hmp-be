package com.bjknrt.health.scheme.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * visitListParam
 * @param patientId  
 * @param pageNo  
 * @param pageSize  
 * @param type  
 * @param isDoctor
 */
data class VisitListParam(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,

    @field:Valid
    @field:JsonProperty("type") val type: kotlin.collections.List<VisitType>? = null,

    @field:JsonProperty("isDoctor") val isDoctor: kotlin.Boolean? = null,
) {

}

