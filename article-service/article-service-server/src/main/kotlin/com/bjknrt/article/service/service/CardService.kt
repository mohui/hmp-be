package com.bjknrt.article.service.service

import com.bjknrt.article.service.vo.*
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import java.math.BigInteger

interface CardService {
    fun addOrUpdate(cardInfo: CardInfo)
    fun page(cardPageParam: ArticlePageParam): PagedResult<CardInfoResponse>
    fun delete(cardIds: List<Id>)
    fun batchOffShelf(cardIds: List<Id>)
    fun articleAndCardPage(articleAndCardPageRequest: ArticleAndCardPageRequest): PagedResult<SimpleEntity>
    fun detail(cardId: BigInteger): CardDetailResponse?

    fun getTitle(cardIds: List<Id>): List<String>
}