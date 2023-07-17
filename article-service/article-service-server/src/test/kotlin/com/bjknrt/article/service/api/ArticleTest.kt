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
import com.bjknrt.health.scheme.api.ClockInApi
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


class ArticleTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var mvc: MockMvc

    @Autowired
    lateinit var api: ArticleApi

    @Autowired
    lateinit var articleInfoTable: AsArticleInfoTable

    @Autowired
    lateinit var articleTagInfoTable: AsArticleTagInfoTable

    @Autowired
    lateinit var asReadRecordInfoTable: AsReadRecordInfoTable

    @MockBean
    lateinit var patientClient: PatientApi

    @MockBean
    lateinit var healthSchemeClockInClient: ClockInApi

    @MockBean
    lateinit var userClient: UserApi

    @MockBean
    lateinit var operationLogClient: LogApi

    val userId: BigInteger = AppIdUtil.nextId()

    @BeforeEach
    fun before() {
        // 获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

        AppSecurityTestUtil.setCurrentUserInfo(
            userId = userId
        )

        Mockito.doNothing().`when`(healthSchemeClockInClient).saveClockIn(any())
        Mockito.doAnswer {
            val mutableListOf = mutableListOf<ListSimpleInfoByIdsParamInner>()
            val bigIntegers = it.arguments[0] as List<Id>
            for (i in 0..bigIntegers.size) {
                mutableListOf.add(
                    ListSimpleInfoByIdsParamInner(
                        bigIntegers[i],
                        "lo",
                        "name$i",
                        com.bjknrt.user.permission.centre.vo.Gender.WOMAN
                    )
                )
            }
            return@doAnswer mutableListOf
        }.`when`(userClient).listSimpleInfoByIds(any())

    }

    /**
     * To test ArticleApi.addOrUpdateTest
     */
    @Test
    fun articleTest() {
        // 1、测试add方法
        val articleTitle = AppIdUtil.nextId().toString()
        val tags = mutableListOf<ArticleTag>()
        val publishStatusTemp = PublishStatus.UN_PUBLISHED
        tags.add(ArticleTag.HYPERTENSION)

        val articleInfoRequest = ArticleInfo(
            title = articleTitle,
            author = "作者",
            dataSource = "数据来源",
            content = "内容",
            status = publishStatusTemp,
            tag = tags,
            id = null,
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/article/addOrUpdate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(articleInfoRequest)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // 测试添加日志
        val addParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient).saveLog(capture(addParam))

        assertEquals(LogModule.HEALTHY, addParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_ADD_ARTICLE, addParam.value.operatorAction)
        assertEquals(titleToString(listOf(articleInfoRequest.title)), addParam.value.content)

        //1.1、添加后的文章断言验证
        val asArticleInfo =
            articleInfoTable.select()
                .where(AsArticleInfoTable.KnArticleTitle eq articleTitle.arg)
                .where(AsArticleInfoTable.IsDel eq false)
                .findOne() ?: fail("添加文章测试：未查询到文章异常")

        assertEquals(articleInfoRequest.title, asArticleInfo?.knArticleTitle)
        assertEquals(articleInfoRequest.author, asArticleInfo?.knAuthor)
        assertEquals(articleInfoRequest.dataSource, asArticleInfo?.knDataSource)
        assertEquals(articleInfoRequest.content, asArticleInfo?.knContent)
        assertEquals(articleInfoRequest.status.value, asArticleInfo?.knStatus)

        //1.2、添加后的文章标签断言验证
        val asArticleTagInfos =
            articleTagInfoTable.select()
                .where(AsArticleTagInfoTable.KnArticleInfoId eq asArticleInfo?.knId.arg)
                .where(AsArticleTagInfoTable.IsDel eq false)
                .find()
        assertEquals(articleInfoRequest.tag.size, asArticleTagInfos.size)
        assertEquals(articleInfoRequest.tag[0].name, asArticleTagInfos[0].knTag)

        // 2.1、添加后的分页查询断言验证
        val articlePageParam = ArticlePageParam(
            pageNo = 1,
            pageSize = 10,
            title = articleTitle,
            tag = tags,
            startCreatedAt = LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
            endCreatedAt = LocalDateTime.of(LocalDate.now(), LocalTime.MAX),
            status = publishStatusTemp,
        )
        val pagedResult = api.page(articlePageParam)

        assertTrue(pagedResult.total >= 1)

        //2.2、测试文章详情
        val detail = api.detail(asArticleInfo.knId)

        //2.3、验证文章详情接口数据
        assertEquals(articleInfoRequest.title, detail.title)

        //2.4、验证标签接口数据
        assertEquals(articleInfoRequest.tag, detail.tag)

        //2.5、验证保存阅读记录接口数据
        val recordInfo = asReadRecordInfoTable.select()
            .where(AsReadRecordInfoTable.KnArticleId eq asArticleInfo.knId.arg)
            .findOne()

        assertEquals(userId, recordInfo?.knReaderId)

        // 3、测试修改
        val updateArticleTitle = AppIdUtil.nextId().toString()
        val updateTags = mutableListOf<ArticleTag>()
        updateTags.add(ArticleTag.ACUTE_CORONARY)

        // 修改
        val updateArticleInfoRequest = ArticleInfo(
            title = updateArticleTitle,
            author = "更新后的作者",
            dataSource = "更新后的数据来源",
            content = "更新后的内容",
            status = publishStatusTemp,
            tag = updateTags,
            id = asArticleInfo?.knId,
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/article/addOrUpdate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(updateArticleInfoRequest)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // 3.1 测试修改日志
        val updParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient, Mockito.times(2)).saveLog(capture(updParam))

        assertEquals(LogModule.HEALTHY, updParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_UPDATE_ARTICLE, updParam.value.operatorAction)
        assertEquals(titleToString(listOf(updateArticleInfoRequest.title)), updParam.value.content)

        // 3.2 发布
        val publishArticleInfoRequest = ArticleInfo(
            title = updateArticleInfoRequest.title,
            author = updateArticleInfoRequest.author,
            dataSource = updateArticleInfoRequest.dataSource,
            content = updateArticleInfoRequest.content,
            status = PublishStatus.PUBLISHED,
            tag = updateArticleInfoRequest.tag,
            id = updateArticleInfoRequest.id,
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/article/addOrUpdate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(publishArticleInfoRequest)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // 3.3 测试发布日志
        val publishParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient, Mockito.times(3)).saveLog(capture(publishParam))

        assertEquals(LogModule.HEALTHY, publishParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_PUBLISH_ARTICLE, publishParam.value.operatorAction)
        assertEquals(titleToString(listOf(publishArticleInfoRequest.title)), publishParam.value.content)

        //3.4、更新后的文章断言验证
        val updateAsArticleInfo =
            articleInfoTable.select()
                .where(AsArticleInfoTable.KnId eq asArticleInfo?.knId.arg)
                .where(AsArticleInfoTable.IsDel eq false)
                .findOne() ?: fail("修改文章测试：未查询到文章异常")
        assertEquals(publishArticleInfoRequest.title, updateAsArticleInfo?.knArticleTitle)
        assertEquals(publishArticleInfoRequest.author, updateAsArticleInfo?.knAuthor)
        assertEquals(publishArticleInfoRequest.dataSource, updateAsArticleInfo?.knDataSource)
        assertEquals(publishArticleInfoRequest.content, updateAsArticleInfo?.knContent)
        assertEquals(publishArticleInfoRequest.status.name, updateAsArticleInfo?.knStatus)
        assertEquals(publishArticleInfoRequest.id, updateAsArticleInfo?.knId)

        //3.5、更新后的文章标签断言验证
        val updateAsArticleTagInfos =
            articleTagInfoTable.select()
                .where(AsArticleTagInfoTable.KnArticleInfoId eq asArticleInfo?.knId.arg)
                .where(AsArticleTagInfoTable.IsDel eq false)
                .find()
        assertEquals(publishArticleInfoRequest.tag.size, updateAsArticleTagInfos.size)
        assertEquals(publishArticleInfoRequest.tag[0].name, updateAsArticleTagInfos[0].knTag)

        //4、下架
        api.batchOffShelf(listOf(updateAsArticleInfo.knId))

        // 4.1 下架日志
        val offShelfParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient, Mockito.times(4)).saveLog(capture(offShelfParam))
        assertEquals(LogModule.HEALTHY, offShelfParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_OFF_SHELF_ARTICLE, offShelfParam.value.operatorAction)
        assertEquals(titleToString(listOf(publishArticleInfoRequest.title)), offShelfParam.value.content)

        //4.2、验证是否下架
        val offShelfFind = articleInfoTable.select()
            .where(AsArticleInfoTable.KnId eq updateAsArticleInfo.knId.arg)
            .where(AsArticleInfoTable.IsDel eq false)
            .findOne()

        assertEquals(PublishStatus.OFF_SHELF.value, offShelfFind?.knStatus)

        //5、删除文章和标签
        api.delete(listOf(updateAsArticleInfo.knId))

        // 5.1 删除文章日志
        val deleteParam: ArgumentCaptor<AddOperationLogParam> = ArgumentCaptor.forClass(AddOperationLogParam::class.java)
        Mockito.verify(operationLogClient, Mockito.times(5)).saveLog(capture(deleteParam))
        assertEquals(LogModule.HEALTHY, deleteParam.value.operatorModel)
        assertEquals(LogAction.HEALTHY_DELETE_ARTICLE, deleteParam.value.operatorAction)
        assertEquals(titleToString(listOf(publishArticleInfoRequest.title)), deleteParam.value.content)

        //5.2、删除后再查询文章和标签进行验证
        val deleteArticleInfo = articleInfoTable.select()
            .where(AsArticleInfoTable.KnId eq updateAsArticleInfo.knId)
            .findOne()
        assertEquals(true, deleteArticleInfo?.isDel)

        val deleteArticleTagInfo = articleTagInfoTable.select()
            .where(AsArticleTagInfoTable.KnArticleInfoId eq updateAsArticleInfo.knId)
            .findOne()
        assertEquals(true, deleteArticleTagInfo?.isDel)
    }

    @Test
    fun recommendTest() {
        // 重置数据
        articleInfoTable.delete().execute()
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
        // 文章
        val articleId = AppIdUtil.nextId()
        articleInfoTable.insertWithoutNull(
            articleInfo(articleId, "at1", "as1", "cc1")
        )
        val articleId2 = AppIdUtil.nextId()
        articleInfoTable.insertWithoutNull(
            articleInfo(articleId2, "at2", "as2", "cc2")
        )
        val articleId3 = AppIdUtil.nextId()
        articleInfoTable.insertWithoutNull(
            articleInfo(articleId3, "at3", "as3", "cc3")
        )
        // tag
        articleTagInfoTable.insertWithoutNull(articleTagInfo(ArticleTag.ACUTE_CORONARY, articleId))
        articleTagInfoTable.insertWithoutNull(articleTagInfo(ArticleTag.COPD, articleId))
        articleTagInfoTable.insertWithoutNull(articleTagInfo(ArticleTag.DIABETES, articleId))

        articleTagInfoTable.insertWithoutNull(articleTagInfo(ArticleTag.ACUTE_CORONARY, articleId2))
        articleTagInfoTable.insertWithoutNull(articleTagInfo(ArticleTag.COPD, articleId2))
        articleTagInfoTable.insertWithoutNull(articleTagInfo(ArticleTag.DIABETES, articleId2))

        articleTagInfoTable.insertWithoutNull(articleTagInfo(ArticleTag.ACUTE_CORONARY, articleId3))
        articleTagInfoTable.insertWithoutNull(articleTagInfo(ArticleTag.COPD, articleId3))

        // 第一次推荐 (相同标签数3，更新时间最新的查出来)
        val response = api.recommend(ArticleAndCardRecommendParam(1, 1, personId))
        assertEquals(3, response.total)
        assertEquals(articleId2, response._data?.get(0)?.id)

        // 阅读
        api.detail(articleId2)

        // 第二次推荐（相同标签数3，未读的查出来）
        val response2 = api.recommend(ArticleAndCardRecommendParam(1, 1, personId))
        assertEquals(3, response.total)
        assertEquals(articleId, response2._data?.get(0)?.id)
    }

    private fun articleInfo(
        articleId: BigInteger,
        title: String,
        dataSource: String,
        content: String
    ): AsArticleInfo =
        AsArticleInfo.builder()
            .setKnId(articleId)
            .setKnArticleTitle(title)
            .setKnDataSource(dataSource)
            .setKnContent(content)
            .setKnStatus(PublishStatus.PUBLISHED.name)
            .build()

    private fun articleTagInfo(articleTag: ArticleTag, articleId: BigInteger): AsArticleTagInfo =
        AsArticleTagInfo.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnTag(articleTag.name)
            .setKnArticleInfoId(articleId)
            .build()
}
