package com.bjknrt.health.indicator.vo

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
 * PatientIndicatorResult
 * @param patientId  
 * @param knBodyHeight  
 * @param knBodyWeight  
 * @param knBmi  
 * @param knWaistline  
 * @param knSystolicBloodPressure  
 * @param knDiastolicBloodPressure  
 * @param knFastingBloodSandalwood  
 * @param knBeforeLunchBloodSugar  
 * @param knBeforeDinnerBloodSugar  
 * @param knRandomBloodSugar  
 * @param knAfterMealBloodSugar  
 * @param knAfterLunchBloodSugar  
 * @param knAfterDinnerBloodSugar  
 * @param knBeforeSleepBloodSugar  
 * @param knTotalCholesterol  
 * @param knTriglycerides  
 * @param knLowDensityLipoprotein  
 * @param knHighDensityLipoprotein  
 * @param knNum  吸烟几支/日
 * @param knWhiteSpirit  
 * @param knBeer  
 * @param knWine  
 * @param knYellowRiceSpirit  
 * @param knPulseOximetry  
 * @param knHeartRate  心率/次/分
 * @param knPulse  脉搏/次/分
 * @param knCelsius  
 */
data class PatientIndicatorResult(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:JsonProperty("knBodyHeight") val knBodyHeight: java.math.BigDecimal? = null,

    @field:JsonProperty("knBodyWeight") val knBodyWeight: java.math.BigDecimal? = null,

    @field:JsonProperty("knBmi") val knBmi: java.math.BigDecimal? = null,

    @field:JsonProperty("knWaistline") val knWaistline: java.math.BigDecimal? = null,

    @field:JsonProperty("knSystolicBloodPressure") val knSystolicBloodPressure: java.math.BigDecimal? = null,

    @field:JsonProperty("knDiastolicBloodPressure") val knDiastolicBloodPressure: java.math.BigDecimal? = null,

    @field:JsonProperty("knFastingBloodSandalwood") val knFastingBloodSandalwood: java.math.BigDecimal? = null,

    @field:JsonProperty("knBeforeLunchBloodSugar") val knBeforeLunchBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knBeforeDinnerBloodSugar") val knBeforeDinnerBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knRandomBloodSugar") val knRandomBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knAfterMealBloodSugar") val knAfterMealBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knAfterLunchBloodSugar") val knAfterLunchBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knAfterDinnerBloodSugar") val knAfterDinnerBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knBeforeSleepBloodSugar") val knBeforeSleepBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("knTotalCholesterol") val knTotalCholesterol: java.math.BigDecimal? = null,

    @field:JsonProperty("knTriglycerides") val knTriglycerides: java.math.BigDecimal? = null,

    @field:JsonProperty("knLowDensityLipoprotein") val knLowDensityLipoprotein: java.math.BigDecimal? = null,

    @field:JsonProperty("knHighDensityLipoprotein") val knHighDensityLipoprotein: java.math.BigDecimal? = null,

    @field:JsonProperty("knNum") val knNum: kotlin.Int? = null,

    @field:JsonProperty("knWhiteSpirit") val knWhiteSpirit: java.math.BigDecimal? = null,

    @field:JsonProperty("knBeer") val knBeer: java.math.BigDecimal? = null,

    @field:JsonProperty("knWine") val knWine: java.math.BigDecimal? = null,

    @field:JsonProperty("knYellowRiceSpirit") val knYellowRiceSpirit: java.math.BigDecimal? = null,

    @field:JsonProperty("knPulseOximetry") val knPulseOximetry: java.math.BigDecimal? = null,

    @field:JsonProperty("knHeartRate") val knHeartRate: kotlin.Int? = null,

    @field:JsonProperty("knPulse") val knPulse: kotlin.Int? = null,

    @field:JsonProperty("knCelsius") val knCelsius: java.math.BigDecimal? = null
) {

}

