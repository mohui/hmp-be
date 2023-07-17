package com.bjknrt.health.scheme.vo

import java.util.Objects
import com.bjknrt.health.scheme.vo.HealthManageResultHypertension
import com.bjknrt.health.scheme.vo.HealthManageResultStage
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
 * HealthManageResult
 * @param patientId  
 * @param createdBy  
 * @param createdAt  
 * @param diabetes  血糖计划
 * @param visits  随访计划
 * @param sports  运动计划
 * @param sportTabooList  运动禁忌
 * @param foods  饮食计划
 * @param evaluates  评估计划
 * @param rehabilitations  康复计划
 * @param pulseOxygenSaturations  脉搏氧饱和度计划
 * @param popularSciences  科普计划
 * @param healthExaminations  体检计划
 * @param vaccinations  疫苗接种计划
 * @param drugPrograms  用药评估
 * @param dietEvaluate  饮食评估
 * @param stage  
 * @param hypertension  
 * @param reportDate  
 */
data class HealthManageResult(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("createdBy", required = true) val createdBy: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDate,
    
    @field:JsonProperty("diabetes", required = true) val diabetes: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("visits", required = true) val visits: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("sports", required = true) val sports: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("sportTabooList", required = true) val sportTabooList: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("foods", required = true) val foods: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("evaluates", required = true) val evaluates: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("rehabilitations", required = true) val rehabilitations: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("pulseOxygenSaturations", required = true) val pulseOxygenSaturations: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("popularSciences", required = true) val popularSciences: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("healthExaminations", required = true) val healthExaminations: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("vaccinations", required = true) val vaccinations: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("drugPrograms", required = true) val drugPrograms: kotlin.collections.List<kotlin.String>,
    
    @field:JsonProperty("dietEvaluate", required = true) val dietEvaluate: kotlin.collections.List<kotlin.String>,

    @field:Valid
    @field:JsonProperty("stage") val stage: HealthManageResultStage? = null,

    @field:Valid
    @field:JsonProperty("hypertension") val hypertension: HealthManageResultHypertension? = null,

    @field:Valid
    @field:JsonProperty("reportDate") val reportDate: java.time.LocalDate? = null
) {

}

