package com.bjknrt.medication.guide.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Size
import javax.validation.Valid

/**
 * 
 * @param generic  
 * @param crowd  
 */
data class SubmitGenericRequest(
    
    @field:Valid
    @get:Size(min=1)
    @field:JsonProperty("generic", required = true) val generic: kotlin.collections.List<SubmitGenericRequestGenericInner>,

    @field:Valid
    @field:JsonProperty("crowd") val crowd: kotlin.collections.List<Crowd>? = null
) {

}

