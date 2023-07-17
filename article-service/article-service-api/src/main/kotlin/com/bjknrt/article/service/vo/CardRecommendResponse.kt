package com.bjknrt.article.service.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * CardRecommendResponse
 * @param id  
 * @param title  标题
 * @param isCard  是否是卡片（true：是，false：否）
 * @param tags  标签集合
 */
data class CardRecommendResponse(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("title", required = true) val title: kotlin.String,
    
    @field:JsonProperty("isCard", required = true) val isCard: kotlin.Boolean,
    
    @field:Valid
    @field:JsonProperty("tags", required = true) val tags: kotlin.collections.List<ArticleTag>
) {

}

