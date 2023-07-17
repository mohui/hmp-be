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
* 题目类型
* Values: RADIO,MULTIPLE_CHOICE,FILL_IN_THE_BLANK,SHORT_ANSWER
*/
enum class QuestionsType(val value: kotlin.String) {

    /**
     * 单选/判断
     */
    @JsonProperty("RADIO") RADIO("RADIO"),
    
    /**
     * 多选
     */
    @JsonProperty("MULTIPLE_CHOICE") MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
    
    /**
     * 填空
     */
    @JsonProperty("FILL_IN_THE_BLANK") FILL_IN_THE_BLANK("FILL_IN_THE_BLANK"),
    
    /**
     * 简答
     */
    @JsonProperty("SHORT_ANSWER") SHORT_ANSWER("SHORT_ANSWER")
    
}

