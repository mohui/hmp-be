package com.bjknrt.wechat.service.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 异常指标实体
 *
 *
 * @param name  指标名称. 例如: 舒张压, 空腹血糖
 * @param value  指标值
 * @param unit  指标单位
 */
data class IndicatorNotifyIndicatorsInner(

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("value", required = true) val value: java.math.BigDecimal,

    @field:JsonProperty("unit", required = true) val unit: kotlin.String
) {

}

