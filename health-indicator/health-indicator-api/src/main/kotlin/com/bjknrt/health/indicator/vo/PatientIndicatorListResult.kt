package com.bjknrt.health.indicator.vo

import java.util.Objects
import com.bjknrt.health.indicator.vo.BloodLipidsResult
import com.bjknrt.health.indicator.vo.BloodPressureResult
import com.bjknrt.health.indicator.vo.BloodSugarResult
import com.bjknrt.health.indicator.vo.BodyHeightResult
import com.bjknrt.health.indicator.vo.BodyWeightResult
import com.bjknrt.health.indicator.vo.SmokeResult
import com.bjknrt.health.indicator.vo.WaistLineResult
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
 * PatientIndicatorListResult
 * @param bodyHeightListResult  
 * @param bodyWeightListResult  
 * @param knWaistlineListResult  
 * @param bloodSugarListResult  
 * @param bloodPressureListResult  
 * @param bloodLipidsListResult  
 * @param smokeListResult  
 */
data class PatientIndicatorListResult(
    
    @field:Valid
    @field:JsonProperty("bodyHeightListResult", required = true) val bodyHeightListResult: kotlin.collections.List<BodyHeightResult>,
    
    @field:Valid
    @field:JsonProperty("bodyWeightListResult", required = true) val bodyWeightListResult: kotlin.collections.List<BodyWeightResult>,
    
    @field:Valid
    @field:JsonProperty("knWaistlineListResult", required = true) val knWaistlineListResult: kotlin.collections.List<WaistLineResult>,
    
    @field:Valid
    @field:JsonProperty("bloodSugarListResult", required = true) val bloodSugarListResult: kotlin.collections.List<BloodSugarResult>,
    
    @field:Valid
    @field:JsonProperty("bloodPressureListResult", required = true) val bloodPressureListResult: kotlin.collections.List<BloodPressureResult>,
    
    @field:Valid
    @field:JsonProperty("bloodLipidsListResult", required = true) val bloodLipidsListResult: kotlin.collections.List<BloodLipidsResult>,
    
    @field:Valid
    @field:JsonProperty("smokeListResult", required = true) val smokeListResult: kotlin.collections.List<SmokeResult>
) {

}

