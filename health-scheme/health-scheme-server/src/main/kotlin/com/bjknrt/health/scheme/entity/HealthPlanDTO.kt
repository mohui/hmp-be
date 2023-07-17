package com.bjknrt.health.scheme.entity

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.health.scheme.vo.Frequency
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param name  健康计划名称
 * @param type 类型
 * @param subName  副标题
 * @param desc  计划描述
 * @param externalKey  用于饮食计划
 * @param cycleStartTime 计划开始时间
 * @param cycleEndTime 计划结束时间
 * @param displayTime 展示时间
 * @param group  分组字段
 * @param clockDisplay  打卡后是否显示
 * @param frequencys  频次:几周几次
 */
data class HealthPlanDTO(

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:Valid
    @field:JsonProperty("type", required = true) val type: HealthPlanType,

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