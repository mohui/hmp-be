package com.bjknrt.health.scheme.vo

import java.util.Objects
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
 * DiabetesStageStatisticsDetail
 * @param id  
 * @param stageReportId  
 * @param measureDatetime  
 * @param createdAt  
 * @param fastingBloodSugar  
 * @param randomBloodSugar  
 * @param afterMealBloodSugar  
 * @param beforeLunchBloodSugar  
 * @param beforeDinnerBloodSugar  
 * @param afterLunchBloodSugar  
 * @param afterDinnerBloodSugar  
 * @param beforeSleepBloodSugar  
 */
data class DiabetesStageStatisticsDetail(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("stageReportId", required = true) val stageReportId: java.math.BigInteger,
    
    @field:JsonProperty("measureDatetime", required = true) val measureDatetime: java.time.LocalDateTime,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,

    @field:JsonProperty("fastingBloodSugar") val fastingBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("randomBloodSugar") val randomBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("afterMealBloodSugar") val afterMealBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("beforeLunchBloodSugar") val beforeLunchBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("beforeDinnerBloodSugar") val beforeDinnerBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("afterLunchBloodSugar") val afterLunchBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("afterDinnerBloodSugar") val afterDinnerBloodSugar: java.math.BigDecimal? = null,

    @field:JsonProperty("beforeSleepBloodSugar") val beforeSleepBloodSugar: java.math.BigDecimal? = null
) {

}

