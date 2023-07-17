package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * CerebralStrokeStageStatisticsResponse
 * @param id  
 * @param stageReportId  
 * @param mrsScore  
 * @param barthelScore  
 * @param eq5dScore  
 * @param createdAt  
 * @param patientId  
 * @param patientName  患者姓名
 * @param patientAge  年龄
 * @param reportName  报告名称
 * @param reportStartDatetime  
 * @param reportEndDatetime  
 * @param abnormalDataAlertMsgList  异常数据提醒消息
 * @param mrsScoreDeviationValue  
 * @param barthelScoreDeviationValue  
 * @param eq5dScoreDeviationValue  
 * @param scoreDeviationValue  
 * @param reportScore  
 */
data class CerebralStrokeStageStatisticsResponse(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("stageReportId", required = true) val stageReportId: java.math.BigInteger,
    
    @field:JsonProperty("mrsScore", required = true) val mrsScore: java.math.BigDecimal,
    
    @field:JsonProperty("barthelScore", required = true) val barthelScore: java.math.BigDecimal,
    
    @field:JsonProperty("eq5dScore", required = true) val eq5dScore: java.math.BigDecimal,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: java.math.BigInteger,
    
    @field:JsonProperty("patientName", required = true) val patientName: kotlin.String,
    
    @field:JsonProperty("patientAge", required = true) val patientAge: kotlin.Int,
    
    @field:JsonProperty("reportName", required = true) val reportName: kotlin.String,
    
    @field:JsonProperty("reportStartDatetime", required = true) val reportStartDatetime: java.time.LocalDateTime,
    
    @field:JsonProperty("reportEndDatetime", required = true) val reportEndDatetime: java.time.LocalDateTime,
    
    @field:Valid
    @field:JsonProperty("abnormalDataAlertMsgList", required = true) val abnormalDataAlertMsgList: kotlin.collections.List<AbnormalDataAlertMsgResult>,

    @field:JsonProperty("mrsScoreDeviationValue") val mrsScoreDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("barthelScoreDeviationValue") val barthelScoreDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("eq5dScoreDeviationValue") val eq5dScoreDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("scoreDeviationValue") val scoreDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("reportScore") val reportScore: java.math.BigDecimal? = null
) {

}

