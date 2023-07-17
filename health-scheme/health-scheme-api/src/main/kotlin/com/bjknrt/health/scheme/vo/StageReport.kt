package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * StageReport
 * @param id  
 * @param healthSchemeManagementInfoId  
 * @param patientId  
 * @param patientName  患者名称
 * @param age  患者年龄
 * @param reportName  报告名称
 * @param reportStartDatetime  
 * @param reportEndDatetime  
 * @param createdAt  
 * @param stageReportType  
 * @param reportScore  
 * @param failMsg  创建阶段报告失败的原因
 */
data class StageReport(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("healthSchemeManagementInfoId", required = true) val healthSchemeManagementInfoId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,
    
    @field:JsonProperty("patientName", required = true) val patientName: kotlin.String,
    
    @field:JsonProperty("age", required = true) val age: kotlin.Int,
    
    @field:JsonProperty("reportName", required = true) val reportName: kotlin.String,
    
    @field:JsonProperty("reportStartDatetime", required = true) val reportStartDatetime: java.time.LocalDateTime,
    
    @field:JsonProperty("reportEndDatetime", required = true) val reportEndDatetime: java.time.LocalDateTime,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,
    
    @field:Valid
    @field:JsonProperty("stageReportType", required = true) val stageReportType: StageReportType,

    @field:JsonProperty("reportScore") val reportScore: java.math.BigDecimal? = null,

    @field:JsonProperty("failMsg") val failMsg: kotlin.String? = null
) {

}

