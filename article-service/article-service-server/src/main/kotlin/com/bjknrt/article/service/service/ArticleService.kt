package com.bjknrt.article.service.service

import com.bjknrt.article.service.vo.ArticleInfo
import com.bjknrt.article.service.vo.ArticleInfoResponse
import com.bjknrt.article.service.vo.ArticlePageParam
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import java.math.BigInteger

interface ArticleService {
    fun addOrUpdate(articleInfo: ArticleInfo)

    fun page(articlePageParam: ArticlePageParam): PagedResult<ArticleInfoResponse>

    fun delete(articleIds: List<Id>)

    fun offShelf(articleIds: List<Id>)

    fun detail(articleId: BigInteger): ArticleInfoResponse?

    fun getTitle(articleIds: List<Id>): List<String>
}