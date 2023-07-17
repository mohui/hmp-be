package com.bjknrt.article.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Size
import javax.validation.Valid

/**
 * CardInfo
 * @param title  标题
 * @param dataSource  数据来源
 * @param content  内容
 * @param status  
 * @param tag  
 * @param id  
 * @param author  作者
 * @param relationArticleId  
 */
data class CardInfo(
    
    @field:JsonProperty("title", required = true) val title: kotlin.String,
    
    @field:JsonProperty("dataSource", required = true) val dataSource: kotlin.String,
    
    @field:JsonProperty("content", required = true) val content: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("status", required = true) val status: PublishStatus,
    
    @field:Valid
    @get:Size(min=1)
    @field:JsonProperty("tag", required = true) val tag: kotlin.collections.List<ArticleTag>,

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null,

    @field:JsonProperty("author") val author: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("relationArticleId") val relationArticleId: java.math.BigInteger? = null
) {

}

