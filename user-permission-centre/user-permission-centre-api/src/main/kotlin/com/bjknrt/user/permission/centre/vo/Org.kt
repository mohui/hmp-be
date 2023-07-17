package com.bjknrt.user.permission.centre.vo

import java.util.Objects
import com.bjknrt.framework.api.vo.Id
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
 * 
 * @param name  机构名称
 * @param regionCode  
 * @param sort  排序
 * @param id  
 */
data class Org(
    
    @get:Size(max=64)
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("regionCode", required = true) val regionCode: java.math.BigInteger,
    
    @field:JsonProperty("sort", required = true) val sort: kotlin.Int,
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id
) {

}

