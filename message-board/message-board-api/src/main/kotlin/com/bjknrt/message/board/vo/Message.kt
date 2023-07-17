package com.bjknrt.message.board.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * Message
 * @param id
 * @param messageDatetime
 * @param content 留言内容
 * @param fromId
 * @param toId
 */
data class Message(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,

    @field:JsonProperty("messageDatetime", required = true) val messageDatetime: java.time.LocalDateTime,

    @field:JsonProperty("content", required = true) val content: kotlin.String,

    @field:Valid
    @field:JsonProperty("fromId", required = true) val fromId: java.math.BigInteger,

    @field:Valid
    @field:JsonProperty("toId", required = true) val toId: java.math.BigInteger
) {

}

