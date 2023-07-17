package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param id 购买id
 * @param patientId 患者id
 * @param name  服务名称. 例如 高血压健康管理服务
 * @param date 购买时间
 */
data class ServiceNotifyInner(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("date", required = true) val date: java.time.LocalDateTime
) {

}

