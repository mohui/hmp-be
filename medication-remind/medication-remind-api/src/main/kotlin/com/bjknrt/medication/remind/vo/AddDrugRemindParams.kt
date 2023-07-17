package com.bjknrt.medication.remind.vo

import java.util.Objects
import com.bjknrt.medication.remind.vo.HealthPlanType
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.medication.remind.vo.Week
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
 * AddDrugRemindParams
 * @param patientId  
 * @param drugName  药品名称
 * @param isUsed  是否需要我们每天提醒
 * @param time Time 时间
 * @param type  
 * @param frequencys  周一到周日
 * @param cycleStartTime  
 * @param subName  剂量
 * @param cycleEndTime  
 */
data class AddDrugRemindParams(
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("drugName", required = true) val drugName: kotlin.String,
    
    @field:JsonProperty("isUsed", required = true) val isUsed: kotlin.Boolean,
    
    @field:Valid
    @field:JsonProperty("time", required = true) val time: java.time.LocalTime,
    
    @field:Valid
    @field:JsonProperty("type", required = true) val type: HealthPlanType,
    
    @field:Valid
    @field:JsonProperty("frequencys", required = true) val frequencys: kotlin.collections.List<Week>,
    
    @field:JsonProperty("cycleStartTime", required = true) val cycleStartTime: java.time.LocalDateTime,

    @field:JsonProperty("subName") val subName: kotlin.String? = null,

    @field:JsonProperty("cycleEndTime") val cycleEndTime: java.time.LocalDateTime? = null
) {

}

