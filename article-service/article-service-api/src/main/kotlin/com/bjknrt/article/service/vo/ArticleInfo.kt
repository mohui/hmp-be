package com.bjknrt.article.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * ArticleInfo
 * @param title  标题
 * @param dataSource  数据来源
 * @param content  内容
 * @param status  
 * @param tag  
 * @param id  
 * @param author  作者
 * @param coverImage  封面图
 * @param summary  摘要
 */
data class ArticleInfo(
    
    @field:JsonProperty("title", required = true) val title: kotlin.String,
    
    @field:JsonProperty("dataSource", required = true) val dataSource: kotlin.String,
    
    @field:JsonProperty("content", required = true) val content: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("status", required = true) val status: PublishStatus,
    
    @field:Valid
    @field:JsonProperty("tag", required = true) val tag: kotlin.collections.List<ArticleTag>,

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null,

    @field:JsonProperty("author") val author: kotlin.String? = null,

    @field:JsonProperty("coverImage") val coverImage: kotlin.String? = null,

    @field:JsonProperty("summary") val summary: kotlin.String? = null
) {

}

