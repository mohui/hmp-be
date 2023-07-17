package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * DiabetesStageStatisticsResponse
 * @param id  
 * @param stageReportId  
 * @param fastingBloodSugarMeasureNumber  本次空腹血糖测量次数
 * @param beforeLunchBloodSugarMeasureNumber  本次餐前血糖（中、晚餐前）血糖测量次数
 * @param afterMealBloodSugarBloodSugarMeasureNumber  本次餐后2个小时血糖测量次数
 * @param lowBloodSugarNumber  本次低血糖次数
 * @param bloodSugarStandardRate  
 * @param createdAt  
 * @param patientId  
 * @param patientName  患者姓名
 * @param patientAge  年龄
 * @param reportName  报告名称
 * @param reportStartDatetime  
 * @param reportEndDatetime  
 * @param abnormalDataAlertMsgList  异常数据提醒消息
 * @param bloodSugarStandardRateDeviationValue  
 * @param scoreDeviationValue  
 * @param reportScore  
 */
data class DiabetesStageStatisticsResponse(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("stageReportId", required = true) val stageReportId: java.math.BigInteger,
    
    @field:JsonProperty("fastingBloodSugarMeasureNumber", required = true) val fastingBloodSugarMeasureNumber: kotlin.Int,
    
    @field:JsonProperty("beforeLunchBloodSugarMeasureNumber", required = true) val beforeLunchBloodSugarMeasureNumber: kotlin.Int,
    
    @field:JsonProperty("afterMealBloodSugarBloodSugarMeasureNumber", required = true) val afterMealBloodSugarBloodSugarMeasureNumber: kotlin.Int,
    
    @field:JsonProperty("lowBloodSugarNumber", required = true) val lowBloodSugarNumber: kotlin.Int,
    
    @field:JsonProperty("bloodSugarStandardRate", required = true) val bloodSugarStandardRate: java.math.BigDecimal,
    
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

    @field:JsonProperty("bloodSugarStandardRateDeviationValue") val bloodSugarStandardRateDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("scoreDeviationValue") val scoreDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("reportScore") val reportScore: java.math.BigDecimal? = null
) {

}

