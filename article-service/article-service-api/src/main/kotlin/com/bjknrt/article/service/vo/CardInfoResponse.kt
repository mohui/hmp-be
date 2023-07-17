package com.bjknrt.article.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * CardInfoResponse
 * @param id  
 * @param title  标题
 * @param dataSource  数据来源
 * @param content  内容
 * @param status  
 * @param tag  
 * @param createdAt  
 * @param author  作者
 * @param publishAt  
 * @param publisher  发布者
 * @param relationArticleId  
 * @param relationArticleTitle  强关联文章或卡片标题
 */
data class CardInfoResponse(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("title", required = true) val title: kotlin.String,
    
    @field:JsonProperty("dataSource", required = true) val dataSource: kotlin.String,
    
    @field:JsonProperty("content", required = true) val content: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("status", required = true) val status: PublishStatus,
    
    @field:Valid
    @field:JsonProperty("tag", required = true) val tag: kotlin.collections.List<ArticleTag>,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,

    @field:JsonProperty("author") val author: kotlin.String? = null,

    @field:JsonProperty("publishAt") val publishAt: java.time.LocalDateTime? = null,

    @field:JsonProperty("publisher") val publisher: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("relationArticleId") val relationArticleId: Id? = null,

    @field:JsonProperty("relationArticleTitle") val relationArticleTitle: kotlin.String? = null
) {

}

