package com.bjknrt.article.service.controller

import com.bjknrt.article.service.api.ArticleApi
import com.bjknrt.article.service.service.ArticleService
import com.bjknrt.article.service.service.RecommendRecordService
import com.bjknrt.article.service.utils.titleToString
import com.bjknrt.article.service.vo.*
import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.api.exception.NotFoundDataException
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.operation.logsdk.util.OperationLogUtil
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController("com.bjknrt.article.service.api.ArticleController")
class ArticleController(
    val articleService: ArticleService,
    val recommendRecordService: RecommendRecordService
) : AppBaseController(), ArticleApi {
    override fun addOrUpdate(articleInfo: ArticleInfo) {
        val action = if (articleInfo.status == PublishStatus.PUBLISHED)
            LogAction.HEALTHY_PUBLISH_ARTICLE
        else {
            if (articleInfo.id == null) LogAction.HEALTHY_ADD_ARTICLE else LogAction.HEALTHY_UPDATE_ARTICLE
        }
        OperationLogUtil.buzLog(
            module = LogModule.HEALTHY,
            action = action,
            context =  titleToString(listOf(articleInfo.title))
        ) {
            articleService.addOrUpdate(articleInfo)
        }
    }

    override fun page(articlePageParam: ArticlePageParam): PagedResult<ArticleInfoResponse> {
        return articleService.page(articlePageParam)
    }

    override fun recommend(articleAndCardRecommendParam: ArticleAndCardRecommendParam): PagedResult<ArticleRecommendResponse> {
        return PagedResult.fromDbPaged(
            recommendRecordService.articleRecommend(
                articleAndCardRecommendParam.readerId,
                articleAndCardRecommendParam.pageNo,
                articleAndCardRecommendParam.pageSize
            )
        ) { ArticleRecommendResponse(it.knId, it.knArticleTitle, false, it.knCoverImage, it.knSummary) }
    }

    override fun delete(id: List<Id>) {
        // 获取文章标题
        val titleString = titleToString(articleService.getTitle(id))
        OperationLogUtil.buzLog(
            module = LogModule.HEALTHY,
            action = LogAction.HEALTHY_DELETE_ARTICLE,
            context = titleString
        ) {
            articleService.delete(id)
        }
    }

    override fun detail(body: BigInteger): ArticleInfoResponse {
        return articleService.detail(body)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("article.detail.not-found"))
    }

    override fun batchOffShelf(id: List<Id>) {
        // 获取文章标题
        val titleString = titleToString(articleService.getTitle(id))
        OperationLogUtil.buzLog(
            module = LogModule.HEALTHY,
            action = LogAction.HEALTHY_OFF_SHELF_ARTICLE,
            context = titleString
        ) {
            articleService.offShelf(id)
        }
    }
}