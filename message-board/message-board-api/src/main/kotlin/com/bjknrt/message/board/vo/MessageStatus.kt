package com.bjknrt.message.board.vo

import com.fasterxml.jackson.annotation.JsonProperty

/**
* 留言状态
* Values: READ,UNREAD,NONE
*/
enum class MessageStatus(val value: kotlin.String) {

    /**
     * 有留言未回复
     */
    @JsonProperty("READ") READ("READ"),

    /**
     * 有新留言
     */
    @JsonProperty("UNREAD") UNREAD("UNREAD"),

    /**
     * 无
     */
    @JsonProperty("NONE") NONE("NONE")

}

