package com.bjknrt.wechat.service.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @param id  订阅消息id
 * @param tempId  订阅消息模板id
 */
data class NotifyListPostResponseInner(

    @field:JsonProperty("id", required = true) val id: kotlin.String,

    @field:JsonProperty("tempId", required = true) val tempId: kotlin.String
) {

}

