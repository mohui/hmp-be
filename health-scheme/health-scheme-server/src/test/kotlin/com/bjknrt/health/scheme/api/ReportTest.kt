package com.bjknrt.health.scheme.api

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.BloodLipidsResult
import com.bjknrt.health.indicator.vo.FindListParam
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.entity.BloodPressure
import com.bjknrt.health.scheme.entity.HealthPlanDTO
import com.bjknrt.health.scheme.service.*
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService
import com.bjknrt.health.scheme.service.health.HealthManageFacadeService
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl
import com.bjknrt.health.scheme.service.impl.AcuteCoronaryDiseaseReportServiceImpl.Companion.ACD_BEHAVIOR_EXAMINATION_PAPER_CODE
import com.bjknrt.health.scheme.service.impl.CopdReportServiceImpl.Companion.COPD_BEHAVIOR_EXAMINATION_PAPER_CODE
import com.bjknrt.health.scheme.service.impl.DiabetesReportServiceImpl.Companion.DIABETES_BEHAVIOR_EXAMINATION_PAPER_CODE
import com.bjknrt.health.scheme.service.impl.HypertensionReportServiceImpl.Companion.BEHAVIOR_EXAMINATION_PAPER_CODE
import com.bjknrt.health.scheme.transfer.buildRemindFrequencyHealthAllParam
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.api.MedicationRemindApi
import com.bjknrt.medication.remind.vo.BatchHealthPlanResult
import com.bjknrt.medication.remind.vo.TypesAndPatientParam
import com.bjknrt.medication.remind.vo.UpsertHealthFrequencyResult
import com.bjknrt.question.answering.system.api.AnswerHistoryApi
import com.bjknrt.question.answering.system.vo.LastAnswerRecord
import com.bjknrt.question.answering.system.vo.LastAnswerRecordListRequest
import com.bjknrt.security.test.AppSecurityTestUtil
import com.fasterxml.jackson.databind.ObjectMapper
import me.danwi.sqlex.core.query.eq
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime

@AutoConfigureMockMvc
class ReportTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper


    lateinit var mvc: MockMvc

    @Autowired
    lateinit var reportApi: ReportApi

    @Autowired
    lateinit var hsStageReportTable: HsStageReportTable

    @Autowired
    lateinit var hsStageStatisticsTable: HsStageStatisticsTable

    @Autowired
    lateinit var hsStageReportStatisticsDetailTable: HsStageStatisticsDetailTable

    @Autowired
    lateinit var hypertensionReportService: HypertensionReportService

    @MockBean
    lateinit var questionsAnswerClient: AnswerHistoryApi

    @MockBean
    lateinit var indicatorClient: IndicatorApi

    @MockBean
    lateinit var medicationRemindClient: MedicationRemindApi

    @Autowired
    lateinit var diabetesReportService: DiabetesReportService

    @Autowired
    lateinit var copdReportService: CopdReportService

    @Autowired
    lateinit var acuteCoronaryDiseaseReportService: AcuteCoronaryDiseaseReportService


    @Autowired
    lateinit var hsDiabetesStageStatisticsTable: HsDiabetesStageStatisticsTable

    @Autowired
    lateinit var hsCopdVisitTable: HsCopdVisitTable

    @Autowired
    lateinit var hsAcuteCoronaryVisitTable: HsAcuteCoronaryVisitTable

    @Autowired
    lateinit var hsDiabetesStageStatisticsDetailTable: HsDiabetesStageStatisticsDetailTable

    @MockBean
    lateinit var healthManageDiabetesServiceImpl: HealthManageDiabetesServiceImpl

    @MockBean
    lateinit var healthManageFacadeService: HealthManageFacadeService

    @MockBean
    lateinit var clockInService: ClockInService

    @MockBean
    lateinit var healthPlanClient: HealthPlanApi

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()

        AppSecurityTestUtil.setCurrentUserInfo(
            userId = AppIdUtil.nextId()
        )

    }

    @Test
    fun pageTest() {
        val patientId = AppIdUtil.nextId()
        val report = HsStageReport.builder()
            .setId(AppIdUtil.nextId())
            .setHealthSchemeManagementInfoId(AppIdUtil.nextId())
            .setPatientId(patientId)
            .setPatientName("张三")
            .setReportName("高血压报告")
            .setReportStartDatetime(LocalDateTime.now().plusDays(-7))
            .setReportEndDatetime(LocalDateTime.now())
            .setCreatedBy(AppIdUtil.nextId())
            .setReportScore(BigDecimal(98))
            .setCreatedAt(LocalDateTime.now())
            .setStageReportType(StageReportType.HYPERTENSION.name)
            .setAge(0)
            .setFailMsg(null)
            .build()

        hsStageReportTable.save(report)

        this.mockClockInReminderViewReportPlan(patientId)
        this.mockDeleteReminderViewReportPlan(patientId)

        val page = reportApi.getReportPage(ReportPageRequest(1, 10, patientId))

        Assertions.assertEquals(true, page.total != 0L)
        Assertions.assertEquals(report.id, page._data?.get(0)?.id ?: BigDecimal.ONE)
    }

    @Test
    fun statisticsTest() {

        val patientId = AppIdUtil.nextId()
        val stageReportId = AppIdUtil.nextId()
        val report = HsStageReport.builder()
            .setId(stageReportId)
            .setHealthSchemeManagementInfoId(AppIdUtil.nextId())
            .setPatientId(patientId)
            .setPatientName("张三")
            .setReportName("高血压报告")
            .setReportStartDatetime(LocalDateTime.now().plusDays(-7))
            .setReportEndDatetime(LocalDateTime.now())
            .setCreatedBy(AppIdUtil.nextId())
            .setReportScore(BigDecimal(98))
            .setCreatedAt(LocalDateTime.now())
            .setStageReportType(StageReportType.HYPERTENSION.name)
            .setAge(0)
            .setFailMsg(null)
            .build()

        hsStageReportTable.save(report)

        val stageStatistics = HsStageStatistics.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setActualMeasureNumber(10)
            .setStandardMeasureNumber(8)
            .setSystolicBloodPressureAvg(BigDecimal(140.5))
            .setDiastolicBloodPressureAvg(BigDecimal(150.5))
            .setSystolicBloodPressureStandardDeviation(BigDecimal(0.9))
            .setDiastolicBloodPressureStandardDeviation(BigDecimal(0.9))
            .setIsBloodPressureAvgStandard(true)
            .setCreatedBy(AppIdUtil.nextId())
            .build()
        hsStageStatisticsTable.save(stageStatistics)


        val statistics = reportApi.getStageStatistics(stageReportId)

        Assertions.assertEquals(stageStatistics.id, statistics.id)
        Assertions.assertEquals(stageStatistics.actualMeasureNumber, statistics.actualMeasureNumber)
        Assertions.assertEquals(report.id, statistics.stageReportId)
        Assertions.assertEquals(report.patientName, statistics.patientName)
    }

    @Test
    fun listTest() {

        val stageReportId = AppIdUtil.nextId()
        var detail = HsStageStatisticsDetail.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setSystolicBloodPressure(BigDecimal(140.5))
            .setDiastolicBloodPressure(BigDecimal(145.5))
            .setIsSystolicBloodPressureMax(false)
            .setIsDiastolicBloodPressureMax(true)
            .setMeasureDatetime(LocalDateTime.now().plusHours(-2))
            .setCreatedBy(AppIdUtil.nextId())
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsStageReportStatisticsDetailTable.save(detail)

         detail = HsStageStatisticsDetail.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setSystolicBloodPressure(BigDecimal(160.5))
            .setDiastolicBloodPressure(BigDecimal(150.5))
            .setIsSystolicBloodPressureMax(true)
            .setIsDiastolicBloodPressureMax(false)
            .setMeasureDatetime(LocalDateTime.now().plusHours(-1))
            .setCreatedBy(AppIdUtil.nextId())
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsStageReportStatisticsDetailTable.save(detail)

        detail = HsStageStatisticsDetail.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setSystolicBloodPressure(BigDecimal(120.5))
            .setDiastolicBloodPressure(BigDecimal(130.5))
            .setIsSystolicBloodPressureMax(false)
            .setIsDiastolicBloodPressureMax(false)
            .setMeasureDatetime(LocalDateTime.now())
            .setCreatedBy(AppIdUtil.nextId())
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsStageReportStatisticsDetailTable.save(detail)


        val detailList = reportApi.getStageStatisticsDetailList(stageReportId)

        Assertions.assertEquals(3, detailList.size)
        Assertions.assertEquals(detail.stageReportId, detailList[0].stageReportId)
        Assertions.assertEquals(false, detailList[0].isSystolicBloodPressureMax)
        Assertions.assertEquals(true, detailList[0].isDiastolicBloodPressureMax)
    }

    @Test
    fun generatorTest() {
        val now = LocalDate.now()
        val hypertensionReport = HypertensionReport(
            patientId = BigInteger.ONE,
            patientName = "zs",
            healthSchemeManagementInfoId = AppIdUtil.nextId(),
            gender = Gender.MAN,
            age = 25,
            isStandard = true,
            startDateTime = now.plusDays(-7).atStartOfDay(),
            endDateTime = now.atStartOfDay(),
            bloodPressureList = listOf(
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-7).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-6).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-5).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-4).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-3).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-2).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-1).atStartOfDay()),
            ),
            smokeNum = 0,
            height = BigDecimal.valueOf(200),
            weight = BigDecimal.valueOf(80),
            waistline = BigDecimal.valueOf(80),
            reportName = "高血压报告",
            planFrequencyValue = listOf(),
            managementStage = ManageStage.INITIAL_STAGE.value
        )
        Mockito.`when`(
            questionsAnswerClient.getLastAnswerRecordList(
                LastAnswerRecordListRequest(
                    examinationPaperCode = BEHAVIOR_EXAMINATION_PAPER_CODE,
                    answerBy = hypertensionReport.patientId,
                    needNum = 1,
                    startDate = hypertensionReport.startDateTime,
                    endDate = hypertensionReport.endDateTime
                )
            )
        ).thenReturn(
            listOf(
                LastAnswerRecord(
                    id = BigInteger.valueOf(1),
                    answerBy = hypertensionReport.patientId,
                    resultsTag = "",
                    resultsMsg = "",
                    totalScore = BigDecimal.TEN,
                    createdBy = BigInteger.ONE,
                    createdAt = LocalDateTime.now(),
                )
            )
        )
        val patientId = hypertensionReport.patientId

        //模拟创建查看报告计划
        mockCreateReminderViewReportPlan(patientId)
        hypertensionReportService.generateReport(hypertensionReport)

        hsStageReportTable.select()
            .where(HsStageReportTable.HealthSchemeManagementInfoId eq hypertensionReport.healthSchemeManagementInfoId)
            .findOne()
            ?.let { report ->
                val stageStatistics = reportApi.getStageStatistics(report.id)

                Assertions.assertEquals(40, stageStatistics.reportScore?.toInt())
                Assertions.assertEquals(7, stageStatistics.actualMeasureNumber)
                Assertions.assertEquals(7, stageStatistics.standardMeasureNumber)
                Assertions.assertEquals(7, stageStatistics.actualMeasureNumber)
                Assertions.assertEquals(100.0, stageStatistics.systolicBloodPressureAvg.toDouble())
                Assertions.assertEquals(80.0, stageStatistics.diastolicBloodPressureAvg.toDouble())
                Assertions.assertEquals(0.0, stageStatistics.systolicBloodPressureStandardDeviation.toDouble())
                Assertions.assertEquals(0.0, stageStatistics.diastolicBloodPressureStandardDeviation.toDouble())
                Assertions.assertEquals(true, stageStatistics.isBloodPressureAvgStandard)
            }
    }


    @Test
    fun diabetesStatisticsTest() {

        val patientId = AppIdUtil.nextId()
        val stageReportId = AppIdUtil.nextId()
        val report = HsStageReport.builder()
            .setId(stageReportId)
            .setHealthSchemeManagementInfoId(AppIdUtil.nextId())
            .setPatientId(patientId)
            .setPatientName("张三")
            .setReportName("糖尿病报告")
            .setReportStartDatetime(LocalDateTime.now().plusDays(-7))
            .setReportEndDatetime(LocalDateTime.now())
            .setCreatedBy(AppIdUtil.nextId())
            .setCreatedAt(LocalDateTime.now())
            .setReportScore(BigDecimal(98))
            .setStageReportType(StageReportType.DIABETES.name)
            .setAge(0)
            .setFailMsg(null)
            .build()
        hsStageReportTable.save(report)

        val stageStatistics = HsDiabetesStageStatistics.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setFastingBloodSugarMeasureNumber(10)
            .setBeforeLunchBloodSugarMeasureNumber(8)
            .setAfterMealBloodSugarBloodSugarMeasureNumber(8)
            .setLowBloodSugarNumber(2)
            .setBloodSugarStandardRate(BigDecimal(80))
            .setCreatedBy(AppIdUtil.nextId())
            .build()
        hsDiabetesStageStatisticsTable.save(stageStatistics)


        val statistics = reportApi.getDiabetesStageStatistics(stageReportId)

        Assertions.assertEquals(stageStatistics.id, statistics.id)
        Assertions.assertEquals(
            stageStatistics.fastingBloodSugarMeasureNumber,
            statistics.fastingBloodSugarMeasureNumber
        )
        Assertions.assertEquals(
            stageStatistics.beforeLunchBloodSugarMeasureNumber,
            statistics.beforeLunchBloodSugarMeasureNumber
        )
        Assertions.assertEquals(
            stageStatistics.afterMealBloodSugarBloodSugarMeasureNumber,
            statistics.afterMealBloodSugarBloodSugarMeasureNumber
        )
        Assertions.assertEquals(stageStatistics.lowBloodSugarNumber, statistics.lowBloodSugarNumber)
        Assertions.assertEquals(report.id, statistics.stageReportId)
        Assertions.assertEquals(report.patientName, statistics.patientName)
    }

    @Test
    fun diabetesListTest() {

        val stageReportId = AppIdUtil.nextId()
        var detail = HsDiabetesStageStatisticsDetail.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setMeasureDatetime(LocalDateTime.now().plusHours(-1))
            .setCreatedBy(AppIdUtil.nextId())
            .setFastingBloodSugar(BigDecimal(10))
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsDiabetesStageStatisticsDetailTable.save(detail)


        detail = HsDiabetesStageStatisticsDetail.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setMeasureDatetime(LocalDateTime.now().plusHours(-2))
            .setCreatedBy(AppIdUtil.nextId())
            .setRandomBloodSugar(BigDecimal(10))
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsDiabetesStageStatisticsDetailTable.save(detail)

        detail = HsDiabetesStageStatisticsDetail.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setMeasureDatetime(LocalDateTime.now().plusHours(-3))
            .setCreatedBy(AppIdUtil.nextId())
            .setAfterMealBloodSugar(BigDecimal(10))
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsDiabetesStageStatisticsDetailTable.save(detail)

        detail = HsDiabetesStageStatisticsDetail.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setMeasureDatetime(LocalDateTime.now().plusHours(-4))
            .setCreatedBy(AppIdUtil.nextId())
            .setBeforeLunchBloodSugar(BigDecimal(10))
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsDiabetesStageStatisticsDetailTable.save(detail)


        detail = HsDiabetesStageStatisticsDetail.builder()
            .setId(AppIdUtil.nextId())
            .setStageReportId(stageReportId)
            .setMeasureDatetime(LocalDateTime.now().plusHours(-5))
            .setCreatedBy(AppIdUtil.nextId())
            .setBeforeDinnerBloodSugar(BigDecimal(10))
            .setCreatedAt(LocalDateTime.now())
            .build()
        hsDiabetesStageStatisticsDetailTable.save(detail)


        val detailList = reportApi.getDiabetesStageStatisticsDetailList(stageReportId)

        Assertions.assertEquals(5, detailList.size)
        Assertions.assertEquals(detail.stageReportId, detailList[0].stageReportId)
        Assertions.assertEquals(10, detailList[0].beforeDinnerBloodSugar?.toInt() ?: 0)
    }

    @Test
    fun diabetesGeneratorTest() {
        val now = LocalDate.now()
        val reportParam = DiabetesReport(
            patientId = BigInteger.ONE,
            patientName = "zs",
            age = 25,
            healthManageId = AppIdUtil.nextId(),
            manageStage = ManageStage.INITIAL_STAGE,
            isStandard = true,
            startDateTime = now.plusDays(-7).atStartOfDay(),
            endDateTime = now.atStartOfDay(),
            reportName = "糖尿病报告",
            bloodSugarList = listOf(
                BloodSugar(
                    BigDecimal.valueOf(4.0),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    now.plusDays(-1).atStartOfDay()
                ),
                BloodSugar(
                    null,
                    BigDecimal.valueOf(5.1),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    now.plusDays(-2).atStartOfDay()
                ),
                BloodSugar(
                    null,
                    null,
                    BigDecimal.valueOf(5.2),
                    null,
                    null,
                    null,
                    null,
                    null,
                    now.plusDays(-3).atStartOfDay()
                ),
                BloodSugar(
                    null,
                    null,
                    BigDecimal.valueOf(3.2),
                    null,
                    null,
                    null,
                    null,
                    null,
                    now.plusDays(-4).atStartOfDay()
                ),
            ),
            planFrequencyValue = listOf(),
        )

        Mockito.`when`(

            questionsAnswerClient.getLastAnswerRecordList(
                LastAnswerRecordListRequest(
                    examinationPaperCode = DIABETES_BEHAVIOR_EXAMINATION_PAPER_CODE,
                    answerBy = reportParam.patientId,
                    needNum = 1,
                    startDate = reportParam.startDateTime,
                    endDate = reportParam.endDateTime
                )
            )
        ).thenReturn(
            listOf(
                LastAnswerRecord(
                    id = BigInteger.valueOf(1),
                    answerBy = reportParam.patientId,
                    resultsTag = "",
                    resultsMsg = "",
                    totalScore = BigDecimal.TEN,
                    createdBy = BigInteger.ONE,
                    createdAt = LocalDateTime.now(),
                )
            )
        )
        Mockito.`when`(
            indicatorClient.bloodLipidsList(
                FindListParam(
                    reportParam.startDateTime,
                    reportParam.endDateTime,
                    reportParam.patientId
                )
            )
        ).thenReturn(
            listOf(
                BloodLipidsResult(
                    AppIdUtil.nextId(),
                    LocalDateTime.now().plusDays(-1),
                    LocalDateTime.now().plusDays(-1),
                    BigDecimal(10),
                    BigDecimal(10),
                    BigDecimal(10),
                    BigDecimal(10),
                ),
                BloodLipidsResult(
                    AppIdUtil.nextId(),
                    LocalDateTime.now().plusDays(-2),
                    LocalDateTime.now().plusDays(-2),
                    BigDecimal(10),
                    BigDecimal(10),
                    BigDecimal(10),
                    BigDecimal(10),
                ),
            )
        )
        val healthManageId = reportParam.healthManageId
        val patientId = reportParam.patientId

        //模拟创建查看报告计划
        mockCreateReminderViewReportPlan(patientId)

        diabetesReportService.generateReport(reportParam)

        hsStageReportTable.select()
            .where(HsStageReportTable.HealthSchemeManagementInfoId eq healthManageId)
            .findOne()
            ?.let { report ->
                val stageStatistics = reportApi.getDiabetesStageStatistics(report.id)

                Assertions.assertEquals(55, stageStatistics.reportScore?.toInt())
                Assertions.assertEquals(1, stageStatistics.lowBloodSugarNumber)
                Assertions.assertEquals(2, stageStatistics.afterMealBloodSugarBloodSugarMeasureNumber)
                Assertions.assertEquals(75.00, stageStatistics.bloodSugarStandardRate.toDouble())
                Assertions.assertEquals(reportParam.startDateTime, stageStatistics.reportStartDatetime)
                Assertions.assertEquals(reportParam.endDateTime, stageStatistics.reportEndDatetime)

            } ?: Assertions.fail()
    }


    @Test
    fun copdGeneratorTest() {
        val healthManageId = AppIdUtil.nextId()
        val now = LocalDate.now()
        val patientId = AppIdUtil.nextId()


        val reportParam = CopdReport(
            patientId = patientId,
            patientName = "zs",
            age = 25,
            healthManageId = healthManageId,
            startDateTime = now.plusDays(-7).atStartOfDay(),
            endDateTime = now.atStartOfDay(),
            reportName = "慢阻肺阶段性报告",
            pulseOxygenSaturationList = listOf(
                PulseOxygenSaturation(
                    BigDecimal.valueOf(4.0),
                    now.plusDays(-1).atStartOfDay()
                ),
                PulseOxygenSaturation(
                    BigDecimal.valueOf(5.1),
                    now.plusDays(-2).atStartOfDay()
                ),
                PulseOxygenSaturation(
                    BigDecimal.valueOf(5.2),
                    now.plusDays(-3).atStartOfDay()
                ),
                PulseOxygenSaturation(
                    BigDecimal.valueOf(3.2),
                    now.plusDays(-4).atStartOfDay()
                ),
            ),
            planFrequencyValue = listOf(),
        )

        //模拟创建查看报告计划
        mockCreateReminderViewReportPlan(patientId)

        //模拟行为习惯问卷记录查询
        Mockito.`when`(
            questionsAnswerClient.getLastAnswerRecordList(
                LastAnswerRecordListRequest(
                    examinationPaperCode = COPD_BEHAVIOR_EXAMINATION_PAPER_CODE,
                    answerBy = reportParam.patientId,
                    needNum = 1,
                    startDate = reportParam.startDateTime,
                    endDate = reportParam.endDateTime
                )
            )
        ).thenReturn(
            listOf(
                LastAnswerRecord(
                    id = BigInteger.valueOf(1),
                    answerBy = reportParam.patientId,
                    resultsTag = "",
                    resultsMsg = "",
                    totalScore = BigDecimal.TEN,
                    createdBy = BigInteger.ONE,
                    createdAt = LocalDateTime.now(),
                )
            )
        )

        hsCopdVisitTable.save(
            HsCopdVisit.builder()
                .setId(AppIdUtil.nextId())
                .setPatientId(reportParam.patientId)
                .setPatientName(reportParam.patientName)
                .setFollowWay(FllowWay.FAMILY.name)
                .setFollowDate(reportParam.startDateTime)
                .setPulseOxygenSaturation(10.0)
                .setPulse(10.0)
                .setSignsSbp(120.0)
                .setSignsDbp(150.0)
                .setSignsHeight(160.4)
                .setSignsWeight(70.4)
                .setLifeSportPerWeek(12)
                .setLifeSportPerTime(12.0)
                .setLifeSaltSituation(LifeSalt.LIGHT.name)
                .setLifeFollowMedicalAdvice(LifeState.BAD.name)
                .setDrugCompliance(DrugCompliance.GAP.name)
                .setPulmonaryFunction(12.0)
                .setCreatedBy(BigInteger.ONE)
                .setUpdatedBy(BigInteger.ONE)
                .build()
        )

        copdReportService.generateReport(reportParam)

        hsStageReportTable.select()
            .where(HsStageReportTable.HealthSchemeManagementInfoId eq healthManageId)
            .where(HsStageReportTable.PatientId eq patientId)
            .findOne()
            ?.let { report ->
                val stageStatistics = reportApi.getCopdStageStatistics(report.id)

                if (report.reportScore != null) {
                    Assertions.assertEquals(30, stageStatistics.reportScore?.toInt())
                    Assertions.assertEquals(reportParam.startDateTime, stageStatistics.reportStartDatetime)
                    Assertions.assertEquals(reportParam.endDateTime, stageStatistics.reportEndDatetime)

                    val detailList = reportApi.getCopdStatisticsDetailList(report.id)
                    Assertions.assertEquals(4, detailList.size)
                }

                Assertions.assertEquals(reportParam.reportName, report.reportName)
                Assertions.assertEquals(reportParam.age, report.age)
                Assertions.assertEquals(reportParam.patientName, report.patientName)
                Assertions.assertEquals(StageReportType.COPD.name, report.stageReportType)

            } ?: Assertions.fail()
    }

    @Test
    fun acuteCoronaryDiseaseGeneratorTest() {
        val healthManageId = AppIdUtil.nextId()
        val now = LocalDate.now()
        val patientId = AppIdUtil.nextId()


        val reportParam = AcuteCoronaryDiseaseReport(
            patientId = patientId,
            patientName = "zs",
            age = 25,
            height = BigDecimal(180),
            weight = BigDecimal(65),
            waistline = BigDecimal(100),
            healthManageId = healthManageId,
            managementStage = ManageStage.INITIAL_STAGE.name,
            isStandard = true,
            startDateTime = now.plusDays(-7).atStartOfDay(),
            endDateTime = now.atStartOfDay(),
            reportName = "冠心病阶段性报告",
            bloodPressureList = listOf(
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-7).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-6).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-5).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-4).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-3).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-2).atStartOfDay()),
                BloodPressure(BigDecimal.valueOf(100), BigDecimal.valueOf(80), now.plusDays(-1).atStartOfDay()),
            ),
            planFrequencyValue = listOf(),
        )

        //模拟创建查看报告计划
        mockDeleteReminderViewReportPlan(patientId)

        //模拟行为习惯问卷记录查询
        Mockito.`when`(
            questionsAnswerClient.getLastAnswerRecordList(
                LastAnswerRecordListRequest(
                    examinationPaperCode = ACD_BEHAVIOR_EXAMINATION_PAPER_CODE,
                    answerBy = reportParam.patientId,
                    needNum = 1,
                    startDate = reportParam.startDateTime,
                    endDate = reportParam.endDateTime
                )
            )
        ).thenReturn(
            listOf(
                LastAnswerRecord(
                    id = BigInteger.valueOf(1),
                    answerBy = reportParam.patientId,
                    resultsTag = "",
                    resultsMsg = "",
                    totalScore = BigDecimal.TEN,
                    createdBy = BigInteger.ONE,
                    createdAt = LocalDateTime.now(),
                )
            )
        )

        hsAcuteCoronaryVisitTable.save(
            HsAcuteCoronaryVisit.builder()
                .setId(AppIdUtil.nextId())
                .setPatientId(reportParam.patientId)
                .setPatientName(reportParam.patientName)
                .setFollowWay(FllowWay.FAMILY.name)
                .setFollowDate(reportParam.startDateTime)
                .setSignsSbp(5.0)
                .setSignsDbp(2.0)
                .setSignsWeight(120.0)
                .setSignsHeartRate(90.0)
                .setLifeCigarettesPerDay(160)
                .setLifeAlcoholPerDay(70.4)
                .setLifeSportPerWeek(12)
                .setLifeSportPerTime(12.0)
                .setLifeSaltSituation(LifeSalt.LIGHT.name)
                .setLifeFollowMedicalAdvice(LifeState.BAD.name)
                .setDrugCompliance(DrugCompliance.GAP.name)
                .setCreatedBy(BigInteger.ONE)
                .setUpdatedBy(BigInteger.ONE)
                .build()
        )

        //模拟创建查看报告计划
        mockCreateReminderViewReportPlan(patientId)

        acuteCoronaryDiseaseReportService.generateReport(reportParam)

        hsStageReportTable.select()
            .where(HsStageReportTable.HealthSchemeManagementInfoId eq healthManageId)
            .where(HsStageReportTable.PatientId eq patientId)
            .findOne()
            ?.let { report ->
                val stageStatistics = reportApi.getAcuteCoronaryDiseaseStageStatistics(report.id)

                if (report.reportScore != null) {
                    Assertions.assertEquals(50, stageStatistics.reportScore?.toInt())
                    Assertions.assertEquals(reportParam.startDateTime, stageStatistics.reportStartDatetime)
                    Assertions.assertEquals(reportParam.endDateTime, stageStatistics.reportEndDatetime)

                    val detailList = reportApi.getAcuteCoronaryDiseaseStageStatisticsDetailList(report.id)
                    Assertions.assertEquals(7, detailList.size)
                }

                Assertions.assertEquals(reportParam.reportName, report.reportName)
                Assertions.assertEquals(reportParam.age, report.age)
                Assertions.assertEquals(reportParam.patientName, report.patientName)
                Assertions.assertEquals(StageReportType.ACUTE_CORONARY_DISEASE.name, report.stageReportType)

            } ?: Assertions.fail()
    }

    private fun mockDeleteReminderViewReportPlan(patientId: BigInteger) {
        Mockito.doNothing().`when`(medicationRemindClient).batchDeleteByTypeAndPatientId (
            listOf(
                TypesAndPatientParam(
                    patientId = patientId,
                    types = listOf(com.bjknrt.medication.remind.vo.HealthPlanType.REMINDER_VIEW_REPORT)
                )
            )
        )
    }

    private fun mockClockInReminderViewReportPlan(patientId: BigInteger) {
        Mockito.doNothing().`when`(clockInService).clockIn(
            patientId,
            com.bjknrt.health.scheme.vo.HealthPlanType.REMINDER_VIEW_REPORT,
            LocalDateTime.now()
        )
    }

    private fun mockCreateReminderViewReportPlan(patientId: BigInteger) {
        val healthPlans = listOf(
            HealthPlanDTO(
                name = AbstractHealthManageService.REMINDER_VIEW_REPORT_PLAN_NAME,
                type = HealthPlanType.REMINDER_VIEW_REPORT,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = LocalDate.now().atStartOfDay(),
                cycleEndTime = null,
                displayTime = LocalDate.now().atStartOfDay(),
                clockDisplay = true,
                frequencys = AbstractHealthManageService.REMINDER_VIEW_REPORT_FREQUENCY
            )
        )
        val frequencyHealthParams = buildRemindFrequencyHealthAllParam(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = listOf()
        )
        Mockito.`when`(
            healthPlanClient.batchAddHealthPlan(
                frequencyHealthParams
            )
        ).doReturn(
            BatchHealthPlanResult(
                healthPlans = listOf(
                    UpsertHealthFrequencyResult(
                        id = AppIdUtil.nextId(),
                        type = frequencyHealthParams.healthPlans[0].type,
                        cycleStartTime = frequencyHealthParams.healthPlans[0].cycleStartTime?: LocalDate.now().atStartOfDay(),
                        cycleEndTime = frequencyHealthParams.healthPlans[0].cycleEndTime
                    )
                ),
                drugPlans = listOf()
            )

        )
    }


}
