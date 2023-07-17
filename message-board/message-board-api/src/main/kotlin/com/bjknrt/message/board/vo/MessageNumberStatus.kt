package com.bjknrt.message.board.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * MessageNumberStatus
 * @param id
 * @param messageNum
 * @param messageStatus
 */
data class MessageNumberStatus(

    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,

    @field:JsonProperty("messageNum", required = true) val messageNum: kotlin.Int,

    @field:Valid
    @field:JsonProperty("messageStatus", required = true) val messageStatus: MessageStatus
) {

}

