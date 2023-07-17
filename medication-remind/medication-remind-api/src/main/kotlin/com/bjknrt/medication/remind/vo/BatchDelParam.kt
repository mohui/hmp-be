package com.bjknrt.medication.remind.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * batchDelParam
 * @param patientId  
 * @param type  
 */
data class BatchDelParam(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:Valid
    @field:JsonProperty("type", required = true) val type: HealthPlanType
) {

}

