package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 健康计划通知实体
 *
 *
 * @param id 计划id
 * @param patientId 患者id
 * @param name  计划名称
 * @param date  计划时间
 * @param remark  计划说明
 */
data class HealthTodoNotify(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("date", required = true) val date: kotlin.String,

    @field:JsonProperty("remark", required = true) val remark: kotlin.String
) {

}

