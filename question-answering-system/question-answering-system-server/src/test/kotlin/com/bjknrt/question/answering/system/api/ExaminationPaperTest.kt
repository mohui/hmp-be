package com.bjknrt.question.answering.system.api

import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.doctor.patient.management.vo.PatientTag
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.SelectRecentlyValidPatientIndicatorParam
import com.bjknrt.question.answering.system.*
import com.bjknrt.question.answering.system.expost.question.impl.OptionValuePostProcessorsHandler
import com.bjknrt.question.answering.system.vo.*
import com.bjknrt.security.SUPER_ADMIN_ROLE_CODE
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.security.test.AppSecurityTestUtil
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
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
import java.time.LocalDateTime

@AutoConfigureMockMvc
class ExaminationPaperTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var qasExaminationPaperTable: QasExaminationPaperTable

    @Autowired
    lateinit var qasQuestionsTable: QasQuestionsTable

    @Autowired
    lateinit var qasOptionTable: QasOptionTable

    @Autowired
    lateinit var qasQuestionsImageTable: QasQuestionsImageTable

    @MockBean
    lateinit var patientInfoRpcService: PatientApi

    @MockBean
    lateinit var indicatorRpcService: IndicatorApi

    val userId = 100000.toBigInteger()

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

        AppSecurityTestUtil.setCurrentUserInfo(
            userId = userId,
            roleCodeSet = setOf(SUPER_ADMIN_ROLE_CODE),
            orgIdSet = setOf(BigInteger.ZERO, BigInteger.ONE)
        )
    }

    @Test
    fun pageListTest() {
        qasExaminationPaperTable.save(
            examinationPaper(
                paperTitle = "营养评估",
                paperCode = "A-PAGE" + AppIdUtil.nextId()
            )
        )
        qasExaminationPaperTable.save(
            examinationPaper(
                paperTitle = "健康检测",
                paperCode = "B-PAGE" + AppIdUtil.nextId()
            )
        )

        val request = PageExaminationPaperRequest(1, 10, mutableSetOf("A"))

        mvc.perform(
            MockMvcRequestBuilders.post("/examinationPaper/list")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

    }

    @Test
    fun getQuestionsOption() {
        Mockito.`when`(
            patientInfoRpcService.getPatientInfo(AppSecurityUtil.currentUserIdWithDefault())
        ).thenReturn(
            PatientInfoResponse(
                AppSecurityUtil.currentUserIdWithDefault(),
                "zs",
                Gender.MAN,
                "123",
                "123",
                LocalDateTime.of(1997, 10, 15, 0, 0),
                24,
                hypertensionDiseaseTag = PatientTag.LOW,
                diabetesDiseaseTag = PatientTag.LOW,
                acuteCoronaryDiseaseTag = PatientTag.EXISTS,
                cerebralStrokeDiseaseTag = PatientTag.EXISTS,
                copdDiseaseTag = PatientTag.EXISTS
            )
        )

        val examinationPaperId = AppIdUtil.nextId()
        val code = "QuestionsOption" + AppIdUtil.nextId()
        qasExaminationPaperTable.save(
            examinationPaper(
                paperTitle = "营养评估",
                paperCode = code,
                examinationPaperId = examinationPaperId
            )
        )

        var questionId = AppIdUtil.nextId()
        qasQuestionsTable.save(
            qasQuestions(
                questionId = questionId,
                examinationPaperId = examinationPaperId,
                questionTitle = "请填写个人信息",
                questionType = QuestionsType.FILL_IN_THE_BLANK
            )
        )

        qasOptionTable.save(
            qasOption(
                questionId = questionId,
                optionValue = "",
                optionLabel = "身高",
                optionTip = "请输入身高"
            )
        )
        qasOptionTable.save(
            qasOption(
                questionId = questionId,
                optionValue = "",
                optionLabel = "体重",
                optionTip = "请输入体重"
            )
        )
        val image = QasQuestionsImage.builder()
            .setId(AppIdUtil.nextId())
            .setQuestionsId(questionId)
            .setImageUrl("https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/patient/behavior_visit_2.png")
            .setSort(1)
            .build()
        qasQuestionsImageTable.save(
            image
        )

        questionId = AppIdUtil.nextId()
        qasQuestionsTable.save(
            qasQuestions(
                questionId = questionId,
                examinationPaperId = examinationPaperId,
                questionTitle = "请选择家族史",
                questionType = QuestionsType.RADIO
            )
        )

        qasOptionTable.save(
            qasOption(
                questionId = questionId,
                optionValue = "1",
                optionLabel = "糖尿病",
                optionTip = ""
            )
        )
        qasOptionTable.save(
            qasOption(
                questionId = questionId,
                optionValue = "2",
                optionLabel = "高血压",
                optionTip = ""
            )
        )

        //mock指标查询
        this.mockIndicator()

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/examinationPaper/option")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(code)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(resultString, object : TypeReference<List<Questions>>() {})

        Assertions.assertEquals(2, response.size)
        Assertions.assertEquals("请填写个人信息", response[0].questionsTitle)
        Assertions.assertEquals(image.imageUrl, response[0].images[0].imageUrl)
        Assertions.assertEquals(2, response[0].options.size)
        // 是否可重复答题
        Assertions.assertEquals(false, response[0].isRepeatAnswer)
        Assertions.assertEquals(false, response[1].isRepeatAnswer)
    }

    @Test
    fun getQuestionsImageList() {

        val questionsId = AppIdUtil.nextId()
        val image = QasQuestionsImage.builder()
            .setId(AppIdUtil.nextId())
            .setQuestionsId(questionsId)
            .setImageUrl("https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/patient/behavior_visit_2.png")
            .setSort(1)
            .build()
        qasQuestionsImageTable.save(
            image
        )

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/examinationPaper/questionImageInfo")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(ImageRequest(questionsId))
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(resultString, object : TypeReference<List<QuestionImageInfo>>() {})

        Assertions.assertEquals(1, response.size)
        Assertions.assertEquals(questionsId, response[0].questionsId)
        Assertions.assertEquals(image.imageUrl, response[0].imageUrl)
    }

    @Test
    fun getSportQuestionsOption() {
        Mockito.`when`(
            patientInfoRpcService.getPatientInfo(AppSecurityUtil.currentUserIdWithDefault())
        ).thenReturn(
            PatientInfoResponse(
                AppSecurityUtil.currentUserIdWithDefault(),
                "zs",
                Gender.MAN,
                "123",
                "123",
                LocalDateTime.of(1997, 10, 15, 0, 0),
                24,
                hypertensionDiseaseTag = PatientTag.LOW,
                diabetesDiseaseTag = PatientTag.LOW,
                acuteCoronaryDiseaseTag = PatientTag.LOW,
                cerebralStrokeDiseaseTag = PatientTag.LOW,
                copdDiseaseTag = PatientTag.LOW
            )
        )
        //mock指标查询
        this.mockIndicator()

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/examinationPaper/option")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString("SPORT")
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(resultString, object : TypeReference<List<Questions>>() {})

        Assertions.assertEquals(3, response.size)
    }

    private fun mockIndicator() {
        val patientId = AppSecurityUtil.currentUserIdWithDefault()
        Mockito.`when`(
            indicatorRpcService.selectRecentlyValidIndicatorByType(
                SelectRecentlyValidPatientIndicatorParam(
                    patientId,
                    OptionValuePostProcessorsHandler.OPTION_ID_MAP.values.distinct().toList()
                )
            )
        ).thenReturn(
            listOf()
        )

    }

    private fun examinationPaper(
        paperTitle: String,
        paperCode: String,
        examinationPaperId: BigInteger? = null
    ): QasExaminationPaper = QasExaminationPaper.builder()
        .setId(examinationPaperId ?: AppIdUtil.nextId())
        .setExaminationPaperTitle(paperTitle)
        .setExaminationPaperCode(paperCode)
        .setCreatedBy(AppIdUtil.nextId())
        .setUpdatedBy(AppIdUtil.nextId())
        .build()

    private fun qasQuestions(
        questionId: BigInteger,
        examinationPaperId: BigInteger,
        questionTitle: String,
        questionType: QuestionsType
    ): QasQuestions = QasQuestions.builder()
        .setId(questionId)
        .setExaminationPaperId(examinationPaperId)
        .setQuestionsTitle(questionTitle)
        .setQuestionsType(questionType.name)
        .setCreatedBy(AppIdUtil.nextId())
        .setUpdatedBy(AppIdUtil.nextId())
        .build()

    private fun qasOption(
        questionId: BigInteger,
        optionValue: String,
        optionLabel: String,
        optionTip: String,
    ): QasOption = QasOption.builder()
        .setId(AppIdUtil.nextId())
        .setQuestionsId(questionId)
        .setOptionValue(optionValue)
        .setOptionLabel(optionLabel)
        .setOptionTip(optionTip)
        .setOptionType(OptionType.INPUT.name)
        .setCreatedBy(AppIdUtil.nextId())
        .setUpdatedBy(AppIdUtil.nextId())
        .build()
}
