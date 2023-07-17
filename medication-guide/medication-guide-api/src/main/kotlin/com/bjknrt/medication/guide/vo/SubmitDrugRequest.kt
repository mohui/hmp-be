package com.bjknrt.medication.guide.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param patientId  
 * @param medicationOrder  
 */
data class SubmitDrugRequest(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:Valid
    @field:JsonProperty("medicationOrder", required = true) val medicationOrder: List<MedicationOrderInner>
) {

}

