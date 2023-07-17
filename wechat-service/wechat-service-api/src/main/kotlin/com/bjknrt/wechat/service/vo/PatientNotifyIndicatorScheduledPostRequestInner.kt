package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 指标监测计划实体
 *
 *
 * @param id 计划id
 * @param patientId  患者id
 * @param name  指标
 * @param desc  描述
 */
data class PatientNotifyIndicatorScheduledPostRequestInner(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("desc", required = true) val desc: kotlin.String
) {

}

