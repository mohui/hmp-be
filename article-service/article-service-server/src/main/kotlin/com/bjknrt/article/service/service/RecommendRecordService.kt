package com.bjknrt.article.service.service

import com.bjknrt.article.service.RecommendDAO
import com.bjknrt.article.service.vo.CardRecommendResponse
import com.bjknrt.framework.api.vo.Id
import me.danwi.sqlex.core.type.PagedResult

interface RecommendRecordService {

    /**
     * 卡片知识延伸
     */
    fun cardRecommend(readerId: Id, cardId: Id, pageNo: Long, pageSize: Long): PagedResult<RecommendDAO.CardRecommendResult>

    /**
     * 根据标签，推荐状态，阅读状态，文章热度，更新时间 推荐文章
     */
    fun articleRecommend(readerId: Id, pageNo: Long, pageSize: Long): PagedResult<RecommendDAO.ArticleRecommendResult>

    /**
     * 根据标签，推荐状态，阅读状态，文章热度，更新时间 推荐卡片
     */
    fun cardRecommendList(readerId: Id, pageNo: Long, pageSize: Long): PagedResult<CardRecommendResponse>

}