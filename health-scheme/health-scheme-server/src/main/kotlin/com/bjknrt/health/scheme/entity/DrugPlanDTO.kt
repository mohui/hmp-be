package com.bjknrt.health.scheme.entity

import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.health.scheme.vo.Week
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * AddDrugRemindParams
 * @param drugName  药品名称
 * @param isUsed  是否需要我们每天提醒
 * @param time Time 时间
 * @param type 健康计划类型
 * @param frequencys  周一到周日
 * @param cycleStartTime 周期开始时间
 * @param subName  剂量
 * @param cycleEndTime 周期结束时间
 */
data class DrugPlanDTO(
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