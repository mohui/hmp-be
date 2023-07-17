package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 随访计划通知实体
 *
 *
 * @param id 计划id
 * @param patientId 患者id
 * @param name  计划名称
 * @param start 计划开始
 * @param end 计划结束
 * @param remark  计划说明
 */
data class VisitNotify(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:JsonProperty("name", required = true) var name: kotlin.String,

    @field:JsonProperty("start", required = true) val start: java.time.LocalDateTime,

    @field:JsonProperty("end", required = true) val end: java.time.LocalDateTime,

    @field:JsonProperty("remark", required = true) val remark: kotlin.String
) {

}

/**
 * 根据患者id和时间分组, 聚合成一条通知
 */
fun List<VisitNotify>.zip(): List<VisitNotify> = this.fold(mutableListOf()) { sum, current ->
    sum.find {
        it.patientId == current.patientId && it.start == current.start && it.end == current.end
    }?.let {
        it.name = listOf(it.name, current.name).joinToString("、")
    } ?: run {
        sum.add(current)
    }
    return@fold sum
}
