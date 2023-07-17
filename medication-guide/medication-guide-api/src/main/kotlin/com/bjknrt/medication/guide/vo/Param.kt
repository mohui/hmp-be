package com.bjknrt.medication.guide.vo

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
 * param
 * @param keywords  以空格区分的检索词组
 * @param pageNo  页数
 * @param pageSize  分页大小
 */
data class Param(
    
    @field:JsonProperty("keywords", required = true) val keywords: kotlin.String,
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Int,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Int
) {

}

