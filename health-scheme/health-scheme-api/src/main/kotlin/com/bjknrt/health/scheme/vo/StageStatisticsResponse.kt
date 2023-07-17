package com.bjknrt.health.scheme.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * StageStatisticsResponse
 * @param id  
 * @param stageReportId  
 * @param actualMeasureNumber  本次测量次数
 * @param standardMeasureNumber  本次达标测量次数
 * @param systolicBloodPressureAvg  
 * @param diastolicBloodPressureAvg  
 * @param systolicBloodPressureStandardDeviation  
 * @param diastolicBloodPressureStandardDeviation  
 * @param bloodPressureFillRate  
 * @param bloodPressureStandardRate  
 * @param systolicBloodPressureUpperLimitNum  本次超收缩压上限的次数
 * @param diastolicBloodPressureLowerLimitNum  本次超舒张压下限的次数
 * @param lowBloodPressureNum  本次低血压次数
 * @param isBloodPressureAvgStandard  本次血压平均值是否达标
 * @param createdAt  
 * @param patientId  
 * @param patientName  患者姓名
 * @param patientAge  年龄
 * @param reportName  报告名称
 * @param reportStartDatetime  
 * @param reportEndDatetime  
 * @param abnormalDataAlertMsgList  异常数据提醒消息
 * @param actualMeasureNumberDeviationValue  本次测量(上传)次数与上次测量(上传)次数的差
 * @param standardMeasureNumberDeviationValue  本次达标次数与上次达标次数的差
 * @param systolicBloodPressureAvgDeviationValue  
 * @param diastolicBloodPressureAvgDeviationValue  
 * @param systolicBloodPressureStandardDeviationValue  
 * @param diastolicBloodPressureStandardDeviationValue  
 * @param scoreDeviationValue  
 * @param standardRateDeviationValue  
 * @param reportScore  
 */
data class StageStatisticsResponse(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("stageReportId", required = true) val stageReportId: java.math.BigInteger,
    
    @field:JsonProperty("actualMeasureNumber", required = true) val actualMeasureNumber: kotlin.Int,
    
    @field:JsonProperty("standardMeasureNumber", required = true) val standardMeasureNumber: kotlin.Int,
    
    @field:JsonProperty("systolicBloodPressureAvg", required = true) val systolicBloodPressureAvg: java.math.BigDecimal,
    
    @field:JsonProperty("diastolicBloodPressureAvg", required = true) val diastolicBloodPressureAvg: java.math.BigDecimal,
    
    @field:JsonProperty("systolicBloodPressureStandardDeviation", required = true) val systolicBloodPressureStandardDeviation: java.math.BigDecimal,
    
    @field:JsonProperty("diastolicBloodPressureStandardDeviation", required = true) val diastolicBloodPressureStandardDeviation: java.math.BigDecimal,
    
    @field:JsonProperty("bloodPressureFillRate", required = true) val bloodPressureFillRate: java.math.BigDecimal,
    
    @field:JsonProperty("bloodPressureStandardRate", required = true) val bloodPressureStandardRate: java.math.BigDecimal,
    
    @field:JsonProperty("systolicBloodPressureUpperLimitNum", required = true) val systolicBloodPressureUpperLimitNum: kotlin.Int,
    
    @field:JsonProperty("diastolicBloodPressureLowerLimitNum", required = true) val diastolicBloodPressureLowerLimitNum: kotlin.Int,
    
    @field:JsonProperty("lowBloodPressureNum", required = true) val lowBloodPressureNum: kotlin.Int,
    
    @field:JsonProperty("isBloodPressureAvgStandard", required = true) val isBloodPressureAvgStandard: kotlin.Boolean,
    
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

    @field:JsonProperty("actualMeasureNumberDeviationValue") val actualMeasureNumberDeviationValue: kotlin.Int? = null,

    @field:JsonProperty("standardMeasureNumberDeviationValue") val standardMeasureNumberDeviationValue: kotlin.Int? = null,

    @field:JsonProperty("systolicBloodPressureAvgDeviationValue") val systolicBloodPressureAvgDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("diastolicBloodPressureAvgDeviationValue") val diastolicBloodPressureAvgDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("systolicBloodPressureStandardDeviationValue") val systolicBloodPressureStandardDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("diastolicBloodPressureStandardDeviationValue") val diastolicBloodPressureStandardDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("scoreDeviationValue") val scoreDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("standardRateDeviationValue") val standardRateDeviationValue: java.math.BigDecimal? = null,

    @field:JsonProperty("reportScore") val reportScore: java.math.BigDecimal? = null
) {

}

