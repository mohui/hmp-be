package com.bjknrt.article.service.vo

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
 * SimpleEntity
 * @param id  
 * @param title  标题
 * @param isCard  是否是卡片（true：是，false：否）
 */
data class SimpleEntity(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("title", required = true) val title: kotlin.String,
    
    @field:JsonProperty("isCard", required = true) val isCard: kotlin.Boolean
) {

}

