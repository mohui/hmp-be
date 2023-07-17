package com.bjknrt.message.board.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * MessageStatusRequest
 * @param fromId
 * @param toId
 */
data class MessageStatusRequest(

    @field:Valid
    @field:JsonProperty("fromId", required = true) val fromId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("toId", required = true) val toId: java.math.BigInteger
) {

}

