package com.bjknrt.user.permission.centre.vo

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
 * Region
 * @param code  
 * @param name  名称
 * @param levelCode  等级编码
 * @param parentCode  
 */
data class Region(
    
    @field:Valid
    @field:JsonProperty("code", required = true) val code: java.math.BigInteger,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:JsonProperty("levelCode", required = true) val levelCode: kotlin.Int,

    @field:Valid
    @field:JsonProperty("parentCode") val parentCode: java.math.BigInteger? = null
) {

}

