package com.bjknrt.article.service.api

import com.bjknrt.article.service.*
import com.bjknrt.article.service.utils.titleToString
import com.bjknrt.article.service.vo.*
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.doctor.patient.management.vo.PatientTag
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.operation.log.api.LogApi
import com.bjknrt.operation.log.vo.AddOperationLogParam
import com.bjknrt.operation.log.vo.LogAction
import com.bjknrt.operation.log.vo.LogModule
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.api.UserApi
import com.bjknrt.user.permission.centre.vo.ListSimpleInfoByIdsParamInner
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.eq
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CardTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: CardApi

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var mvc: MockMvc

    @Autowired
    lateinit var cardInfoTable: AsCardInfoTable

    @Autowired
    lateinit var asReadRecordInfoTable: AsReadRecordInfoTable

    @Autowired
    lateinit var articleTagInfoTable: AsArticleTagInfoTable

    @Autowired
    lateinit var articleInfoTable: AsArticleInfoTable

    @MockBean
    lateinit var userClient: UserApi

    @MockBean
    lateinit var patientClient: PatientApi

    @MockBean
    lateinit var operationLogClient: LogApi

    val userId: BigInteger = AppIdUtil.nextId()

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

        AppSecurityTestUtil.setCurrentUserInfo(
            userId = userId
        )

        Mockito.doAnswer {
            val mutableListOf = mutableListOf<ListSimpleInfoByIdsParamInner>()
            val bigIntegers = it.arguments[0] as List<Id>
            for (i in 0..bigIntegers.size) {
                mutableListOf.add(
                    ListSimpleInfoByIdsParamInner(
                        bigIntegers[i],
                        "lo",
                        "name$i",
                        com.bjknrt.user.permission.centre.vo.Gender.MAN
                    )
                )
            }
            return@doAnswer mutableListOf
        }.`when`(userClient).listSimpleInfoByIds(any())
    }

    /**
     * To test CardApi.addOrUpdate
     */
    @Test
    fun cardTest() {
        // 1、测试添加卡片
        val cardTitleTemp = AppIdUtil.nextId().toString()
        val relationArticleIdTemp = AppIdUtil.nextId()
        val publishStatusTemp = PublishStatus.UN_PUBLISHED
        val tags = mutableListOf<ArticleTag>()
        tags.add(ArticleTag.HYPERTENSION)

        val cardInfoRequest = CardInfo(
            title = cardTitleTemp,
            author = "作者",
            dataSource = "数据来源",
            content = "内容",
            status = publishStatusTemp,
            relationArticleId = relationArticleIdTemp,
            tag = tags,
            id = null
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/card/addOrUpdate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(cardInfoRequest)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //1.1、添加后的卡片断言验证
        val returnAsCardInfo =
            cardInfoTable.select()
                .where(AsCardInfoTable.KnCardTitle eq cardTitleTemp.arg)
                .where(AsCardInfoTable.IsDel eq false)
                .findOne() ?: fail("卡片添加测试：未查询到数据")

        assertEquals(cardInfoRequest.title, returnAsCardInfo?.knCardTitle)
        assertEquals(cardInfoRequest.author, returnAsCardInfo?.knAuthor)
        assertEquals(cardInfoRequest.dataSource, returnAsCardInfo?.knDataSource)
        assertEquals(cardInfoRequest.content, returnAsCardInfo?.knContent)
        assertEquals(cardInfoRequest.status.value, returnAsCardInfo?.knStatus)

        //1.2、添加后的卡片标签断言验证
        val asArticleTagInfos =
            articleTagInfoTable.select()
                .where(AsArticleTagInfoTable.KnArticleInfoId eq returnAsCardInfo?.knId.arg)
                .where(AsArticleTagInfoTable.IsDel eq false)
                .find()
        assertEquals(cardInfoRequest.tag.size, asArticleTagInfos.size)
        assertEquals(cardInfoRequest.tag[0].name, asArticleTagInfos[0].knTag)

        // 1.3测试添加日志
        val addParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient).saveLog(capture(addParam))

        assertEquals(LogModule.HEALTHY, addParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_ADD_CARD, addParam.value.operatorAction)
        assertEquals(titleToString(listOf(cardInfoRequest.title)), addParam.value.content)

        // 2.1 添加后的分页查询断言验证
        val cardPageParam = ArticlePageParam(
            pageNo = 1,
            pageSize = 10,
            title = cardTitleTemp,
            tag = tags,
            startCreatedAt = LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
            endCreatedAt = LocalDateTime.of(LocalDate.now(), LocalTime.MAX),
            status = publishStatusTemp,
        )
        val cardPagedResult = api.page(cardPageParam)

        assertTrue(cardPagedResult.total >= 1)

        // 2.2 详情
        val detail: CardDetailResponse = api.detail(returnAsCardInfo.knId)
        assertEquals(cardInfoRequest.title, detail.title)
        assertEquals(cardInfoRequest.relationArticleId, detail.relationArticleId)

        //2.3、关联标签断言验证
        assertEquals(cardInfoRequest.tag, detail.tag)

        //2.4、阅读记录断言验证
        val returnRecordInfo = asReadRecordInfoTable.select()
            .where(AsReadRecordInfoTable.KnReaderId eq userId.arg)
            .findOne() ?: fail("查询阅读记录测试：未查询到数据异常")

        assertEquals(detail.id, returnRecordInfo.knArticleId)

        // 2.5、添加后的分页查询断言验证
        val articleAndCardPageRequest = ArticleAndCardPageRequest(
            pageNo = 1,
            pageSize = 10,
            title = cardTitleTemp
        )
        val articleAndCardPageResult = api.articleAndCardPage(articleAndCardPageRequest)

        assertTrue(articleAndCardPageResult.total >= 1)

        // 3、测试修改
        val updateCardTitle = AppIdUtil.nextId().toString()
        val updateTags = mutableListOf<ArticleTag>()
        updateTags.add(ArticleTag.ACUTE_CORONARY)

        val updateCardInfoRequest = CardInfo(
            title = updateCardTitle,
            author = "更新后的作者",
            dataSource = "更新后的数据来源",
            content = "更新后的内容",
            status = publishStatusTemp,
            relationArticleId = AppIdUtil.nextId(),
            tag = updateTags,
            id = returnAsCardInfo?.knId
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/card/addOrUpdate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(updateCardInfoRequest)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // 3.1 测试修改日志
        val updParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient, Mockito.times(2)).saveLog(capture(updParam))

        assertEquals(LogModule.HEALTHY, updParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_UPDATE_CARD, updParam.value.operatorAction)
        assertEquals(titleToString(listOf(updateCardInfoRequest.title)), updParam.value.content)

        // 3.2 发布卡片
        val publishCardInfoRequest = CardInfo(
            title = updateCardInfoRequest.title,
            author = updateCardInfoRequest.author,
            dataSource = updateCardInfoRequest.dataSource,
            content = updateCardInfoRequest.content,
            status = PublishStatus.PUBLISHED,
            relationArticleId = updateCardInfoRequest.relationArticleId,
            tag = updateCardInfoRequest.tag,
            id = updateCardInfoRequest.id
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/card/addOrUpdate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(publishCardInfoRequest)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // 3.3 测试修改日志
        val publishParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient, Mockito.times(3)).saveLog(capture(publishParam))

        assertEquals(LogModule.HEALTHY, publishParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_PUBLISH_CARD, publishParam.value.operatorAction)
        assertEquals(titleToString(listOf(publishCardInfoRequest.title)), publishParam.value.content)

        // 3.4、更新后的卡片断言验证
        val updateAsCardInfo =
            cardInfoTable.select()
                .where(AsCardInfoTable.KnId eq returnAsCardInfo?.knId.arg)
                .where(AsCardInfoTable.IsDel eq false)
                .findOne() ?: fail("修改卡片测试：未查询到文章异常")
        assertEquals(publishCardInfoRequest.title, updateAsCardInfo?.knCardTitle)
        assertEquals(publishCardInfoRequest.author, updateAsCardInfo?.knAuthor)
        assertEquals(publishCardInfoRequest.dataSource, updateAsCardInfo?.knDataSource)
        assertEquals(publishCardInfoRequest.content, updateAsCardInfo?.knContent)
        assertEquals(publishCardInfoRequest.status.name, updateAsCardInfo?.knStatus)
        assertEquals(publishCardInfoRequest.id, updateAsCardInfo?.knId)

        // 3.5、更新后的卡片标签断言验证
        val updateAsCardTagInfos =
            articleTagInfoTable.select()
                .where(AsArticleTagInfoTable.KnArticleInfoId eq returnAsCardInfo?.knId.arg)
                .where(AsArticleTagInfoTable.IsDel eq false)
                .find()
        assertEquals(publishCardInfoRequest.tag.size, updateAsCardTagInfos.size)
        assertEquals(publishCardInfoRequest.tag[0].name, updateAsCardTagInfos[0].knTag)

        //4、下架
        api.batchOffShelf(listOf(updateAsCardInfo.knId))

        // 4.1 下架日志
        val offShelfParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient, Mockito.times(4)).saveLog(capture(offShelfParam))
        assertEquals(LogModule.HEALTHY, offShelfParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_OFF_SHELF_CARD, offShelfParam.value.operatorAction)
        assertEquals(titleToString(listOf(publishCardInfoRequest.title)), offShelfParam.value.content)

        //4.2、验证是否下架
        val ai =
            cardInfoTable.select()
                .where(AsCardInfoTable.KnId eq updateAsCardInfo.knId.arg)
                .where(AsCardInfoTable.IsDel eq false)
                .findOne()

        assertEquals(PublishStatus.OFF_SHELF.value, ai?.knStatus)

        // 5、删除卡片和标签
        api.delete(listOf(updateAsCardInfo.knId))

        // 5.1 删除文章日志
        val deleteParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient, Mockito.times(5)).saveLog(capture(deleteParam))
        assertEquals(LogModule.HEALTHY, deleteParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_DELETE_CARD, deleteParam.value.operatorAction)
        assertEquals(titleToString(listOf(publishCardInfoRequest.title)), deleteParam.value.content)

        //5.2、删除后再查询卡片和标签进行验证
        val deleteCardInfo = cardInfoTable.select()
            .where(AsCardInfoTable.KnId eq updateAsCardInfo.knId)
            .findOne()
        assertEquals(true, deleteCardInfo?.isDel)

        val deleteArticleTagInfo = articleTagInfoTable.select()
            .where(AsArticleTagInfoTable.KnArticleInfoId eq updateAsCardInfo.knId)
            .findOne()
        assertEquals(true, deleteArticleTagInfo?.isDel)
    }

    @Test
    fun recommendTest() {
        // 重置数据
        articleInfoTable.delete().execute()
        cardInfoTable.delete().execute()
        articleTagInfoTable.delete().execute()

        // 准备数据
        // 阅读人
        val personId = AppIdUtil.nextId()
        // 文章
        val articleId = AppIdUtil.nextId()
        articleInfoTable.insertWithoutNull(articleInfo(articleId, "at", "as", "cc"))
        val articleId2 = AppIdUtil.nextId()
        articleInfoTable.insertWithoutNull(articleInfo(articleId2, "at2", "as2", "cc2"))
        val articleId3 = AppIdUtil.nextId()
        articleInfoTable.insertWithoutNull(articleInfo(articleId3, "at3", "as3", "cc3"))
        // 卡片
        val cardId = AppIdUtil.nextId()

        cardInfoTable.insertWithoutNull(
            AsCardInfo.builder()
                .setKnId(cardId)
                .setKnCardTitle("ct")
                .setKnDataSource("cs")
                .setKnContent("cc")
                .setKnStatus(PublishStatus.PUBLISHED.name)
                .setKnRelationArticleId(articleId)
                .build()
        )
        val cardId2 = AppIdUtil.nextId()
        cardInfoTable.insertWithoutNull(
            AsCardInfo.builder()
                .setKnId(cardId2)
                .setKnCardTitle("ct2")
                .setKnDataSource("cs2")
                .setKnContent("cc2")
                .setKnStatus(PublishStatus.PUBLISHED.name)
                .setKnRelationArticleId(articleId2)
                .build()
        )
        // tag

        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.ACUTE_CORONARY,articleId2))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.COPD,articleId2))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.DIABETES,articleId2))

        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.ACUTE_CORONARY,articleId3))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.COPD,articleId3))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.DIABETES,articleId3))

        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.ACUTE_CORONARY,cardId2))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.DIABETES,cardId2))

        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.DIABETES,cardId))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.ACUTE_CORONARY,cardId))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.COPD,cardId))

        // 第一次推荐
        val response = api.recommend(RecommendRequest(1, 2, personId, cardId))
        assertEquals(4, response.total)
        assertEquals(articleId, response._data?.get(0)?.id)
        assertEquals(articleId3, response._data?.get(1)?.id)

        // 第二次推荐
        val response2 = api.recommend(RecommendRequest(1, 2, personId, cardId))
        assertEquals(4, response.total)
        assertEquals(articleId, response2._data?.get(0)?.id)
        assertEquals(articleId2, response2._data?.get(1)?.id)
    }

    @Test
    fun recommendListTest() {
        // 重置数据
        cardInfoTable.delete().execute()
        articleTagInfoTable.delete().execute()
        asReadRecordInfoTable.delete().execute()

        // 准备数据
        // 阅读人
        val personId = AppIdUtil.nextId()
        // 修改当前登录人
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = personId
        )
        // mock远程服务
        Mockito.doReturn(
            PatientInfoResponse(
                id = personId,
                name = "name",
                gender = Gender.MAN,
                phone = "13111256325",
                idCard = "233265956415115465",
                birthday = LocalDateTime.now(),
                age = 18,
                hypertensionDiseaseTag = PatientTag.EXISTS,
                diabetesDiseaseTag = PatientTag.EXISTS,
                acuteCoronaryDiseaseTag = PatientTag.EXISTS,
                cerebralStrokeDiseaseTag = PatientTag.EXISTS,
                copdDiseaseTag = PatientTag.EXISTS
            )
        ).`when`(patientClient).getPatientInfo(personId)
        // 卡片
        val cardId = AppIdUtil.nextId()
        cardInfoTable.insertWithoutNull(cardInfo(cardId,"ct","cds","cc"))
        val cardId2 = AppIdUtil.nextId()
        cardInfoTable.insertWithoutNull(cardInfo(cardId2,"ct2","cds2","cc2"))
        val cardId3 = AppIdUtil.nextId()
        cardInfoTable.insertWithoutNull(cardInfo(cardId3,"ct3","cds3","cc3"))
        // tag
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.ACUTE_CORONARY, cardId))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.COPD, cardId))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.DIABETES, cardId))

        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.ACUTE_CORONARY, cardId2))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.COPD, cardId2))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.DIABETES, cardId2))

        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.ACUTE_CORONARY, cardId3))
        articleTagInfoTable.insertWithoutNull(articleTag(ArticleTag.COPD, cardId3))

        // 第一次推荐 (相同标签数3，更新时间最新的查出来)
        val response = api.recommendList(ArticleAndCardRecommendParam(1, 1, personId))
        assertEquals(3, response.total)
        assertEquals(cardId2, response._data?.get(0)?.id)

        // 阅读
        api.detail(cardId2)

        // 第二次推荐（相同标签数3，未读的查出来）
        val response2 = api.recommendList(ArticleAndCardRecommendParam(1, 1, personId))
        assertEquals(3, response.total)
        assertEquals(cardId, response2._data?.get(0)?.id)
    }

    private fun articleInfo(articleId: BigInteger, title: String, dataSource: String, content: String): AsArticleInfo =
        AsArticleInfo.builder()
            .setKnId(articleId)
            .setKnArticleTitle(title)
            .setKnDataSource(dataSource)
            .setKnContent(content)
            .setKnStatus(PublishStatus.PUBLISHED.name)
            .build()

    private fun articleTag(articleTag: ArticleTag, articleId: BigInteger): AsArticleTagInfo =
        AsArticleTagInfo.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnTag(articleTag.name)
            .setKnArticleInfoId(articleId)
            .build()

    private fun cardInfo(cardId: BigInteger, title: String, dataSource: String, content: String): AsCardInfo =
        AsCardInfo.builder()
            .setKnId(cardId)
            .setKnCardTitle(title)
            .setKnDataSource(dataSource)
            .setKnContent(content)
            .setKnStatus(PublishStatus.PUBLISHED.name)
            .build()

}
