package com.bjknrt.wechat.service.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param appid  公众号id, patient: 患者端; doctor: 医生端
 * @param from  微信用户UnionID
 * @param eventAt  事件时间
 * @param event  事件类型, subscribe: 订阅; unsubscribe:取消订阅
 */
data class WechatSubscribePostRequest(

    @field:Valid
    @field:JsonProperty("appid", required = true) val appid: AppId,

    @field:JsonProperty("from", required = true) val from: kotlin.String,

    @field:JsonProperty("eventAt", required = true) val eventAt: java.time.LocalDateTime,

    @field:Valid
    @field:JsonProperty("event", required = true) val event: Event
) {

}

