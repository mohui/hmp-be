package com.bjknrt.article.service.controller

import com.bjknrt.article.service.api.CardApi
import com.bjknrt.article.service.service.CardService
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

@RestController("com.bjknrt.article.service.api.CardController")
class CardController(
    val cardService: CardService,
    val recommendRecordService: RecommendRecordService
) : AppBaseController(), CardApi {
    override fun addOrUpdate(cardInfo: CardInfo) {
        val action = if (cardInfo.status == PublishStatus.PUBLISHED) {
            LogAction.HEALTHY_PUBLISH_CARD
        } else {
            if (cardInfo.id == null) LogAction.HEALTHY_ADD_CARD else LogAction.HEALTHY_UPDATE_CARD
        }
        OperationLogUtil.buzLog(
            module = LogModule.HEALTHY,
            action = action,
            context = titleToString(listOf(cardInfo.title))
        ) {
            cardService.addOrUpdate(cardInfo)
        }
    }

    override fun articleAndCardPage(articleAndCardPageRequest: ArticleAndCardPageRequest): PagedResult<SimpleEntity> {
        return cardService.articleAndCardPage(articleAndCardPageRequest)
    }

    override fun batchOffShelf(id: List<Id>) {
        val titleStr = titleToString(cardService.getTitle(id))
        OperationLogUtil.buzLog(
            module = LogModule.HEALTHY,
            action = LogAction.HEALTHY_OFF_SHELF_CARD,
            context = titleStr
        ) {
            cardService.batchOffShelf(id)
        }
    }

    override fun delete(id: List<Id>) {
        val titleStr = titleToString(cardService.getTitle(id))
        OperationLogUtil.buzLog(
            module = LogModule.HEALTHY,
            action = LogAction.HEALTHY_DELETE_CARD,
            context = titleStr
        ) {
            cardService.delete(id)
        }
    }

    override fun detail(body: BigInteger): CardDetailResponse {
        return cardService.detail(body)
            ?: throw NotFoundDataException(AppSpringUtil.getMessage("card.detail.not-found"))
    }

    override fun page(articlePageParam: ArticlePageParam): PagedResult<CardInfoResponse> {
        return cardService.page(articlePageParam)
    }

    override fun recommend(recommendRequest: RecommendRequest): PagedResult<SimpleEntity> {
        return PagedResult.fromDbPaged(
            recommendRecordService.cardRecommend(
                recommendRequest.readerId,
                recommendRequest.cardId,
                recommendRequest.pageNo,
                recommendRequest.pageSize
            )
        ) {
            SimpleEntity(it.knId, it.knArticleTitle, it.relationId != null)
        }
    }

    override fun recommendList(articleAndCardRecommendParam: ArticleAndCardRecommendParam): PagedResult<CardRecommendResponse> {
        return PagedResult.fromDbPaged(
            recommendRecordService.cardRecommendList(
                articleAndCardRecommendParam.readerId,
                articleAndCardRecommendParam.pageNo,
                articleAndCardRecommendParam.pageSize
            )
        ) {
            CardRecommendResponse(it.id, it.title, true, it.tags)
        }
    }

}