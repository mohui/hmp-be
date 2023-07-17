package com.bjknrt.medication.remind.vo

import java.util.Objects
import com.bjknrt.medication.remind.vo.Frequency
import com.bjknrt.medication.remind.vo.HealthPlanType
import com.bjknrt.framework.api.vo.Id
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
 * 
 * @param name  健康计划名称
 * @param type  
 * @param patientId  
 * @param id  
 * @param subName  副标题
 * @param desc  计划描述
 * @param externalKey  用于饮食计划
 * @param cycleStartTime  
 * @param cycleEndTime  
 * @param displayTime  
 * @param group  分组字段
 * @param clockDisplay  打卡后是否显示
 * @param frequencys  频次
 */
data class FrequencyHealthParams(
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("type", required = true) val type: HealthPlanType,
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null,

    @field:JsonProperty("subName") val subName: kotlin.String? = null,

    @field:JsonProperty("desc") val desc: kotlin.String? = null,

    @field:JsonProperty("externalKey") val externalKey: kotlin.String? = null,

    @field:JsonProperty("cycleStartTime") val cycleStartTime: java.time.LocalDateTime? = null,

    @field:JsonProperty("cycleEndTime") val cycleEndTime: java.time.LocalDateTime? = null,

    @field:JsonProperty("displayTime") val displayTime: java.time.LocalDateTime? = null,

    @field:JsonProperty("group") val group: kotlin.String? = null,

    @field:JsonProperty("clockDisplay") val clockDisplay: kotlin.Boolean? = null,

    @field:Valid
    @field:JsonProperty("frequencys") val frequencys: kotlin.collections.List<Frequency>? = null
) {

}

