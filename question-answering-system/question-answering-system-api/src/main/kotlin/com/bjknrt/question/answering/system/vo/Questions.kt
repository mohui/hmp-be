package com.bjknrt.question.answering.system.vo

import java.util.Objects
import com.bjknrt.question.answering.system.vo.QuestionImageInfo
import com.bjknrt.question.answering.system.vo.QuestionsOptions
import com.bjknrt.question.answering.system.vo.QuestionsType
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
 * Questions
 * @param id  
 * @param questionsTitle  题目标题
 * @param questionsType  
 * @param questionsSort  排序
 * @param images  
 * @param options  
 * @param isRepeatAnswer  是否重复答题
 * @param questionsGroupTitle  分组标题
 * @param questionsTip  题目提示
 * @param matchRegExp  题目的选择结果符合正则进行下一步操作，不符合提示匹配失败的消息
 * @param matchFailMsg  提示匹配失败的消息
 */
data class Questions(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:JsonProperty("questionsTitle", required = true) val questionsTitle: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("questionsType", required = true) val questionsType: QuestionsType,
    
    @field:JsonProperty("questionsSort", required = true) val questionsSort: kotlin.Int,
    
    @field:Valid
    @field:JsonProperty("images", required = true) val images: kotlin.collections.List<QuestionImageInfo>,
    
    @field:Valid
    @field:JsonProperty("options", required = true) val options: kotlin.collections.List<QuestionsOptions>,
    
    @field:JsonProperty("isRepeatAnswer", required = true) val isRepeatAnswer: kotlin.Boolean,

    @field:JsonProperty("questionsGroupTitle") val questionsGroupTitle: kotlin.String? = null,

    @field:JsonProperty("questionsTip") val questionsTip: kotlin.String? = null,

    @field:JsonProperty("matchRegExp") val matchRegExp: kotlin.String? = null,

    @field:JsonProperty("matchFailMsg") val matchFailMsg: kotlin.String? = null
) {

}

