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
 * AcuteCoronaryDiseaseStageStatisticsDetail
 * @param id  
 * @param stageReportId  
 * @param systolicBloodPressure  
 * @param diastolicBloodPressure  
 * @param measureDatetime  
 * @param createdAt  
 */
data class AcuteCoronaryDiseaseStageStatisticsDetail(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("stageReportId", required = true) val stageReportId: java.math.BigInteger,
    
    @field:JsonProperty("systolicBloodPressure", required = true) val systolicBloodPressure: java.math.BigDecimal,
    
    @field:JsonProperty("diastolicBloodPressure", required = true) val diastolicBloodPressure: java.math.BigDecimal,
    
    @field:JsonProperty("measureDatetime", required = true) val measureDatetime: java.time.LocalDateTime,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime
) {

}

