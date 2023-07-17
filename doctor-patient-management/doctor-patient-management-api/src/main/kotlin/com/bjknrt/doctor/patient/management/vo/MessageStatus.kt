package com.bjknrt.doctor.patient.management.vo

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

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

