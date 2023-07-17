package com.bjknrt.question.answering.system.vo

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
* 类型类型
* Values: SELECT,INPUT,CHECKBOX,TIME
*/
enum class OptionType(val value: kotlin.String) {

    /**
     * 选择/单选
     */
    @JsonProperty("SELECT") SELECT("SELECT"),
    
    /**
     * 输入
     */
    @JsonProperty("INPUT") INPUT("INPUT"),
    
    /**
     * 多选
     */
    @JsonProperty("CHECKBOX") CHECKBOX("CHECKBOX"),
    
    /**
     * 时间
     */
    @JsonProperty("TIME") TIME("TIME")
    
}

