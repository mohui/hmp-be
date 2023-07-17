package com.bjknrt.medication.remind.vo

import java.util.Objects
import com.bjknrt.medication.remind.vo.FrequencyClock
import com.bjknrt.medication.remind.vo.HealthPlanType
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
 * HealthPlan
 * @param id  主键id
 * @param name  健康计划名称
 * @param type  
 * @param clockIn  true:打卡,false:未打卡
 * @param cycleStartTime  
 * @param subName  副名称,如:有氧运动-游泳,的游泳
 * @param desc  打卡说明
 * @param time Time 时间
 * @param externalKey  扩展参数（目前只有饮食计划使用）
 * @param group  分组
 * @param cycleEndTime  
 * @param todayFrequency  
 * @param subFrequency  
 * @param mainFrequency  
 */
data class HealthPlan(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("type", required = true) val type: HealthPlanType,
    
    @field:JsonProperty("clockIn", required = true) val clockIn: kotlin.Boolean,
    
    @field:JsonProperty("cycleStartTime", required = true) val cycleStartTime: java.time.LocalDateTime,

    @field:JsonProperty("subName") val subName: kotlin.String? = null,

    @field:JsonProperty("desc") val desc: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("time") val time: java.time.LocalTime? = null,

    @field:JsonProperty("externalKey") val externalKey: kotlin.String? = null,

    @field:JsonProperty("group") val group: kotlin.String? = null,

    @field:JsonProperty("cycleEndTime") val cycleEndTime: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("todayFrequency") val todayFrequency: FrequencyClock? = null,

    @field:Valid
    @field:JsonProperty("subFrequency") val subFrequency: FrequencyClock? = null,

    @field:Valid
    @field:JsonProperty("mainFrequency") val mainFrequency: FrequencyClock? = null
) {

}

