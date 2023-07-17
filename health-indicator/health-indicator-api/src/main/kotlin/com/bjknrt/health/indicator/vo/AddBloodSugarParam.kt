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
 * AddBloodSugarParam
 * @param knPatientId  
 * @param fromTag  
 * @param knFastingBloodSandalwood  
 * @param knRandomBloodSugar  
 * @param knAfterMealBloodSugar  
 * @param knMeasureAt  
 * @param knBeforeLunchBloodSugar  
 * @param knBeforeDinnerBloodSugar  
 * @param knAfterLunchBloodSugar  
 * @param knAfterDinnerBloodSugar  
 * @param knBeforeSleepBloodSugar  
 */
data class AddBloodSugarParam(
    
    @field:Valid
    @field:JsonProperty("knPatientId", required = true) val knPatientId: Id,
    
    @field:Valid
    @field:JsonProperty("fromTag", required = true) val fromTag: FromTag,

    @field:JsonProperty("knFastingBloodSandalwood") val knFastingBloodSandalwood: java.math.BigDecimal? = null,

    @field:JsonProperty("knRandomBloodSugar") val knRandomBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knAfterMealBloodSugar") val knAfterMealBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knMeasureAt") val knMeasureAt: java.time.LocalDateTime? = null,

    @field:JsonProperty("knBeforeLunchBloodSugar") val knBeforeLunchBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knBeforeDinnerBloodSugar") val knBeforeDinnerBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knAfterLunchBloodSugar") val knAfterLunchBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knAfterDinnerBloodSugar") val knAfterDinnerBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knBeforeSleepBloodSugar") val knBeforeSleepBloodSugar: java.math.BigDecimal? = null
) {

}

