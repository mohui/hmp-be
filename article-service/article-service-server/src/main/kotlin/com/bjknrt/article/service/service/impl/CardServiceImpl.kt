package com.bjknrt.article.service.service.impl

import com.bjknrt.article.service.*
import com.bjknrt.article.service.service.CardService
import com.bjknrt.article.service.service.ReadRecordService
import com.bjknrt.article.service.vo.*
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.api.UserApi
import com.bjknrt.user.permission.centre.vo.ListSimpleInfoByIdsParamInner
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.expression.Expression
import me.danwi.sqlex.core.query.expression.ParameterExpression
import me.danwi.sqlex.core.query.`in`
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class CardServiceImpl(
    val cardInfoTable: AsCardInfoTable,
    val asArticleTagInfoTable: AsArticleTagInfoTable,
    val cardInfoDAO: CardInfoDAO,
    val recordService: ReadRecordService,
    val userClient: UserApi
) : CardService {

    @Transactional
    override fun addOrUpdate(cardInfo: CardInfo) {
        //添加或更新卡片表
        val returnCardInfo = saveOrUpdateCardInfo(cardInfo)

        //添加或更新卡片标签表
        returnCardInfo?.let {
            //先删除此卡片关联的标签
            asArticleTagInfoTable
                .update()
                .setIsDel(true)
                .where(AsArticleTagInfoTable.KnArticleInfoId eq returnCardInfo.knId.arg)
                .execute()

            //再添加卡片标签信息
            cardInfo.tag.forEach { tag ->
                val articleTagInfo = AsArticleTagInfo.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnTag(tag.name)
                    .setKnArticleInfoId(it.knId)
                    .build()
                asArticleTagInfoTable.insertWithoutNull(articleTagInfo)
            }
        }
    }

    override fun page(cardPageParam: ArticlePageParam): PagedResult<CardInfoResponse> {
        //1、主表分页(拿到一页)
        val cardPagedResult = cardInfoDAO.pageCard(
            cardPageParam.title,
            cardPageParam.tag?.map { it.name }?.toList(),
            cardPageParam.startCreatedAt,
            cardPageParam.endCreatedAt,
            cardPageParam.status?.name,
            cardPageParam.pageSize,
            cardPageParam.pageNo,
        )
        if (cardPagedResult.data.size == 0) {
            return PagedResult.fromDbPaged(
                me.danwi.sqlex.core.type.PagedResult(
                    cardPageParam.pageSize,
                    cardPageParam.pageNo,
                    cardPagedResult.total,
                    listOf()
                )
            )
        }

        //2、根据查询出的主表数据，查询子表数据
        val idList: Iterable<ParameterExpression> = cardPagedResult.data.map { Expression.arg(it.knId) }
        val articleTagMap: Map<BigInteger, List<AsArticleTagInfo>> =
            asArticleTagInfoTable.select()
                .where(AsArticleTagInfoTable.IsDel eq false)
                .where(AsArticleTagInfoTable.KnArticleInfoId `in` idList)
                .find()
                .groupBy { it.knArticleInfoId }

        // 强关联信息
        val simpleInfoMap: Map<Id, CardInfoDAO.ListArticleAndCardSimpleInfoResult>? = listSimpleDataMap(cardPagedResult.data.map { it.knRelationArticleId })

        // 补充发布人姓名
        var userMap: Map<Id, ListSimpleInfoByIdsParamInner>? = null
        cardPagedResult.data.mapNotNull { it.knPublisher }.takeIf { it.isNotEmpty() }
            ?.let { ids -> userMap = userClient.listSimpleInfoByIds(ids).associateBy { it.id } }

        //3、组装分页结果
        return PagedResult.fromDbPaged(cardPagedResult) {
            CardInfoResponse(title = it.knCardTitle,
                author = it.knAuthor,
                dataSource = it.knDataSource,
                content = it.knContent,
                status = PublishStatus.valueOf(it.knStatus),
                tag = articleTagMap[it.knId]?.map { tagInfo -> ArticleTag.valueOf(tagInfo.knTag) } ?: listOf(),
                createdAt = it.knCreatedAt,
                id = it.knId,
                publishAt = it.knPublishAt,
                publisher = it.knPublisher?.let { publisher -> userMap?.get(publisher)?.name },
                relationArticleId = it.knRelationArticleId,
                relationArticleTitle = simpleInfoMap?.get(it.knRelationArticleId)?.title
            )
        }
    }

    private fun listSimpleDataMap(data: List<Id>?): Map<Id, CardInfoDAO.ListArticleAndCardSimpleInfoResult>? {
        var simpleInfoMap: Map<Id, CardInfoDAO.ListArticleAndCardSimpleInfoResult>? = null
        data.takeIf { !it.isNullOrEmpty() }?.let { list ->
            list.takeIf { it.isNotEmpty() }?.let { ids ->
                simpleInfoMap = cardInfoDAO.listArticleAndCardSimpleInfo(ids).associateBy { info -> info.knId }
            }
        }
        return simpleInfoMap
    }

    @Transactional
    override fun delete(cardIds: List<Id>) {

        //id集合转换
        val idList = cardIds.map { Expression.arg(it) }

        //逻辑删除标签数据
        asArticleTagInfoTable.update()
            .setIsDel(true)
            .where(AsArticleTagInfoTable.KnArticleInfoId `in` idList)
            .execute()

        //逻辑删除卡片数据
        cardInfoTable.update()
            .setIsDel(true)
            .where(AsCardInfoTable.KnId `in` idList)
            .execute()
    }

    @Transactional
    override fun batchOffShelf(cardIds: List<Id>) {
        cardInfoTable.update()
            .setKnStatus(PublishStatus.OFF_SHELF.value)
            .where(AsCardInfoTable.KnId `in` cardIds.map { Expression.arg(it) })
            .execute()
    }

    override fun articleAndCardPage(articleAndCardPageRequest: ArticleAndCardPageRequest): PagedResult<SimpleEntity> {
        //分页
        val articleAndCardPageResult = cardInfoDAO.pageArticleAndCard(
            articleAndCardPageRequest.title,
            articleAndCardPageRequest.pageSize,
            articleAndCardPageRequest.pageNo
        )
        //分页结果为空，直接返回
        if (articleAndCardPageResult.data.size == 0) {
            return PagedResult.fromDbPaged(
                me.danwi.sqlex.core.type.PagedResult(
                    articleAndCardPageRequest.pageSize,
                    articleAndCardPageRequest.pageNo,
                    articleAndCardPageResult.total,
                    listOf()
                )
            )
        }

        return PagedResult.fromDbPaged(articleAndCardPageResult) {
            SimpleEntity(
                id = it.knId,
                title = it.knCardTitle,
                isCard = it.knRelationArticleId != null
            )
        }
    }

    override fun detail(cardId: BigInteger): CardDetailResponse? {
        //查询卡片详情
        val asCardInfo = detailCard(cardId)

        return asCardInfo?.let {
            //查询卡片关联的标签详情
            val articleTagInfos =
                asArticleTagInfoTable.select()
                    .where(AsArticleTagInfoTable.KnArticleInfoId eq it.knId.arg)
                    .where(AsArticleTagInfoTable.IsDel eq false)
                    .find()

            //保存阅读记录
            val readRecordParam = SaveReadRecordParam(AppSecurityUtil.currentUserIdWithDefault(), cardId)
            recordService.saveReadRecord(readRecordParam)

            //返回结果
            return CardDetailResponse(
                id = it.knId,
                title = it.knCardTitle,
                author = it.knAuthor,
                dataSource = it.knDataSource,
                content = it.knContent,
                status = PublishStatus.valueOf(it.knStatus),
                tag = articleTagInfos.map { tag -> ArticleTag.valueOf(tag.knTag) },
                createdAt = it.knCreatedAt,
                publishAt = it.knPublishAt,
                publisher = it.knPublisher?.let { publisher -> userClient.listSimpleInfoByIds(listOf(publisher)).find { user -> user.id == publisher }?.name },
                relationArticleId = it.knRelationArticleId,
                relationArticleTitle = it.knRelationArticleId?.let { id -> listSimpleDataMap(listOf(id))?.get(id)?.title }
            )
        }
    }

    fun detailCard(cardId: BigInteger): AsCardInfo? {
        return cardInfoTable.findByKnId(cardId)
    }


    private fun saveOrUpdateCardInfo(articleInfo: CardInfo): AsCardInfo? {
        val now = LocalDateTime.now()
        val userId = AppSecurityUtil.currentUserIdWithDefault()
        val asCardInfo = AsCardInfo.builder()
            //id为空是新增操作，id不为空是更新操作
            .setKnId(articleInfo.id.takeIf { it != null } ?: AppIdUtil.nextId())
            .setKnCardTitle(articleInfo.title)
            .setKnDataSource(articleInfo.dataSource)
            .setKnContent(articleInfo.content)
            .setKnStatus(articleInfo.status.name)
            //额外可空字段的处理
            .setKnAuthor(articleInfo.author)
            .setKnCreatedBy(userId)
            .build().apply {
                //确认发布时，才设置发布人和发布时间
                if (articleInfo.status == PublishStatus.PUBLISHED) {
                    this.knPublisher = userId
                    this.knPublishAt = now
                }
                //文章id不为空为更新操作，设置更新时间
                articleInfo.id?.let {
                    this.knUpdatedBy = userId.toLong()
                    this.knUpdatedAt = now
                }
                articleInfo.relationArticleId?.let {
                    this.knRelationArticleId = it
                }
            }
        return cardInfoTable.saveOrUpdate(asCardInfo)
    }

    override fun getTitle(cardIds: List<Id>): List<String> {
        if (cardIds.isEmpty()) return listOf()
        val list = cardInfoTable
            .select()
            .where(AsCardInfoTable.KnId `in` cardIds.map { it.arg })
            .find()
        return list.map { it.knCardTitle }
    }
}