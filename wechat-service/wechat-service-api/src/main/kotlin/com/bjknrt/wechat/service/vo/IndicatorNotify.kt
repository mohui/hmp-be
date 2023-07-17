package com.bjknrt.wechat.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.Size

/**
 * 指标异常通知实体
 *
 *
 * @param id 指标id
 * @param patientId 患者id
 * @param name 指标名称. 例如: 血压, 血糖
 * @param date 指标时间
 * @param message 异常说明
 * @param indicators  异常指标数组
 */
data class IndicatorNotify(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,

    @field:Valid
    @field:JsonProperty("patientId", required = true) val patientId: Id,

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("date", required = true) val date: java.time.LocalDateTime,

    @field:JsonProperty("message", required = true) val message: kotlin.String,

    @field:Valid
    @get:Size(min = 1)
    @field:JsonProperty(
        "indicators",
        required = true
    ) val indicators: kotlin.collections.List<IndicatorNotifyIndicatorsInner>
) {

}

