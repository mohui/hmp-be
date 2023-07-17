package com.bjknrt.article.service.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * ArticlePageParam
 * @param pageNo  页码
 * @param pageSize  每页条数
 * @param title  文章标题
 * @param tag  标签集合
 * @param startCreatedAt  
 * @param endCreatedAt  
 * @param status  
 */
data class ArticlePageParam(
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,

    @field:JsonProperty("title") val title: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("tag") val tag: kotlin.collections.List<ArticleTag>? = null,

    @field:JsonProperty("startCreatedAt") val startCreatedAt: java.time.LocalDateTime? = null,

    @field:JsonProperty("endCreatedAt") val endCreatedAt: java.time.LocalDateTime? = null,

    @field:Valid
    @field:JsonProperty("status") val status: PublishStatus? = null
) {

}

