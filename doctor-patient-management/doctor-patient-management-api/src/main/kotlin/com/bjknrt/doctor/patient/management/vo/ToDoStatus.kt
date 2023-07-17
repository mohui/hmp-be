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
* 代办事项
* Values: START_ASSESS,SUCCESS,NONE,SEVEN_DAYS_NOT_LOGIN
*/
enum class ToDoStatus(val value: kotlin.String) {

    /**
     * 开始评估
     */
    @JsonProperty("START_ASSESS") START_ASSESS("START_ASSESS"),
    
    /**
     * 完成
     */
    @JsonProperty("SUCCESS") SUCCESS("SUCCESS"),
    
    /**
     * 无
     */
    @JsonProperty("NONE") NONE("NONE"),
    
    /**
     * 7天未登录
     */
    @JsonProperty("SEVEN_DAYS_NOT_LOGIN") SEVEN_DAYS_NOT_LOGIN("SEVEN_DAYS_NOT_LOGIN")
    
}

