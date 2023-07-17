package com.bjknrt.article.service.service.impl

import com.bjknrt.article.service.*
import com.bjknrt.article.service.event.ReadRecordEvent
import com.bjknrt.article.service.service.ArticleService
import com.bjknrt.article.service.service.ReadRecordService
import com.bjknrt.article.service.vo.*
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.user.permission.centre.api.UserApi
import com.bjknrt.user.permission.centre.vo.ListSimpleInfoByIdsParamInner
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import me.danwi.sqlex.core.query.expression.Expression
import me.danwi.sqlex.core.query.`in`
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class ArticleServiceImpl(
    val asArticleInfoTable: AsArticleInfoTable,
    val asArticleTagInfoTable: AsArticleTagInfoTable,
    val articleInfoDAO: ArticleInfoDAO,
    val recordService: ReadRecordService,
    val eventPublisher: ApplicationEventPublisher,
    val userClient: UserApi
) : ArticleService {

    @Transactional
    override fun addOrUpdate(articleInfo: ArticleInfo) {

        //添加或更新文章表
        val returnArticleInfo = saveOrUpdateArticleInfo(articleInfo)

        //添加或更新文章标签表
        returnArticleInfo?.let {

            //先删除此文章关联的标签
            asArticleTagInfoTable
                .update()
                .setIsDel(true)
                .where(AsArticleTagInfoTable.KnArticleInfoId eq returnArticleInfo.knId.arg)
                .execute()

            //再添加文章标签信息
            articleInfo.tag.forEach { tag ->
                val articleTagInfo = AsArticleTagInfo.builder()
                    .setKnId(AppIdUtil.nextId())
                    .setKnTag(tag.name)
                    .setKnArticleInfoId(it.knId)
                    .build()
                asArticleTagInfoTable.insertWithoutNull(articleTagInfo)
            }
        }
    }

    private fun saveOrUpdateArticleInfo(articleInfo: ArticleInfo): AsArticleInfo? {
        val now = LocalDateTime.now()
        val userId = AppSecurityUtil.currentUserIdWithDefault()
        val asArticleInfo = AsArticleInfo.builder()
            //id为空是新增操作，id不为空是更新操作
            .setKnId(articleInfo.id.takeIf { it != null } ?: AppIdUtil.nextId())
            .setKnArticleTitle(articleInfo.title)
            .setKnDataSource(articleInfo.dataSource)
            .setKnContent(articleInfo.content)
            .setKnStatus(articleInfo.status.name)
            //额外可空字段的处理
            .setKnAuthor(articleInfo.author)
            .setKnCoverImage(articleInfo.coverImage)
            .setKnSummary(articleInfo.summary)
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
            }
        return asArticleInfoTable.saveOrUpdate(asArticleInfo)
    }

    override fun page(articlePageParam: ArticlePageParam): PagedResult<ArticleInfoResponse> {

        //1、主表分页(拿到一页)
        val articlePagedResult = articleInfoDAO.page(
            articlePageParam.title,
            articlePageParam.tag?.map { it.name }?.toList(),
            articlePageParam.startCreatedAt,
            articlePageParam.endCreatedAt,
            articlePageParam.status?.name,
            articlePageParam.pageSize,
            articlePageParam.pageNo,
        )

        if (articlePagedResult.data.size == 0) {
            return PagedResult.fromDbPaged(
                me.danwi.sqlex.core.type.PagedResult(
                    articlePageParam.pageSize,
                    articlePageParam.pageNo,
                    articlePagedResult.total,
                    listOf()
                )
            )
        }

        //2、根据查询出的主表数据，查询子表数据
        val idList = articlePagedResult.data.map { Expression.arg(it.knId) }
        val articleTagMap: Map<BigInteger, List<AsArticleTagInfo>> =
            asArticleTagInfoTable.select()
                .where(AsArticleTagInfoTable.IsDel eq false)
                .where(AsArticleTagInfoTable.KnArticleInfoId `in` idList)
                .find()
                .groupBy { it.knArticleInfoId }

        // 补充发布人姓名
        var userMap: Map<Id, ListSimpleInfoByIdsParamInner>? = null
        articlePagedResult.data.mapNotNull { it.knPublisher }.takeIf { it.isNotEmpty() }
            ?.let { ids -> userMap = userClient.listSimpleInfoByIds(ids).associateBy { it.id } }

        //3、组装分页结果
        return PagedResult.fromDbPaged(articlePagedResult) {
            ArticleInfoResponse(
                title = it.knArticleTitle,
                author = it.knAuthor,
                dataSource = it.knDataSource,
                content = it.knContent,
                coverImage = it.knCoverImage,
                summary = it.knSummary,
                status = PublishStatus.valueOf(it.knStatus),
                tag = articleTagMap[it.knId]?.map { tagInfo -> ArticleTag.valueOf(tagInfo.knTag) } ?: listOf(),
                createdAt = it.knCreatedAt,
                id = it.knId,
                publishAt = it.knPublishAt,
                publisher = it.knPublisher?.let { publisher -> userMap?.get(publisher)?.name }
            )
        }
    }

    @Transactional
    override fun delete(articleIds: List<Id>) {
        //id集合转换
        val idList = articleIds.map { Expression.arg(it) }

        //逻辑删除标签数据
        asArticleTagInfoTable.update()
            .setIsDel(true)
            .where(AsArticleTagInfoTable.KnArticleInfoId `in` idList)
            .execute()

        //逻辑删除文章数据
        asArticleInfoTable.update()
            .setIsDel(true)
            .where(AsArticleInfoTable.KnId `in` idList)
            .execute()
    }

    @Transactional
    override fun offShelf(articleIds: List<Id>) {
        asArticleInfoTable.update()
            .setKnStatus(PublishStatus.OFF_SHELF.value)
            .where(AsArticleInfoTable.KnId `in` articleIds.map { Expression.arg(it) })
            .execute()
    }

    override fun detail(articleId: BigInteger): ArticleInfoResponse? {
        //查询文章详情
        val asArticleInfo = detailArticle(articleId)

        return asArticleInfo?.let {
            val readerId = AppSecurityUtil.currentUserIdWithDefault()

            //查询文章关联的标签详情
            val articleTagInfos =
                asArticleTagInfoTable.select()
                    .where(AsArticleTagInfoTable.KnArticleInfoId eq it.knId.arg)
                    .where(AsArticleTagInfoTable.IsDel eq false)
                    .find()

            //发布事件
            eventPublisher.publishEvent(
                ReadRecordEvent(
                    this,
                    readerId,
                    asArticleInfo.knId,
                    HealthPlanType.SCIENCE_POPULARIZATION_PLAN,
                    LocalDateTime.now()
                )
            )

            //保存阅读记录
            val readRecordParam = SaveReadRecordParam(readerId, articleId)
            recordService.saveReadRecord(readRecordParam)

            //返回结果
            return ArticleInfoResponse(
                id = it.knId,
                title = it.knArticleTitle,
                author = it.knAuthor,
                dataSource = it.knDataSource,
                content = it.knContent,
                coverImage = it.knCoverImage,
                summary = it.knSummary,
                status = PublishStatus.valueOf(it.knStatus),
                tag = articleTagInfos.map { tag -> ArticleTag.valueOf(tag.knTag) },
                createdAt = it.knCreatedAt,
                publishAt = it.knPublishAt,
                publisher = it.knPublisher?.let { publisher ->
                    userClient.listSimpleInfoByIds(listOf(publisher)).find { user -> user.id == publisher }?.name
                }
            )
        }
    }

    fun detailArticle(articleId: BigInteger): AsArticleInfo? {
        return asArticleInfoTable.findByKnId(articleId)
    }

    override fun getTitle(articleIds: List<Id>): List<String> {
        if (articleIds.isEmpty()) return listOf()
        val list = asArticleInfoTable
            .select()
            .where(AsArticleInfoTable.KnId `in` articleIds.map { it.arg })
            .find()
        return list.map { it.knArticleTitle }
    }
}