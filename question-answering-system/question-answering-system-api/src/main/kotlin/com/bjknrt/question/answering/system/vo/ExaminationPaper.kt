package com.bjknrt.question.answering.system.vo

import java.util.Objects
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
 * ExaminationPaper
 * @param id  
 * @param examinationPaperTitle  问卷题名称
 * @param examinationPaperDesc  问卷题描述
 * @param reference  参考文献
 * @param evaluationTime  预计评测时常
 * @param examinationPaperCode  问卷题编码
 * @param examinationPaperTip  问卷题提示
 */
data class ExaminationPaper(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: java.math.BigInteger,
    
    @field:JsonProperty("examinationPaperTitle", required = true) val examinationPaperTitle: kotlin.String,
    
    @field:JsonProperty("examinationPaperDesc", required = true) val examinationPaperDesc: kotlin.String,
    
    @field:JsonProperty("reference", required = true) val reference: kotlin.String,
    
    @field:JsonProperty("evaluationTime", required = true) val evaluationTime: kotlin.String,
    
    @field:JsonProperty("examinationPaperCode", required = true) val examinationPaperCode: kotlin.String,

    @field:JsonProperty("examinationPaperTip") val examinationPaperTip: kotlin.String? = null
) {

}

