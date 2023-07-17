package com.bjknrt.medication.remind.entity

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.medication.remind.vo.HealthPlanType
import com.bjknrt.medication.remind.vo.Week
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param drugName  药品名称
 * @param isUsed  是否需要我们每天提醒
 * @param time Time 时间
 * @param frequencys  周一到周日
 * @param type  药品计划类型
 * @param patientId  患者ID
 * @param subName  剂量
 * @param id  主键, 可空, 如果不为空是修改
 * @param cycleStartTime  计划开始时间
 * @param cycleEndTime  计划结束时间
 */
data class DrugDTO (

    @field:JsonProperty("drugName", required = true) val drugName: kotlin.String,

    @field:JsonProperty("isUsed", required = true) val isUsed: kotlin.Boolean,

    @field:Valid
    @field:JsonProperty("time", required = true) val time: java.time.LocalTime,

    @field:Valid
    @field:JsonProperty("frequencys", required = true) val frequencys: kotlin.collections.List<Week>,

    @field:Valid
    @field:JsonProperty("type", required = true) val type: HealthPlanType,

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:JsonProperty("subName") val subName: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null,

    @field:JsonProperty("cycleStartTime") val cycleStartTime: java.time.LocalDateTime? = null,

    @field:JsonProperty("cycleEndTime") val cycleEndTime: java.time.LocalDateTime? = null
) {

}