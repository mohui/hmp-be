package com.bjknrt.medication.guide.vo

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonFormat
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
 * 
 * @param id  
 * @param name  
 * @param subTitle  
 * @param url  
 * @param initial  
 */
data class Inner(
    
    @field:JsonProperty("id", required = true) val id: kotlin.Long,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:JsonProperty("subTitle", required = true) val subTitle: kotlin.String,
    
    @field:JsonProperty("url", required = true) val url: kotlin.String,

    @field:JsonProperty("initial") val initial: kotlin.String? = null
) {

}

