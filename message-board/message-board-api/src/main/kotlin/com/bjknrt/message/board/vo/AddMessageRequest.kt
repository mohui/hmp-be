package com.bjknrt.message.board.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 *
 * @param fromId
 * @param toId
 * @param content 留言内容
 */
data class AddMessageRequest(

    @field:Valid
    @field:JsonProperty("fromId", required = true) val fromId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("toId", required = true) val toId: java.math.BigInteger,

    @field:JsonProperty("content", required = true) val content: kotlin.String
) {

}

