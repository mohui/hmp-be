package com.bjknrt.question.answering.system.vo

import java.util.Objects
import com.bjknrt.question.answering.system.vo.OptionType
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
 * QuestionsOptions
 * @param questionsId  
 * @param optionId  
 * @param optionValue  选项结果
 * @param optionLabel  选项名称
 * @param optionTip  选项提示
 * @param optionSort  排序
 * @param optionType  
 * @param isDynamic  是否动态选项
 * @param isAutoCommit  是否自动提交答题
 * @param optionCode  选项编码
 * @param optionScore  
 * @param optionRule  选项规则（JSON格式）
 * @param forwardTo  
 */
data class QuestionsOptions(
    
    @field:Valid
    @field:JsonProperty("questionsId", required = true) val questionsId: java.math.BigInteger,
    
    @field:Valid
    @field:JsonProperty("optionId", required = true) val optionId: java.math.BigInteger,
    
    @field:JsonProperty("optionValue", required = true) val optionValue: kotlin.String,
    
    @field:JsonProperty("optionLabel", required = true) val optionLabel: kotlin.String,
    
    @field:JsonProperty("optionTip", required = true) val optionTip: kotlin.String,
    
    @field:JsonProperty("optionSort", required = true) val optionSort: kotlin.Int,
    
    @field:Valid
    @field:JsonProperty("optionType", required = true) val optionType: OptionType,
    
    @field:JsonProperty("isDynamic", required = true) val isDynamic: kotlin.Boolean,
    
    @field:JsonProperty("isAutoCommit", required = true) val isAutoCommit: kotlin.Boolean,

    @field:JsonProperty("optionCode") val optionCode: kotlin.String? = null,

    @field:JsonProperty("optionScore") val optionScore: java.math.BigDecimal? = null,

    @field:JsonProperty("optionRule") val optionRule: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("forwardTo") val forwardTo: java.math.BigInteger? = null
) {

}

