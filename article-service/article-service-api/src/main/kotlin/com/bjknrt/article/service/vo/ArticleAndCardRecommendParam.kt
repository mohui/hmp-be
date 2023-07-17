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
 * ArticleAndCardRecommendParam
 * @param pageNo  页码
 * @param pageSize  每页条数
 * @param readerId  
 */
data class ArticleAndCardRecommendParam(
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,
    
    @field:Valid
    @field:JsonProperty("readerId", required = true) val readerId: Id
) {

}

