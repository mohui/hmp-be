package com.bjknrt.message.board.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * MessageNumberStatusRequest
 * @param fromId  发送者
 * @param toId
 */
data class MessageNumberStatusRequest(

    @field:Valid
    @field:JsonProperty("fromId", required = true) val fromId: kotlin.collections.List<java.math.BigInteger>,

    @field:Valid
    @field:JsonProperty("toId", required = true) val toId: java.math.BigInteger
) {

}

