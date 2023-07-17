package com.bjknrt.question.answering.system.api

import cn.hutool.core.date.DateUtil
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.doctor.patient.management.vo.PatientTag
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.api.ExaminationApi
import com.bjknrt.health.scheme.api.ManageApi
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.question.answering.system.*
import com.bjknrt.question.answering.system.expost.evaluate.impl.DrugProgramEvaluatePostProcessorsHandler.Companion.DIABETES_QUESTIONS_1_OPTION_ID_1
import com.bjknrt.question.answering.system.expost.evaluate.impl.DrugProgramEvaluatePostProcessorsHandler.Companion.DIABETES_QUESTIONS_ID_1
import com.bjknrt.question.answering.system.expost.evaluate.impl.SportEvaluatePostProcessorsHandler
import com.bjknrt.question.answering.system.expost.evaluate.impl.SportEvaluatePostProcessorsHandler.Companion.OPTION_CODE_SPORT
import com.bjknrt.question.answering.system.interpret.impl.DrugProgramInterpretStrategy
import com.bjknrt.question.answering.system.vo.*
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.sqlex.core.query.eq
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
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
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime

@AutoConfigureMockMvc
class AnswerTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var questionsAnswerRecordTable: QasQuestionsAnswerRecordTable

    @Autowired
    lateinit var qasExaminationPaperTable: QasExaminationPaperTable

    @Autowired
    lateinit var interpretationOfResults: QasInterpretationOfResultsTable

    @Autowired
    lateinit var qasQuestionsAnswerResultTable: QasQuestionsAnswerResultTable

    @MockBean
    lateinit var healthPlanManageRpcService: ManageApi

    @MockBean
    lateinit var clockInRpcService: ClockInApi

    @MockBean
    lateinit var patientInfoRpcService: PatientApi

    @MockBean
    lateinit var examinationRpcService: ExaminationApi

    @Autowired
    lateinit var optionTable: QasOptionTable

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build();

    }

    @Test
    fun evaluateResult() {
        val answerId = AppIdUtil.nextId()
        val examinationPaperId = AppIdUtil.nextId()

        createInterpret(examinationPaperId)

        qasExaminationPaperTable.save(
            QasExaminationPaper.builder()
                .setId(examinationPaperId)
                .setExaminationPaperTitle("测试评估")
                .setExaminationPaperCode("TEST" + AppIdUtil.nextId())
                .setCreatedBy(AppIdUtil.nextId())
                .setUpdatedBy(AppIdUtil.nextId())
                .setCreatedAt(LocalDateTime.now())
                .setEvaluationTime("")
                .setIsDel(false)
                .setStrategyCode("DEFAULT")
                .setUpdatedAt(LocalDateTime.now())
                .setExaminationPaperDesc("")
                .setExaminationPaperReference("")
                .setExaminationPaperTip("")
                .build()
        )
        // 选项ID, 为了测试同一选项提交两次
        val optionId1 = AppIdUtil.nextId()
        val optionId2 = AppIdUtil.nextId()
        val request = EvaluateResultRequest(
            answerId,
            examinationPaperId,
            mutableListOf(
                EvaluateResultQuestionsOption(
                    examinationPaperId,
                    optionId1,
                    "",
                    null,
                    BigDecimal.valueOf(5),
                ),
                EvaluateResultQuestionsOption(
                    examinationPaperId,
                    optionId2,
                    "",
                    null,
                    BigDecimal.valueOf(5),
                ),
                EvaluateResultQuestionsOption(
                    examinationPaperId,
                    optionId2,
                    "",
                    null,
                    BigDecimal.valueOf(5),
                )
            )
        )

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/answer/evaluateResult")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(resultString, EvaluateResultResponse::class.java)

        Assertions.assertEquals(BigDecimal.valueOf(15), response.totalScore)
        Assertions.assertEquals("危险", response.resultsTag)
        Assertions.assertEquals(
            "注意！您的营养不良状况已经非常明显了，请立即安排进一步评估及请求专业人士线下干预！",
            response.resultsMsg
        )

        // 查询最近一次评估结果
        val lastParams = LastEvaluateResultRequest(answerId, examinationPaperId)
        val lastResult = mvc.perform(
            MockMvcRequestBuilders.post("/answer/last")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(lastParams)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val lastResponse = objectMapper.readValue(lastResult, EvaluateResultResponse::class.java)
        // 断言最后一次做题的ID不为null
        Assertions.assertNotNull(lastResponse.id)

        // 根据最后一次做题的ID查询详情
        val resultString1 = mvc.perform(
            MockMvcRequestBuilders.post("/answer/detail")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(lastResponse.id)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val detailResponse = objectMapper.readValue(resultString1, AnswerDetailResponse::class.java)
        // 断言
        Assertions.assertEquals(3, detailResponse.options.size)
        Assertions.assertEquals(1, detailResponse.options[0].questionsAnsweredCount)
        Assertions.assertEquals(1, detailResponse.options[1].questionsAnsweredCount)
        Assertions.assertEquals(2, detailResponse.options[2].questionsAnsweredCount)

    }

    @Test
    fun lastEvaluateResult() {
        val patientId = AppIdUtil.nextId()
        val examinationPaperId = AppIdUtil.nextId()
        val interpretId = AppIdUtil.nextId()
        val createId = AppIdUtil.nextId()
        interpretationOfResults.save(
            qasInterpretResult(
                interpretId = interpretId,
                examinationPaperId = examinationPaperId,
                createId = createId,
                resultTag = "危险",
                resultMsg = "注意！您的营养不良状况已经非常明显了，请立即安排进一步评估及请求专业人士线下干预！",
                minValue = BigDecimal.valueOf(0),
                maxValue = BigDecimal.valueOf(100)
            )
        )

        val record = questionAnswerRecord(
            patientId = patientId,
            examinationPaperId = examinationPaperId,
            resultTag = "危险",
            resultMsg = "注意！您的营养不良状况已经非常明显了，请立即安排进一步评估及请求专业人士线下干预！"
        )

        questionsAnswerRecordTable.save(record)

        val request = LastEvaluateResultRequest(patientId, examinationPaperId)

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/answer/last")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(resultString, EvaluateResultResponse::class.java)

        response.totalScore?.let { Assertions.assertEquals(10, it.intValueExact()) }
        Assertions.assertEquals("危险", response.resultsTag)
        Assertions.assertEquals(
            "注意！您的营养不良状况已经非常明显了，请立即安排进一步评估及请求专业人士线下干预！",
            response.resultsMsg
        )


    }

    @Test
    fun pageEvaluateResultTest() {
        val patientId = AppIdUtil.nextId()
        val examinationPaperId = AppIdUtil.nextId()
        var record = answerRecord(
            patientId = patientId,
            examinationPaperId = examinationPaperId,
            resultTag = "健康",
            resultMsg = "您的营养状况很好，建议六个月后重新检测一次，观察是否有变化。",
            totalScore = BigDecimal.valueOf(2)
        )
        questionsAnswerRecordTable.save(record)

        record = answerRecord(
            patientId = patientId,
            examinationPaperId = examinationPaperId,
            resultTag = "中等",
            resultMsg = "注意，您可能有营养不良的倾向，建议您接受饮食习惯干预或者生活方式干预，三个月后重新测试。",
            totalScore = BigDecimal.valueOf(4)
        )
        questionsAnswerRecordTable.save(record)


        record = answerRecord(
            patientId = patientId,
            examinationPaperId = examinationPaperId,
            resultTag = "危险",
            resultMsg = "注意！您的营养不良状况已经非常明显了，请立即安排进一步评估及请求专业人士线下干预！",
            totalScore = BigDecimal.valueOf(10)
        )
        questionsAnswerRecordTable.save(record)


        val request = PageEvaluateResultRequest(patientId, examinationPaperId, 1, 10)

        mvc.perform(
            MockMvcRequestBuilders.post("/answer/page")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun answerDetailTest() {
        val patientId = AppIdUtil.nextId()
        val examinationPaperId = AppIdUtil.nextId()
        val recordId = AppIdUtil.nextId()

        qasExaminationPaperTable.save(
            QasExaminationPaper.builder()
                .setId(examinationPaperId)
                .setExaminationPaperTitle("营养评估")
                .setExaminationPaperCode("B-ANSWER" + AppIdUtil.nextId())
                .setCreatedBy(AppIdUtil.nextId())
                .setUpdatedBy(AppIdUtil.nextId())
                .build()
        )

        val resultId = AppIdUtil.nextId()
        interpretationOfResults.save(
            qasInterpretResult(
                interpretId = resultId,
                examinationPaperId = examinationPaperId,
                createId = AppIdUtil.nextId(),
                resultMsg = "注意！您的营养不良状况已经非常明显了，请立即安排进一步评估及请求专业人士线下干预！",
                resultTag = "危险",
                minValue = BigDecimal.valueOf(6),
                maxValue = BigDecimal.valueOf(1000)
            )
        )

        qasQuestionsAnswerResultTable.save(
            QasQuestionsAnswerResult.builder()
                .setId(AppIdUtil.nextId())
                .setQuestionsAnswerRecordId(recordId)
                .setQuestionsId(AppIdUtil.nextId())
                .setOptionId(AppIdUtil.nextId())
                .setOptionValue("")
                .setOptionCode("")
                .setOptionScore(BigDecimal(50))
                .setQuestionsAnsweredCount(1)
                .build()
        )

        val record = answerRecord(
            patientId = patientId,
            examinationPaperId = examinationPaperId,
            resultTag = "危险",
            resultMsg = "注意！您的营养不良状况已经非常明显了，请立即安排进一步评估及请求专业人士线下干预！",
            totalScore = BigDecimal.valueOf(50),
            recordId = recordId
        )
        questionsAnswerRecordTable.save(record)


        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/answer/detail")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(recordId)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val response = objectMapper.readValue(resultString, AnswerDetailResponse::class.java)

        response.evaluateResult.totalScore?.let { Assertions.assertEquals(50, it.intValueExact()) }
        Assertions.assertEquals("危险", response.evaluateResult.resultsTag)
        Assertions.assertEquals(1, response.options.size)
        Assertions.assertEquals("营养评估", response.examinationPaper.examinationPaperTitle)


    }

    private fun createInterpret(examinationPaperId: BigInteger): List<BigInteger> {
        val createId = AppIdUtil.nextId()
        val resultIdList = listOf(AppIdUtil.nextId(), AppIdUtil.nextId(), AppIdUtil.nextId())
        interpretationOfResults.save(
            qasInterpretResult(
                interpretId = resultIdList[0],
                examinationPaperId = examinationPaperId,
                createId = createId,
                resultTag = "健康",
                resultMsg = "您的营养状况很好，建议六个月后重新检测一次，观察是否有变化。",
                minValue = BigDecimal.valueOf(0),
                maxValue = BigDecimal.valueOf(2)
            )
        )
        interpretationOfResults.save(
            qasInterpretResult(
                interpretId = resultIdList[1],
                examinationPaperId = examinationPaperId,
                createId = createId,
                resultTag = "中等",
                resultMsg = "注意，您可能有营养不良的倾向，建议您接受饮食习惯干预或者生活方式干预，三个月后重新测试。",
                minValue = BigDecimal.valueOf(3),
                maxValue = BigDecimal.valueOf(5)
            )
        )
        interpretationOfResults.save(
            qasInterpretResult(
                interpretId = resultIdList[2],
                examinationPaperId = examinationPaperId,
                createId = createId,
                resultTag = "危险",
                resultMsg = "注意！您的营养不良状况已经非常明显了，请立即安排进一步评估及请求专业人士线下干预！",
                minValue = BigDecimal.valueOf(6),
                maxValue = BigDecimal.valueOf(1000)
            )
        )
        return resultIdList
    }

    @Test
    fun evaluateSportResult() {

        val request = EvaluateResultRequest(
            BigInteger.ONE,
            BigInteger.valueOf(1500),
            mutableListOf(
                EvaluateResultQuestionsOption(
                    BigInteger.valueOf(150001),
                    BigInteger.valueOf(15000107),
                    "7",
                    "SPORT_150001_07",
                ),
                EvaluateResultQuestionsOption(
                    BigInteger.valueOf(150002),
                    BigInteger.valueOf(15000201),
                    "1",
                    "SPORT_150002_01",
                ),
                EvaluateResultQuestionsOption(
                    BigInteger.valueOf(150003),
                    BigInteger.valueOf(15000301),
                    "1",
                    "SPORT_150003_01",
                ),
                EvaluateResultQuestionsOption(
                    BigInteger.valueOf(150003),
                    BigInteger.valueOf(15000302),
                    "1",
                    "SPORT_150003_02",
                )
            )
        )
        val patientId = BigInteger.ONE
        val startDate = LocalDate.now().atStartOfDay()

        Mockito.`when`(
            patientInfoRpcService.getPatientInfo(patientId)
        ).thenReturn(
            PatientInfoResponse(
                patientId,
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

        Mockito.doNothing().`when`(healthPlanManageRpcService).addHealthPlan(
            AddHealthPlanParam(
                healthPlans = request.questionsOption
                    .filter {
                        (it.questionsId == SportEvaluatePostProcessorsHandler.AEROBIC_EXERCISE_QUESTION_ID && it.optionId != SportEvaluatePostProcessorsHandler.AEROBIC_EXERCISE_QUESTION_OPTION_NULL_ID)
                                || (it.questionsId == SportEvaluatePostProcessorsHandler.RESISTANCE_EXERCISE_QUESTION_ID && it.optionId != SportEvaluatePostProcessorsHandler.RESISTANCE_EXERCISE_QUESTION_OPTION_NULL_ID)
                    }
                    .map {
                        //运动名称
                        val name = SportEvaluatePostProcessorsHandler.OPTION_CODE_SPORT.getOrDefault(it.optionCode, "")
                        //运动分组及频次
                        val group: String?
                        if (it.questionsId == SportEvaluatePostProcessorsHandler.AEROBIC_EXERCISE_QUESTION_ID) {
                            group = SportEvaluatePostProcessorsHandler.AEROBIC_EXERCISE
                        } else {
                            group = SportEvaluatePostProcessorsHandler.RESISTANCE_EXERCISE
                        }
                        return@map FrequencyHealthParams(
                            name = name,
                            type = HealthPlanType.EXERCISE_PROGRAM,
                            patientId = patientId,
                            subName = "",
                            desc = "每周5次，每次30分钟",
                            cycleStartTime = startDate,
                            cycleEndTime = null,
                            group = group,
                            frequencys = SportEvaluatePostProcessorsHandler.ONE_WEEKS_FIVE_DAYS_FREQUENCY
                        )
                    },
                drugPlans = listOf()
            )
        )
        // 模拟完成运动反馈
        val adjustmentRemindParam = AddHealthPlanParam(
            healthPlans = listOf(
                FrequencyHealthParams(
                    name = "完成运动情况反馈",
                    type = HealthPlanType.EXERCISE_PROGRAM_ADJUSTMENT_REMIND,
                    patientId = patientId,
                    subName = "",
                    desc = "",
                    cycleStartTime = LocalDate.now().atStartOfDay(),
                    cycleEndTime = null,
                    clockDisplay = false,
                    frequencys = SportEvaluatePostProcessorsHandler.EXERCISE_PROGRAM_FOUR_WEEKS_ONE_SEQUENCE
                )
            ),
            drugPlans = listOf()
        )
        Mockito.doNothing().`when`(healthPlanManageRpcService).addHealthPlan(adjustmentRemindParam)

        Mockito.doNothing().`when`(healthPlanManageRpcService).delHealthPlanByPatientIdAndTypes(
            DelHealthPlanByPatientIdAndTypes(
                patientId = patientId,
                types = listOf(
                    HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                    HealthPlanType.EXERCISE_PROGRAM_ADJUSTMENT_REMIND
                ),
            )
        )

        // 加打卡mock
        Mockito.doNothing().`when`(clockInRpcService).saveClockIn(
            ClockInRequest(
                patientId = patientId,
                healthPlanType = HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                currentDateTime = LocalDateTime.now()
            )
        )

        //加同步运动禁忌mock
        Mockito.doNothing().`when`(examinationRpcService).syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = patientId,
                knExaminationPaperCode = SportEvaluatePostProcessorsHandler.SPORT_CODE,
                knExaminationPaperOptionList = listOf()
            )
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/answer/evaluateResult")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        // 拦截 完成运动情况反馈, 行为习惯随访参数，血压测量的mock参数 mock
        val addCaptor: ArgumentCaptor<AddHealthPlanParam> =
            ArgumentCaptor.forClass(AddHealthPlanParam::class.java)
        Mockito.verify(healthPlanManageRpcService).addHealthPlan(capture(addCaptor))
        // 断言,前三个是运动计划,第四个是完成运动情况反馈
        Assertions.assertEquals(4, addCaptor.value.healthPlans.size)
        Assertions.assertEquals(OPTION_CODE_SPORT[request.questionsOption[1].optionCode], addCaptor.value.healthPlans[0].name)
        Assertions.assertEquals(OPTION_CODE_SPORT[request.questionsOption[2].optionCode], addCaptor.value.healthPlans[1].name)
        Assertions.assertEquals(OPTION_CODE_SPORT[request.questionsOption[3].optionCode], addCaptor.value.healthPlans[2].name)

        Assertions.assertEquals(adjustmentRemindParam.healthPlans[0].type, addCaptor.value.healthPlans[3].type)
        Assertions.assertEquals(adjustmentRemindParam.healthPlans[0].name, addCaptor.value.healthPlans[3].name)
        Assertions.assertEquals(adjustmentRemindParam.healthPlans[0].patientId, addCaptor.value.healthPlans[3].patientId)
        Assertions.assertEquals(adjustmentRemindParam.drugPlans.size, addCaptor.value.drugPlans.size)

        // 拦截删除参数
        val argumentCaptorDelete: ArgumentCaptor<DelHealthPlanByPatientIdAndTypes> =
            ArgumentCaptor.forClass(DelHealthPlanByPatientIdAndTypes::class.java)
        Mockito.verify(healthPlanManageRpcService).delHealthPlanByPatientIdAndTypes(capture(argumentCaptorDelete))
        // 断言删除未进行用药评估远程调用参数
        Assertions.assertEquals(patientId, argumentCaptorDelete.value.patientId)
        Assertions.assertEquals(
            listOf(
                HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                HealthPlanType.EXERCISE_PROGRAM_ADJUSTMENT_REMIND,
                HealthPlanType.EXERCISE_PROGRAM
            ),
            argumentCaptorDelete.value.types
        )
    }

    /**
     * 运动评估-选择第1题运动禁忌选项的情况
     */
    @Test
    fun evaluateSportResultWithSportTaboo() {
        val patientId = AppIdUtil.nextId()

        val request = EvaluateResultRequest(
            answerBy = patientId,
            examinationPaperId = BigInteger.valueOf(1500),
            questionsOption = mutableListOf(
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(150001),
                    optionId = BigInteger.valueOf(15000101),
                    optionValue = "1",
                    optionCode = "SPORT_150001_01",
                )
            )
        )

        //打卡mock
        Mockito.doNothing().`when`(clockInRpcService).saveClockIn(any())

        //删除运动调整计划mock, 未进行运动评估计划mock
        Mockito.doNothing().`when`(healthPlanManageRpcService).delHealthPlanByPatientIdAndTypes(any())

        //添加运动调整计划mock
        Mockito.doNothing().`when`(healthPlanManageRpcService).addHealthPlan(any())

        //调用评估接口
        mvc.perform(
            MockMvcRequestBuilders.post("/answer/evaluateResult")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        //拦截AddCurrentSchemeSportTabooParam参数类型的方法
        val sportTabooParamCaptor: ArgumentCaptor<AddCurrentSchemeExaminationAdapterParam> =
            ArgumentCaptor.forClass(AddCurrentSchemeExaminationAdapterParam::class.java)
        Mockito.verify(examinationRpcService).syncCurrentSchemeExaminationAdapter(capture(sportTabooParamCaptor))

        //验证：患者ID、选项个数、选项Id
        Assertions.assertEquals(request.answerBy, sportTabooParamCaptor.value.knPatientId)
        Assertions.assertEquals(request.questionsOption.size, sportTabooParamCaptor.allValues.size)
        Assertions.assertEquals(
            request.questionsOption[0].optionId,
            sportTabooParamCaptor.value.knExaminationPaperOptionList[0].knOptionId
        )

        //查询选项信息
        val option = optionTable.select()
            .where(QasOptionTable.Id eq request.questionsOption[0].optionId)
            .where(QasOptionTable.QuestionsId eq request.questionsOption[0].questionsId)
            .where(QasOptionTable.IsDel eq false)
            .findOne()

        //验证：选项标签
        Assertions.assertEquals(
            option?.optionLabel,
            sportTabooParamCaptor.value.knExaminationPaperOptionList[0].knOptionLabel
        )

        //查询问卷记录
        val answerRecord =
            questionsAnswerRecordTable.select()
                .where(QasQuestionsAnswerRecordTable.AnswerBy eq request.answerBy)
                .where(QasQuestionsAnswerRecordTable.ExaminationPaperId eq request.examinationPaperId)
                .findOne() ?: Assertions.fail(AppSpringUtil.getMessage("answer.record.not-found"))

        //查询问卷结果
        val answerResult =
            qasQuestionsAnswerResultTable.select()
                .where(QasQuestionsAnswerResultTable.QuestionsAnswerRecordId eq answerRecord.id)
                .where(QasQuestionsAnswerResultTable.OptionId eq request.questionsOption[0].optionId)
                .findOne()

        //验证：记录Id
        Assertions.assertEquals(
            sportTabooParamCaptor.value.knExaminationPaperOptionList[0].knAnswerRecordId,
            answerRecord.id
        )

        //验证：结果Id
        Assertions.assertEquals(
            sportTabooParamCaptor.value.knExaminationPaperOptionList[0].knAnswerResultId,
            answerResult?.id
        )
    }

    @Test
    fun drugEvaluateResult() {
        val answerId = AppIdUtil.nextId()
        val examinationPaperId = BigInteger.valueOf(3400)

        // 选项ID, 为了测试同一选项提交两次
        val request = EvaluateResultRequest(
            answerBy = answerId,
            examinationPaperId = examinationPaperId,
            questionsOption = mutableListOf(
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340001),
                    optionId = BigInteger.valueOf(34000101),
                    optionValue = "A",
                    optionCode = "DIABETES_DRUG_PROGRAM_340001_1",
                    optionScore = null,
                ),
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340002),
                    optionId = BigInteger.valueOf(34000201),
                    optionValue = "糖尿病A药",
                    optionCode = "DIABETES_DRUG_PROGRAM_340002_1",
                    optionScore = null,
                ),
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340002),
                    optionId = BigInteger.valueOf(34000202),
                    optionValue = "糖尿病A药规格",
                    optionCode = "DIABETES_DRUG_PROGRAM_340002_2",
                    optionScore = null,
                ),
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340002),
                    optionId = BigInteger.valueOf(34000203),
                    optionValue = "[\"MONDAY\", \"SUNDAY\"]",
                    optionCode = "DIABETES_DRUG_PROGRAM_340002_3",
                    optionScore = null,
                ),
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340002),
                    optionId = BigInteger.valueOf(34000204),
                    optionValue = "08:30:00",
                    optionCode = "DIABETES_DRUG_PROGRAM_340002_4",
                    optionScore = null,
                ),
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340002),
                    optionId = BigInteger.valueOf(34000201),
                    optionValue = "糖尿病B药",
                    optionCode = "DIABETES_DRUG_PROGRAM_340002_1",
                    optionScore = null,
                ),
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340002),
                    optionId = BigInteger.valueOf(34000202),
                    optionValue = "糖尿病B药规格",
                    optionCode = "DIABETES_DRUG_PROGRAM_340002_2",
                    optionScore = null,
                ),
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340002),
                    optionId = BigInteger.valueOf(34000203),
                    optionValue = "[\"MONDAY\", \"SUNDAY\"]",
                    optionCode = "DIABETES_DRUG_PROGRAM_340002_3",
                    optionScore = null,
                ),
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340002),
                    optionId = BigInteger.valueOf(34000204),
                    optionValue = "08:35:00",
                    optionCode = "DIABETES_DRUG_PROGRAM_340002_4",
                    optionScore = null,
                ),
                EvaluateResultQuestionsOption(
                    questionsId = BigInteger.valueOf(340003),
                    optionId = BigInteger.valueOf(34000301),
                    optionValue = "A",
                    optionCode = "DIABETES_DRUG_PROGRAM_340003_1",
                    optionScore = null,
                )
            )
        )

        val resultString = mvc.perform(
            MockMvcRequestBuilders.post("/answer/evaluateResult")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // 拦截校验打卡参数
        val argumentCaptor: ArgumentCaptor<ClockInRequest> = ArgumentCaptor.forClass(ClockInRequest::class.java)
        Mockito.verify(clockInRpcService).saveClockIn(capture(argumentCaptor))
        // 断言打卡参数
        Assertions.assertEquals(request.answerBy, argumentCaptor.value.patientId)
        Assertions.assertEquals(HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED, argumentCaptor.value.healthPlanType)

        // 拦截删除参数
        val argumentCaptorDelete: ArgumentCaptor<DelHealthPlanByPatientIdAndTypes> =
            ArgumentCaptor.forClass(DelHealthPlanByPatientIdAndTypes::class.java)
        Mockito.verify(healthPlanManageRpcService).delHealthPlanByPatientIdAndTypes(capture(argumentCaptorDelete))
        // 断言删除未进行用药评估远程调用参数
        Assertions.assertEquals(request.answerBy, argumentCaptorDelete.value.patientId)
        Assertions.assertEquals(
            listOf(
                HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED,
                HealthPlanType.DIABETES_DRUG_PROGRAM
            ),
            argumentCaptorDelete.value.types
        )

        // 拦截
        val argumentCaptorUpsert: ArgumentCaptor<AddHealthPlanParam> =
            ArgumentCaptor.forClass(AddHealthPlanParam::class.java)
        Mockito.verify(healthPlanManageRpcService, Mockito.times(1))
            .addHealthPlan(capture(argumentCaptorUpsert))

        // 拦截到的药品1
        val drugPlanParam1 = argumentCaptorUpsert.allValues[0]?.drugPlans?.get(0);
        // 测试用药提醒远程调用
        Assertions.assertEquals(request.answerBy, drugPlanParam1?.patientId)
        Assertions.assertEquals(HealthPlanType.DIABETES_DRUG_PROGRAM, drugPlanParam1?.type)

        Assertions.assertEquals(request.questionsOption[1].optionValue, drugPlanParam1?.drugName)
        Assertions.assertEquals(request.questionsOption[2].optionValue, drugPlanParam1?.subName)
        // 把药品1频率转为数组类型
        val drug1Frequency: Array<String> =
            objectMapper.readValue(request.questionsOption[3].optionValue, arrayOf<String>()::class.java)
        // 断言频率
        Assertions.assertArrayEquals(drug1Frequency, drugPlanParam1?.frequencys?.map { it.name }?.toTypedArray())
        // 断言时间
        Assertions.assertEquals(
            DateUtil.parseTime(request.questionsOption[4].optionValue).toLocalDateTime().toLocalTime(),
            drugPlanParam1?.time
        )

        // 拦截到的药品2
        val drugPlanParam2 = argumentCaptorUpsert.allValues[0]?.drugPlans?.get(1)
        Assertions.assertEquals(request.questionsOption[5].optionValue, drugPlanParam2?.drugName)
        Assertions.assertEquals(request.questionsOption[6].optionValue, drugPlanParam2?.subName)
        // 把药品2频率转为数组类型
        val drug2Frequency: Array<String> =
            objectMapper.readValue(request.questionsOption[7].optionValue, arrayOf<String>()::class.java)
        // 断言频率
        Assertions.assertArrayEquals(drug2Frequency, drugPlanParam2?.frequencys?.map { it.name }?.toTypedArray())
        // 断言时间
        Assertions.assertEquals(
            DateUtil.parseTime(request.questionsOption[8].optionValue).toLocalDateTime().toLocalTime(),
            drugPlanParam2?.time
        )
        // 拦截到的问卷选项
        val drugProgramAdapterParam: ArgumentCaptor<AddCurrentSchemeExaminationAdapterParam> =
            ArgumentCaptor.forClass(AddCurrentSchemeExaminationAdapterParam::class.java)
        Mockito.verify(examinationRpcService).syncCurrentSchemeExaminationAdapter(capture(drugProgramAdapterParam))

        Assertions.assertEquals(request.answerBy, drugProgramAdapterParam.value.knPatientId)
        Assertions.assertEquals("DIABETES_DRUG_PROGRAM", drugProgramAdapterParam.value.knExaminationPaperCode)
        Assertions.assertEquals(1, drugProgramAdapterParam.value.knExaminationPaperOptionList.size)
        Assertions.assertEquals(
            DIABETES_QUESTIONS_ID_1,
            drugProgramAdapterParam.value.knExaminationPaperOptionList[0].knQuestionsId
        )
        Assertions.assertEquals(
            DIABETES_QUESTIONS_1_OPTION_ID_1,
            drugProgramAdapterParam.value.knExaminationPaperOptionList[0].knOptionId
        )


        val evaluateResultResponse = objectMapper.readValue(resultString, EvaluateResultResponse::class.java)
        Assertions.assertEquals(
            evaluateResultResponse.resultsMsg,
            DrugProgramInterpretStrategy.optionCodeMap[request.questionsOption[0].optionCode]
        )

        // 查询最近一次评估结果
        val lastParams = LastEvaluateResultRequest(answerId, examinationPaperId)
        val lastResult = mvc.perform(
            MockMvcRequestBuilders.post("/answer/last")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(lastParams)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val lastResponse = objectMapper.readValue(lastResult, EvaluateResultResponse::class.java)
        // 断言最后一次做题的ID不为null
        Assertions.assertNotNull(lastResponse.id)

        // 根据最后一次做题的ID查询详情
        val resultString1 = mvc.perform(
            MockMvcRequestBuilders.post("/answer/detail")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(lastResponse.id)
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val detailResponse = objectMapper.readValue(resultString1, AnswerDetailResponse::class.java)
        // 断言
        Assertions.assertEquals(10, detailResponse.options.size)
        Assertions.assertEquals(request.questionsOption[1].optionValue, detailResponse.options[1].optionValue)
        Assertions.assertEquals(request.questionsOption[2].optionValue, detailResponse.options[2].optionValue)
        Assertions.assertEquals(request.questionsOption[3].optionValue, detailResponse.options[3].optionValue)
        Assertions.assertEquals(request.questionsOption[4].optionValue, detailResponse.options[4].optionValue)
    }

    private fun qasInterpretResult(
        interpretId: BigInteger,
        examinationPaperId: BigInteger,
        createId: BigInteger,
        resultTag: String,
        resultMsg: String,
        minValue: BigDecimal,
        maxValue: BigDecimal
    ): QasInterpretationOfResults {
        return QasInterpretationOfResults.builder()
            .setId(interpretId)
            .setExaminationPaperId(examinationPaperId)
            .setMinValue(minValue)
            .setMaxValue(maxValue)
            .setResultsTag(resultTag)
            .setCreatedBy(createId)
            .setUpdatedBy(createId)
            .setCreatedAt(LocalDateTime.now())
            .setUpdatedAt(LocalDateTime.now())
            .setResultsMsg(resultMsg)
            .setIsDel(false)
            .build()
    }

    private fun questionAnswerRecord(
        patientId: BigInteger,
        examinationPaperId: BigInteger,
        resultTag: String,
        resultMsg: String
    ): QasQuestionsAnswerRecord {
        return this.answerRecord(patientId, examinationPaperId, resultTag, resultMsg)
    }

    private fun answerRecord(
        patientId: BigInteger,
        examinationPaperId: BigInteger,
        resultTag: String,
        resultMsg: String,
        totalScore: BigDecimal? = null,
        recordId: BigInteger? = null
    ): QasQuestionsAnswerRecord = QasQuestionsAnswerRecord.builder()
        .setId(recordId ?: AppIdUtil.nextId())
        .setAnswerBy(patientId)
        .setExaminationPaperId(examinationPaperId)
        .setExaminationPaperCode("A" + AppIdUtil.nextId())
        .setResultsTag(resultTag)
        .setCreatedBy(patientId)
        .setCreatedAt(LocalDateTime.now())
        .setResultsMsg(resultMsg)
        .setTotalScore(totalScore)
        .build()
}
