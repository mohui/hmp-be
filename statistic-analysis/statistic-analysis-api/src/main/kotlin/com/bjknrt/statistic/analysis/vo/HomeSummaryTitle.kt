package com.bjknrt.statistic.analysis.vo

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
 * HomeSummaryTitle
 * @param peopleNumber  慢病管理人数
 * @param patientNumber  日活跃患者数
 * @param serviceRegisterNumber  服务包订阅人数
 * @param medicalPersonnelNumber  医务人数
 * @param fiveDiseaseNumber  五病评估人数
 */
data class HomeSummaryTitle(
    
    @field:JsonProperty("peopleNumber", required = true) val peopleNumber: kotlin.Int,
    
    @field:JsonProperty("patientNumber", required = true) val patientNumber: kotlin.Int,
    
    @field:JsonProperty("serviceRegisterNumber", required = true) val serviceRegisterNumber: kotlin.Int,
    
    @field:JsonProperty("medicalPersonnelNumber", required = true) val medicalPersonnelNumber: kotlin.Int,
    
    @field:JsonProperty("fiveDiseaseNumber", required = true) val fiveDiseaseNumber: kotlin.Int
) {

}

