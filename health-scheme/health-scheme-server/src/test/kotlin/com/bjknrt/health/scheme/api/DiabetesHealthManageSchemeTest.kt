package com.bjknrt.health.scheme.api

import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.doctor.patient.management.vo.PatientSynthesisTag
import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.*
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.constant.*
import com.bjknrt.health.scheme.entity.DrugPlanDTO
import com.bjknrt.health.scheme.entity.HealthPlanDTO
import com.bjknrt.health.scheme.enums.DietPlanTypeEnum
import com.bjknrt.health.scheme.enums.ExaminationCodeEnum
import com.bjknrt.health.scheme.job.DiabetesJob
import com.bjknrt.health.scheme.job.DiabetesJob.Companion.DIABETES_EXAMINATION_CODE_LIST
import com.bjknrt.health.scheme.job.HypertensionJob
import com.bjknrt.health.scheme.job.HypertensionJob.Companion.HYPERTENSION_EXAMINATION_CODE_LIST
import com.bjknrt.health.scheme.service.ExaminationService
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.DIET_PLAN_DESC
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.DIET_PLAN_FREQUENCIES
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.DIET_PLAN_GROUP
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.DIET_PLAN_NAME
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.NOT_EVALUATE_DRUG_PROGRAM_PLAN_FREQUENCIES
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.NOT_EVALUATE_DRUG_PROGRAM_PLAN_NAME
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.NOT_DIET_EVALUATE_FREQUENCIES
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.NOT_DIET_EVALUATE_NAME
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.NOT_EVALUATE_SPORT_PLAN_FREQUENCIES
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.NOT_EVALUATE_SPORT_PLAN_NAME
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.OFFLINE_ACUTE_CORONARY_DISEASE_PLAN_NAME
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.OFFLINE_CEREBRAL_STROKE_PLAN_NAME
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.OFFLINE_COPD_FREQUENCY
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.OFFLINE_COPD_PLAN_NAME
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.OFFLINE_DIABETES_PLAN_NAME
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.OFFLINE_HYPERTENSION_PLAN_NAME
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.OFFLINE_VISIT_FREQUENCY
import com.bjknrt.health.scheme.service.health.AbstractHealthManageService.Companion.SCIENCE_POPULARIZATION_PLAN_FREQUENCIES
import com.bjknrt.health.scheme.service.health.impl.HealthManageCerebralStrokeServiceImpl
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl.Companion.DIABETES_AFTER_MEAL_BLOOD_SUGAR_PLAN_DESC_MSG_MAP
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl.Companion.DIABETES_AFTER_MEAL_BLOOD_SUGAR_PLAN_NAME
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl.Companion.DIABETES_BEFORE_MEAL_BLOOD_SUGAR_PLAN_DESC_MSG_MAP
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl.Companion.DIABETES_BEFORE_MEAL_BLOOD_SUGAR_PLAN_NAME
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl.Companion.DIABETES_FASTING_BLOOD_SUGAR_PLAN_DESC_MSG_MAP
import com.bjknrt.health.scheme.service.health.impl.HealthManageDiabetesServiceImpl.Companion.DIABETES_FASTING_BLOOD_SUGAR_PLAN_NAME
import com.bjknrt.health.scheme.service.health.impl.HealthManageHypertensionServiceImpl
import com.bjknrt.health.scheme.service.impl.DiabetesReportServiceImpl
import com.bjknrt.health.scheme.service.impl.HypertensionReportServiceImpl
import com.bjknrt.health.scheme.transfer.buildRemindFrequencyHealthAllParam
import com.bjknrt.health.scheme.util.getFrequencyIds
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.health.scheme.vo.FrequencyHealthAllParam
import com.bjknrt.health.scheme.vo.FrequencyHealthParams
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.api.MedicationRemindApi
import com.bjknrt.medication.remind.vo.*
import com.bjknrt.medication.remind.vo.HealthPlanType
import com.bjknrt.question.answering.system.api.AnswerHistoryApi
import com.bjknrt.question.answering.system.vo.LastAnswerRecord
import com.bjknrt.question.answering.system.vo.LastAnswerRecordListRequest
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.api.HealthServiceApi
import com.bjknrt.user.permission.centre.vo.HealthService
import me.danwi.sqlex.core.query.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import org.mockito.kotlin.capture
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
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Collectors

@AutoConfigureMockMvc
class DiabetesHealthManageSchemeTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    lateinit var mvc: MockMvc

    @Autowired
    lateinit var manageApi: ManageApi

    @MockBean
    lateinit var servicePackageClient: HealthServiceApi

    @MockBean
    lateinit var medicationRemindClient: MedicationRemindApi

    @MockBean
    lateinit var healthPlanClient: HealthPlanApi

    @MockBean
    lateinit var patientClient: PatientApi

    @MockBean
    lateinit var indicatorClient: IndicatorApi

    @Autowired
    lateinit var diabetesJob: DiabetesJob

    @Autowired
    lateinit var healthSchemeManagementInfoTable: HsHealthSchemeManagementInfoTable

    @Autowired
    lateinit var hsmHealthPlanTable: HsHsmHealthPlanTable

    @MockBean
    lateinit var questionsAnswerClient: AnswerHistoryApi

    @Autowired
    lateinit var hypertensionJob: HypertensionJob

    @Autowired
    lateinit var examinationApi: ExaminationApi
    @Autowired
    lateinit var examinationService: ExaminationService

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

    /**
     * 购买服务包后创建健康管理方案的测试方法（糖尿病方案）
     */
    @Test
    fun addDiabetesHealthSchemeManageByServicePackageTest() {
        val patientId = AppIdUtil.nextId()
        val startDate = LocalDate.now().minusDays(SEVEN_DAY).plusDays(2)
        addDiabetesByServicePackage(startDate, patientId)
    }

    /**
     * 创建健康管理方案的测试方法（糖尿病方案）
     */
    private fun addDiabetesByServicePackage(startDate: LocalDate, patientId: BigInteger) {
        //订阅的服务包（糖尿病包）
        val request = AddManageByServicePackageRequest(
            patientId = patientId,
            startDate = startDate,
            serviceCodeList = listOf(DIABETES_SERVICE_CODE)
        )

        //服务包模拟
        Mockito.doReturn(
            listOf(
                HealthService(
                    healthServiceId = AppIdUtil.nextId(),
                    healthServiceCode = DIABETES_SERVICE_CODE,
                    healthServiceName = "糖尿病服务包",
                    isSigned = true,
                    activationDate = LocalDateTime.now()
                )
            )
        ).`when`(servicePackageClient).getHealthServicePatient(patientId)


        //需校验的参数（频率，远程频率id）
        val frequencyHealthAllParamList = mutableListOf<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>()
        val frequencyIdMap = mutableMapOf<HealthPlanType, List<BigInteger>>()

        //方案相关模拟
        val newJobId = this.mockDiabetesManageSchemeData(patientId)

        //通用计划模拟
        this.mockDiabetesCommonPlanData(patientId, startDate, frequencyHealthAllParamList, frequencyIdMap)

        //个性化计划模拟
        this.mockDiabetesPersonalPlanData(
            startDate,
            patientId,
            ManageStage.INITIAL_STAGE,
            frequencyHealthAllParamList,
            frequencyIdMap
        )

        //创建方案接口执行
        manageApi.addHealthSchemeManageByServicePackage(request)

        //方案和个性化计划校验
        this.diabetesManageSchemeAndPersonalPlanVerify(
            patientId,
            startDate,
            ManageStage.INITIAL_STAGE,
            frequencyHealthAllParamList,
            frequencyIdMap,
            1,
            1,
            null,
            newJobId,
        )

        //通用计划的校验
        this.diabetesCommonPlanVerify(patientId, frequencyHealthAllParamList, frequencyIdMap)
        // 添加答题结果模拟数据
        this.mockDiabetesAdapter(patientId)
    }

    /**
     * job回调的测试方法（糖尿病方案）
     */
    @Test
    fun diabetesHealthManageJobHandlerTest() {
//        val patientId = AppIdUtil.nextId()
//        val currentDate = LocalDate.now()
//        val oldStartDate = currentDate.minusDays(SEVEN_DAY).plusDays(2)
//        val oldEndDate = oldStartDate.plusDays(SEVEN_DAY)
//
//        //准备旧数据：添加逻辑（切换使用）
//        this.addDiabetesByServicePackage(oldStartDate, patientId)
//
//        //查询旧数据（方案和计划信息）
//        val oldManageSchemeAndPlanInfo = this.getManageSchemeAndPlanInfo(patientId)
//        val manageStage = oldManageSchemeAndPlanInfo.first.knManagementStage
//            ?: Assertions.fail(AppSpringUtil.getMessage("health-manage-scheme.stage-is-null"))
//
//        val oldJobId = oldManageSchemeAndPlanInfo.first.knJobId
//            ?: Assertions.fail(AppSpringUtil.getMessage("health-manage-scheme.stage-is-null"))
//        val oldPlanList = oldManageSchemeAndPlanInfo.second
//
//        // 旧答题结果
//        val oldManageDetailExaminationAdapter = examinationService.queryCurrentSchemeExaminationAdapterList(
//            ExaminationService.QueryCurrentSchemeExaminationAdapterListParam(
//                knPatientId = patientId,
//                knHealthManageSchemeId = oldManageSchemeAndPlanInfo.first.knId,
//                knExaminationPaperCodeSet = DIABETES_EXAMINATION_CODE_LIST
//            )
//        )
//
//        //需校验的参数（频率，远程频率id）
//        val frequencyHealthAllParamList = mutableListOf<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>()
//        val frequencyIdMap = mutableMapOf<HealthPlanType, List<BigInteger>>()
//
//        //方案相关的模拟
//        val newJobId = this.mockDiabetesManageSchemeData(patientId, oldManageSchemeAndPlanInfo)
//
//        //阶段报告相关的模拟
//        this.mockDiabetesStageReportData(patientId, oldStartDate, oldEndDate, oldPlanList)
//
//        //这里根据血糖监测次数和血糖值模拟的数据是否达标，确定下一阶段
//        val judgeStandard = false
//        val nextStage = diabetesJob.getNextStage(ManageStage.valueOf(manageStage), judgeStandard)
//
//        //个性化计划的模拟
//        this.mockDiabetesPersonalPlanData(
//            oldEndDate,
//            patientId,
//            nextStage,
//            frequencyHealthAllParamList,
//            frequencyIdMap
//        )
//        //提醒查看报告计划模拟
//        this.mockReminderViewReportPlan(patientId, frequencyHealthAllParamList, frequencyIdMap)
//        //job回调执行
//        diabetesJob.diabetesHealthManageJobHandler()
//
//        //方案和个性化计划校验
//        this.diabetesManageSchemeAndPersonalPlanVerify(
//            patientId,
//            oldEndDate,
//            nextStage,
//            frequencyHealthAllParamList,
//            frequencyIdMap,
//            1,
//            3,
//            oldJobId,
//            newJobId
//        )
//
//        //通用计划校验
//        this.diabetesCommonPlanVerify(patientId, oldPlanList)
//        // 糖尿病答题结果校验
//        this.diabetesAdapterVerify(patientId, oldManageDetailExaminationAdapter)
    }

    /**
     * 购买服务包后创建健康管理方案的测试方法（高血压方案）
     */
    @Test
    fun addHypertensionHealthSchemeManageByServicePackageTest() {
        val patientId = AppIdUtil.nextId()
        val startDate = LocalDate.now().minusDays(SEVEN_DAY).plusDays(2)
        addHypertensionByServicePackage(startDate, patientId)
    }

    private fun addHypertensionByServicePackage(startDate: LocalDate, patientId: BigInteger) {
        //订阅的服务包（高血压包）
        val request = AddManageByServicePackageRequest(
            patientId = patientId,
            startDate = startDate,
            serviceCodeList = listOf(HYPERTENSION_SERVICE_CODE)
        )
        Mockito.doReturn(
            listOf(
                HealthService(
                    healthServiceId = AppIdUtil.nextId(),
                    healthServiceCode = HYPERTENSION_SERVICE_CODE,
                    healthServiceName = "高血压服务包",
                    isSigned = true,
                    activationDate = LocalDateTime.now()
                )
            )
        ).`when`(servicePackageClient).getHealthServicePatient(patientId)
        //需校验的参数（频率，远程频率id）
        val frequencyHealthAllParamList = mutableListOf<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>()
        val frequencyIdMap = mutableMapOf<HealthPlanType, List<BigInteger>>()

        //方案相关模拟
        val newJobId = this.mockHypertensionManageSchemeData(patientId)

        //通用计划模拟
        this.mockHypertensionCommonPlanData(patientId, startDate, frequencyHealthAllParamList, frequencyIdMap)

        //个性化计划模拟
        this.mockHypertensionPersonalPlanData(
            startDate = startDate,
            patientId = patientId,
            managementStage = ManageStage.INITIAL_STAGE,
            frequencyHealthAllParams = frequencyHealthAllParamList,
            frequencyIdMap = frequencyIdMap
        )

        //创建方案接口执行
        manageApi.addHealthSchemeManageByServicePackage(request)

        //方案和个性化计划校验
        this.hypertensionManageSchemeAndPersonalPlanVerify(
            patientId = patientId,
            startDate = startDate,
            stage = ManageStage.INITIAL_STAGE,
            frequencyHealthAllParams = frequencyHealthAllParamList,
            frequencyIdMap = frequencyIdMap,
            remoteInvokeCount = 1,
            onlyAddRemoteInvokeCount = 1,
            oldJobId = null,
            newJobId = newJobId
        )

        //通用计划校验
        this.hypertensionCommonPlanVerify(patientId, frequencyHealthAllParamList, frequencyIdMap)
        // 高血压模拟答题结果
        this.mockHypertensionAdapter(patientId)
    }

    /**
     * job回调的测试方法（高血压方案）
     */
    @Test
    fun hypertensionHealthManageJobHandlerTest() {
//        val patientId = AppIdUtil.nextId()
//        val currentDate = LocalDate.now()
//        val oldStartDate = currentDate.minusDays(SEVEN_DAY).plusDays(2)
//        val oldEndDate = oldStartDate.plusDays(SEVEN_DAY)
//
//        //准备旧数据：添加逻辑（切换使用）
//        this.addHypertensionByServicePackage(oldStartDate, patientId)
//
//        //查询旧数据（方案和计划信息）
//        val oldManageSchemeAndPlanInfo = this.getManageSchemeAndPlanInfo(patientId)
//        val manageStage = oldManageSchemeAndPlanInfo.first.knManagementStage
//            ?: Assertions.fail(AppSpringUtil.getMessage("health-manage-scheme.stage-is-null"))
//
//        val oldJobId = oldManageSchemeAndPlanInfo.first.knJobId
//            ?: Assertions.fail(AppSpringUtil.getMessage("health-manage-scheme.stage-is-null"))
//        val oldPlanList = oldManageSchemeAndPlanInfo.second
//
//        // 旧答题结果
//        val oldManageDetailExaminationAdapter = examinationService.queryCurrentSchemeExaminationAdapterList(
//            ExaminationService.QueryCurrentSchemeExaminationAdapterListParam(
//                knPatientId = patientId,
//                knHealthManageSchemeId = oldManageSchemeAndPlanInfo.first.knId,
//                knExaminationPaperCodeSet = HypertensionJob.HYPERTENSION_EXAMINATION_CODE_LIST
//            )
//        )
//
//        //需校验的参数（频率，远程频率id）
//        val frequencyHealthAllParamList = mutableListOf<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>()
//        val frequencyIdMap = mutableMapOf<HealthPlanType, List<BigInteger>>()
//
//        //方案相关的模拟
//        val newJobId = this.mockHypertensionManageSchemeData(patientId, oldManageSchemeAndPlanInfo)
//
//        //阶段报告相关的模拟
//        this.mockHypertensionStageReportData(patientId, oldStartDate, oldEndDate, oldPlanList)
//
//        //这里根据血糖监测次数和血糖值的模拟数据，确定是否达标，确定下一阶段
//        val isStandard = false
//        val nextStage = hypertensionJob.getNextStage(ManageStage.valueOf(manageStage), isStandard)
//
//        //个性化计划的模拟
//        this.mockHypertensionPersonalPlanData(
//            startDate = oldEndDate,
//            patientId = patientId,
//            managementStage = nextStage,
//            isStandard = isStandard,
//            frequencyHealthAllParams = frequencyHealthAllParamList,
//            frequencyIdMap = frequencyIdMap
//        )
//
//
//        //job回调执行
//        hypertensionJob.hypertensionHealthManageJobHandler()
//
//        //方案和个性化计划校验
//        this.hypertensionManageSchemeAndPersonalPlanVerify(
//            patientId,
//            oldEndDate,
//            nextStage,
//            frequencyHealthAllParamList,
//            frequencyIdMap,
//            1,
//            3,
//            oldJobId,
//            newJobId
//        )
//
//        //通用计划校验
//        this.hypertensionCommonPlanVerify(patientId, oldPlanList)
//        // 高血压答题结果校验
//        this.hypertensionAdapterVerify(patientId, oldManageDetailExaminationAdapter)
    }

    /**
     * 切方案（高血压方案->糖尿病患者方案），生成对应类型的健康方案
     */
    @Test
    fun switchAddSchemeByDiseaseEvaluate() {
        val patientId = AppIdUtil.nextId()
        val startDate = LocalDate.now().minusDays(SEVEN_DAY).plusDays(2)
        // 新建高血压方案
        addHypertensionByServicePackage(startDate, patientId)
        //停止方案的定时任务的模拟和重置健康计划的mock对象
        this.pauseSchemeJobMock(patientId)
        // 新建糖尿病方案
        addDiabetesByServicePackage(startDate, patientId)
        //停止方案的定时任务的模拟和重置健康计划的mock对象
        this.pauseSchemeJobMock(patientId)
    }

    /**
     * 刪除健康管理方案，健康计划，定时任务
     */
    @Test
    fun removeHealthManageSchemeByPatientId() {
        val patientId = AppIdUtil.nextId()
        val startDate = LocalDate.now().minusDays(SEVEN_DAY).plusDays(2)
        // 新建高血压方案
        addHypertensionByServicePackage(startDate, patientId)
        //查询新建的高血压方案及计划信息
        val hypertensionManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        val hypertensionSchemeId = hypertensionManageSchemeAndPlanInfo.first.knId
        //停止高血压方案
        this.pauseSchemeJobMock(patientId)
        manageApi.pause(patientId)
        //查询患者已经停止的健康方案
        val deleteHypertensionManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //校验方案是否停止
        removeManageSchemeVerify(
            hypertensionManageSchemeAndPlanInfo.first,
            deleteHypertensionManageSchemeAndPlanInfo.first
        )
        //校验高血压方案中的计划是否停止
        removeHypertensionPlanVerify(
            hypertensionSchemeId,
            hypertensionManageSchemeAndPlanInfo.second,
            deleteHypertensionManageSchemeAndPlanInfo.second
        )

        // 新建糖尿病方案
        addDiabetesByServicePackage(startDate, patientId)
        //查询新建的高血压方案及计划信息
        val diabetesManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        val diabetesSchemeId = diabetesManageSchemeAndPlanInfo.first.knId
        //停止糖尿病方案
        this.pauseSchemeJobMock(patientId)
        manageApi.pause(patientId)
        //查询患者已经停止的健康方案
        val deleteDiabetesManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //校验方案是否停止
        removeManageSchemeVerify(diabetesManageSchemeAndPlanInfo.first, deleteDiabetesManageSchemeAndPlanInfo.first)
        //校验糖尿病方案中的计划是否停止
        removeDiabetesPlanVerify(
            diabetesSchemeId,
            diabetesManageSchemeAndPlanInfo.second,
            deleteDiabetesManageSchemeAndPlanInfo.second
        )
    }

    /**
     * 购买服务包后创建健康管理方案的测试方法（脑卒中方案）
     */
    @Test
    fun addCerebralStrokeHealthManageSchemeByServicePackageTest() {
        val patientId = AppIdUtil.nextId()
        val startDate = LocalDate.now().plusDays(1)
        addCerebralStrokeSchemeByServicePackageTest(startDate, patientId)
    }

    private fun addCerebralStrokeSchemeByServicePackageTest(startDate: LocalDate, patientId: BigInteger) {
        //订阅的服务包（脑卒中包）
        val request = AddManageByServicePackageRequest(
            patientId = patientId,
            startDate = startDate,
            serviceCodeList = listOf(CEREBRAL_STROKE_SERVICE_CODE)
        )
        //服务包模拟
        Mockito.doReturn(
            listOf(
                HealthService(
                    healthServiceId = AppIdUtil.nextId(),
                    healthServiceCode = CEREBRAL_STROKE_SERVICE_CODE,
                    healthServiceName = "脑卒中服务包",
                    isSigned = true,
                    activationDate = LocalDateTime.now()
                )
            )
        ).`when`(servicePackageClient).getHealthServicePatient(patientId)
        //需校验的参数（频率，远程频率id）
        val frequencyHealthAllParamList = mutableListOf<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>()
        val frequencyIdMap = mutableMapOf<HealthPlanType, List<BigInteger>>()

        //方案相关模拟
        this.mockCerebralStrokeManageSchemeData(patientId)

        //通用计划模拟
        this.mockCerebralStrokeCommonPlanData(patientId, startDate, frequencyHealthAllParamList, frequencyIdMap)

        //个性化计划模拟
        this.mockCerebralStrokePersonalPlanData(
            startDate = startDate,
            patientId = patientId,
            frequencyHealthAllParams = frequencyHealthAllParamList,
            frequencyIdMap = frequencyIdMap
        )

        //创建方案接口执行
        manageApi.addHealthSchemeManageByServicePackage(request)

        //方案和个性化计划校验
        this.cerebralStrokeSchemeAndPersonalPlanVerify(
            patientId = patientId,
            startDate = startDate,
            frequencyHealthAllParams = frequencyHealthAllParamList,
            frequencyIdMap = frequencyIdMap,
            remoteInvokeCount = 2
        )

        //通用计划校验
        this.cerebralStrokeCommonPlanVerify(patientId, frequencyHealthAllParamList, frequencyIdMap)
    }

        /**
     * mock跟方案相关的job
     */
    private fun pauseSchemeJobMock(patientId: BigInteger) {
        //重置健康计划mock对象
        Mockito.reset(healthPlanClient)
        //健康方案的停止定时任务模拟
        val manageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        // val oldJobId = manageSchemeAndPlanInfo.first.knJobId
        //    ?: Assertions.fail(AppSpringUtil.getMessage("health-manage-scheme.job-id-is-null"))
        // TODO: Mockito.`when`(jobInfoApi.pause(oldJobId)).thenReturn(ReturnT("1"))
    }

/**
     * mock跟计划相关的job
     */
    private fun pausePlanJobMock(patientId: BigInteger) {
        //重置健康计划mock对象
        Mockito.reset(healthPlanClient)
        //健康计划的停止定时任务模拟
        val manageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        val oldJobId = manageSchemeAndPlanInfo.second.first { it.knJobId != null }.knJobId
            ?: Assertions.fail(AppSpringUtil.getMessage("health-manage-scheme.job-id-is-null"))
        // TODO: Mockito.`when`(jobInfoApi.pause(oldJobId)).thenReturn(ReturnT("1"))
    }

    private fun removeDiabetesPlanVerify(
        knId: BigInteger,
        planList: List<HsHsmHealthPlan>,
        deletePlanList: List<HsHsmHealthPlan>
    ) {

        var plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name && it.knSchemeManagementId == knId }
        var deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name && it.knSchemeManagementId == knId }
        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.DIET_PLAN.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.DIET_PLAN.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_DIABETES.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_DIABETES.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.OFFLINE_DIABETES.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.OFFLINE_DIABETES.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.FASTING_BLOOD_GLUCOSE.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.FASTING_BLOOD_GLUCOSE.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.ONLINE_DIABETES.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.ONLINE_DIABETES.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.DIABETES_BEHAVIOR_VISIT.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.DIABETES_BEHAVIOR_VISIT.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)
    }

    private fun removeHypertensionPlanVerify(
        knId: BigInteger,
        planList: List<HsHsmHealthPlan>,
        deletePlanList: List<HsHsmHealthPlan>
    ) {
        var plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name && it.knSchemeManagementId == knId }
        var deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name && it.knSchemeManagementId == knId }
        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.DIET_PLAN.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.DIET_PLAN.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.OFFLINE_HYPERTENSION.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.OFFLINE_HYPERTENSION.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.BLOOD_PRESSURE_MEASUREMENT.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.BLOOD_PRESSURE_MEASUREMENT.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.HYPERTENSION_VISIT.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.HYPERTENSION_VISIT.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)

        plan =
            planList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.BEHAVIOR_VISIT.name && it.knSchemeManagementId == knId }
        deletePlan =
            deletePlanList.first { it.knPlanType == com.bjknrt.health.scheme.vo.HealthPlanType.BEHAVIOR_VISIT.name && it.knSchemeManagementId == knId }

        Assertions.assertEquals(true, deletePlan.isDel)
        Assertions.assertEquals(plan.knStartDate, deletePlan.knStartDate)
        Assertions.assertEquals(plan.knEndDate, deletePlan.knEndDate)
        Assertions.assertEquals(plan.knForeignPlanId, deletePlan.knForeignPlanId)
        Assertions.assertEquals(plan.knForeignPlanFrequencyIds, deletePlan.knForeignPlanFrequencyIds)
    }

    private fun removeManageSchemeVerify(
        manageSchemeAndPlanInfo: HsHealthSchemeManagementInfo,
        deleteManageSchemeAndPlanInfo: HsHealthSchemeManagementInfo,
    ) {
        Assertions.assertEquals(true, deleteManageSchemeAndPlanInfo.isDel)
        Assertions.assertEquals(
            manageSchemeAndPlanInfo.knStartDate,
            deleteManageSchemeAndPlanInfo.knStartDate
        )
        Assertions.assertEquals(
            manageSchemeAndPlanInfo.knEndDate,
            deleteManageSchemeAndPlanInfo.knEndDate
        )
        Assertions.assertEquals(
            manageSchemeAndPlanInfo.knPatientId,
            deleteManageSchemeAndPlanInfo.knPatientId
        )
        Assertions.assertEquals(
            manageSchemeAndPlanInfo.knHealthManageType,
            deleteManageSchemeAndPlanInfo.knHealthManageType
        )
        Assertions.assertEquals(
            manageSchemeAndPlanInfo.knManagementStage,
            deleteManageSchemeAndPlanInfo.knManagementStage
        )
        Assertions.assertEquals(
            manageSchemeAndPlanInfo.knDiseaseExistsTag,
            deleteManageSchemeAndPlanInfo.knDiseaseExistsTag
        )
        Assertions.assertEquals(
            manageSchemeAndPlanInfo.knJobId,
            deleteManageSchemeAndPlanInfo.knJobId
        )
    }

    /**
     * 糖尿病方案的模拟和校验方法抽取
     */
    private fun mockDiabetesManageSchemeData(patientId: BigInteger): Int {
        //定时任务模拟
        val newJobId = ThreadLocalRandom.current().nextInt()
        //TODO: Mockito.`when`(jobInfoApi.add(any())).thenReturn(ReturnT(newJobId.toString()))

        //患者的五病标签模拟（仅有糖尿病标签）
        this.mockDiabetesPatient(patientId)

        return newJobId
    }

    private fun mockDiabetesManageSchemeData(
        patientId: BigInteger,
        manageSchemeAndPlanInfo: Pair<HsHealthSchemeManagementInfo, List<HsHsmHealthPlan>>
    ): Int {
        //添加定时任务的模拟
        val newJobId = ThreadLocalRandom.current().nextInt()

        //TODO: 移除定时任务的模拟
        val oldJobId = manageSchemeAndPlanInfo.first.knJobId
            ?: Assertions.fail(AppSpringUtil.getMessage("health-manage-scheme.job-id-is-null"))


        // TODO: JobContext中的方案id和jobId的模拟

        //获取患者信息的模拟（仅有糖尿病标签）
        this.mockDiabetesPatient(patientId)
        return newJobId
    }

    private fun mockDiabetesStageReportData(
        patientId: BigInteger,
        oldStartDate: LocalDate,
        oldEndDate: LocalDate,
        oldPlanList: List<HsHsmHealthPlan>
    ) {
        //获取方案周期内指标集合信息的模拟
        val now = LocalDateTime.now()
        val bloodSugarResultList = listOf(
            BloodSugarResult(
                knId = AppIdUtil.nextId(),
                knCreatedAt = now,
                knMeasureAt = now,
                knFastingBloodSandalwood = BigDecimal.valueOf(1.0),
                knRandomBloodSugar = BigDecimal.valueOf(2.0),
                knAfterMealBloodSugar = BigDecimal.valueOf(3.0),
                knBeforeSleepBloodSugar = BigDecimal.valueOf(4.0),
                knBeforeLunchBloodSugar = BigDecimal.valueOf(5.0),
                knBeforeDinnerBloodSugar = BigDecimal.valueOf(6.0),
                knAfterLunchBloodSugar = BigDecimal.valueOf(7.0),
                knAfterDinnerBloodSugar = BigDecimal.valueOf(8.0)
            )
        )
        Mockito.doReturn(
            PatientIndicatorListResult(
                bloodSugarListResult = bloodSugarResultList,
                bodyHeightListResult = listOf(
                    BodyHeightResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knBodyHeight = BigDecimal.valueOf(180),
                        knMeasureAt = now
                    )
                ),
                bodyWeightListResult = listOf(
                    BodyWeightResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knBodyWeight = BigDecimal.valueOf(70),
                        knMeasureAt = now
                    )
                ),
                knWaistlineListResult = listOf(
                    WaistLineResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knWaistline = BigDecimal.valueOf(50),
                        knMeasureAt = now
                    )
                ),
                bloodPressureListResult = listOf(
                    BloodPressureResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knMeasureAt = now,
                        knSystolicBloodPressure = BigDecimal.valueOf(50),
                        knDiastolicBloodPressure = BigDecimal.valueOf(50),
                    )
                ),
                bloodLipidsListResult = listOf(
                    BloodLipidsResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knMeasureAt = now
                    )
                ),
                smokeListResult = listOf(
                    SmokeResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knMeasureAt = now,
                        knNum = 5
                    )
                ),
            )
        ).`when`(indicatorClient).selectAnyIndicatorListForDpm(
            FindListParam(
                startTime = oldStartDate.atStartOfDay(),
                endTime = oldEndDate.atStartOfDay(),
                patientId = patientId
            )
        )

        //获取空腹血糖打卡次数的模拟
        val fastingBloodSugarPlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.FASTING_BLOOD_GLUCOSE.name }

        Mockito.doReturn(2).`when`(healthPlanClient).dateTimeGetClockIn(
            DateTimeGetClockInParams(
                healthPlanId = fastingBloodSugarPlan.knForeignPlanId,
                startTime = oldStartDate.atStartOfDay(),
                endTime = oldEndDate.atStartOfDay().plusHours(-1),
            )
        )

        //获取餐前血糖打卡次数的模拟
        val beforeMealBloodSugarPlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE.name }
        Mockito.doReturn(3).`when`(healthPlanClient).dateTimeGetClockIn(
            DateTimeGetClockInParams(
                healthPlanId = beforeMealBloodSugarPlan.knForeignPlanId,
                startTime = oldStartDate.atStartOfDay(),
                endTime = oldEndDate.atStartOfDay().plusHours(-1),
            )
        )

        //获取餐后2h血糖打卡次数的模拟
        val afterMealBloodSugarPlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE.name }
        Mockito.doReturn(4).`when`(healthPlanClient).dateTimeGetClockIn(
            DateTimeGetClockInParams(
                healthPlanId = afterMealBloodSugarPlan.knForeignPlanId,
                startTime = oldStartDate.atStartOfDay(),
                endTime = oldEndDate.atStartOfDay().plusHours(-1),
            )
        )

        //获血糖取指标值集合的模拟
        Mockito.doReturn(bloodSugarResultList).`when`(indicatorClient).bloodSugarList(
            FindListParam(
                oldStartDate.atStartOfDay(),
                oldEndDate.atStartOfDay(),
                patientId
            )
        )

        //阶段性报告-获取行为习惯评估记录的模拟
        Mockito.doReturn(
            listOf(
                LastAnswerRecord(
                    id = BigInteger.valueOf(1),
                    answerBy = patientId,
                    resultsTag = "",
                    resultsMsg = "",
                    totalScore = BigDecimal.TEN,
                    createdBy = BigInteger.ONE,
                    createdAt = LocalDateTime.now(),
                )
            )
        ).`when`(questionsAnswerClient).getLastAnswerRecordList(
            LastAnswerRecordListRequest(
                examinationPaperCode = DiabetesReportServiceImpl.DIABETES_BEHAVIOR_EXAMINATION_PAPER_CODE,
                answerBy = patientId,
                needNum = 1,
                startDate = oldStartDate.atStartOfDay(),
                endDate = oldEndDate.atStartOfDay()
            )
        )
    }

    /**
     * 糖尿病个性化模拟
     */
    private fun mockDiabetesPersonalPlanData(
        startDate: LocalDate,
        patientId: BigInteger,
        managementStage: ManageStage,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>,
        judgeStandard: Boolean = false
    ) {
        val endDate = getEndDate(managementStage, startDate).atStartOfDay()
        //糖尿病包的空腹血糖测量计划模拟
        val healthPlans = mutableListOf(
            HealthPlanDTO(
                name = DIABETES_FASTING_BLOOD_SUGAR_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.FASTING_BLOOD_GLUCOSE,
                desc = DIABETES_FASTING_BLOOD_SUGAR_PLAN_DESC_MSG_MAP[managementStage],
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = endDate,
                frequencys = DIABETES_FASTING_BLOOD_SUGAR_FREQUENCY_MAP[managementStage]
            ),
            //糖尿病包的餐前血糖测量计划模拟
            HealthPlanDTO(
                name = DIABETES_BEFORE_MEAL_BLOOD_SUGAR_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE,
                desc = DIABETES_BEFORE_MEAL_BLOOD_SUGAR_PLAN_DESC_MSG_MAP[managementStage],
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = endDate,
                frequencys = DIABETES_BEFORE_LUNCH_OR_DINNER_BLOOD_SUGAR_FREQUENCY_MAP[managementStage]
            ),
            // 糖尿病包的餐后2h血糖测量计划模拟
            HealthPlanDTO(
                name = DIABETES_AFTER_MEAL_BLOOD_SUGAR_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE,
                desc = DIABETES_AFTER_MEAL_BLOOD_SUGAR_PLAN_DESC_MSG_MAP[managementStage],
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = endDate,
                frequencys = DIABETES_AFTER_MEAL_ANY_BLOOD_SUGAR_FREQUENCY_MAP[managementStage]
            )
        )

        //糖尿病包的糖尿病随访模拟
        val endDateTime = getEndDate(managementStage, startDate).atStartOfDay()
        val diabetesVisitLastDay =
            if (judgeStandard) HealthManageDiabetesServiceImpl.DIABETES_VISIT_LAST_DAY_3 else HealthManageDiabetesServiceImpl.DIABETES_VISIT_LAST_DAY_2
        val intervalDay =
            if (judgeStandard) HealthManageDiabetesServiceImpl.DIABETES_VISIT_IS_STANDARD else HealthManageDiabetesServiceImpl.DIABETES_VISIT_NOT_STANDARD
        val diabetesVisitFrequencies =
            if (judgeStandard) HealthManageDiabetesServiceImpl.DIABETES_VISIT_PLAN_FREQUENCIES_3_DAYS_1_SEQUENCE else HealthManageDiabetesServiceImpl.DIABETES_VISIT_PLAN_FREQUENCIES_2_DAYS_1_SEQUENCE
        var tempStartDateTime = startDate.atStartOfDay()

        val list = mutableListOf<HealthPlanDTO>()
        do {
            val tempEndDateTime = tempStartDateTime.plusDays(intervalDay)
            tempStartDateTime = tempEndDateTime.plusDays(diabetesVisitLastDay)
            val startFormat = LocalDateTimeUtil.format(
                tempStartDateTime,
                HealthManageDiabetesServiceImpl.DIABETES_MONTH_DAY_FORMAT
            )
            val endFormat = LocalDateTimeUtil.format(
                tempEndDateTime.plusDays(-1),
                HealthManageDiabetesServiceImpl.DIABETES_MONTH_DAY_FORMAT
            )

            val healthParams = HealthPlanDTO(
                name = HealthManageDiabetesServiceImpl.DIABETES_VISIT_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.ONLINE_DIABETES,
                desc = "${startFormat}-${endFormat},完成${HealthManageDiabetesServiceImpl.DIABETES_VISIT_FREQUENCY_NUM}次",
                cycleStartTime = tempStartDateTime,
                cycleEndTime = tempEndDateTime,
                frequencys = diabetesVisitFrequencies
            )
            tempStartDateTime = tempEndDateTime
            list.add(healthParams)
        } while (tempStartDateTime < endDateTime)
        healthPlans.addAll(list)

        //糖尿病包的行为习惯随访模拟
        val behaviorTempStartDateTime =
            endDateTime.plusDays(HealthManageDiabetesServiceImpl.DIABETES_BEHAVIOR_VISIT_LAST_DAY)
        val startFormat = LocalDateTimeUtil.format(
            behaviorTempStartDateTime,
            HealthManageDiabetesServiceImpl.DIABETES_MONTH_DAY_FORMAT
        )
        val endFormat = LocalDateTimeUtil.format(
            endDateTime.plusDays(-1),
            HealthManageDiabetesServiceImpl.DIABETES_MONTH_DAY_FORMAT
        )
        val healthParams = HealthPlanDTO(
            name = HealthManageDiabetesServiceImpl.DIABETES_BEHAVIOR_VISIT_PLAN_NAME,
            type = com.bjknrt.health.scheme.vo.HealthPlanType.DIABETES_BEHAVIOR_VISIT,
            desc = "${startFormat}-${endFormat},完成${HealthManageDiabetesServiceImpl.DIABETES_BEHAVIOR_VISIT_PLAN_FREQUENCY_NUM}次",
            cycleStartTime = behaviorTempStartDateTime,
            cycleEndTime = endDateTime,
            frequencys = HealthManageDiabetesServiceImpl.DIABETES_BEHAVIOR_VISIT_PLAN_FREQUENCIES
        )
        healthPlans.addAll(listOf(healthParams))
        this.mockAddRemindPlan(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = listOf(),
            frequencyHealthAllParams = frequencyHealthAllParams,
            frequencyIdMap = frequencyIdMap
        )
    }

    /**
     * 糖尿病通用计划模拟
     */
    private fun mockDiabetesCommonPlanData(
        patientId: BigInteger,
        startDate: LocalDate,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {
        val healthPlans = listOf(
            //科普计划模拟
            HealthPlanDTO(
                name = "每日一科普",
                type = com.bjknrt.health.scheme.vo.HealthPlanType.SCIENCE_POPULARIZATION_PLAN,
                subName = null,
                desc = "每日1篇",
                externalKey = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                group = SCIENCE_POPULARIZATION,
                frequencys = SCIENCE_POPULARIZATION_PLAN_FREQUENCIES
            ),
            //饮食计划模拟（糖尿病）
            HealthPlanDTO(
                name = DIET_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.DIET_PLAN,
                subName = null,
                desc = DIET_PLAN_DESC,
                externalKey = DietPlanTypeEnum.DIABETES.name,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                group = DIET_PLAN_GROUP,
                frequencys = DIET_PLAN_FREQUENCIES
            ),
            //未进行运动评估的运动健康计划模拟
            HealthPlanDTO(
                name = NOT_EVALUATE_SPORT_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = NOT_EVALUATE_SPORT_PLAN_FREQUENCIES
            ),
            // 未进行用药评估(糖尿病模拟)
            HealthPlanDTO(
                name = NOT_EVALUATE_DRUG_PROGRAM_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = NOT_EVALUATE_DRUG_PROGRAM_PLAN_FREQUENCIES
            ),
            //未进行饮食评估的运动健康计划模拟
            HealthPlanDTO(
                name = NOT_DIET_EVALUATE_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.DIET_NOT_EVALUATED_DIABETES,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = NOT_DIET_EVALUATE_FREQUENCIES
            ),
            // 冠心病线下随访
            HealthPlanDTO(
                name = OFFLINE_DIABETES_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.OFFLINE_DIABETES,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = LocalDate.of(LocalDate.now().year, 1, 1).atStartOfDay(),
                cycleEndTime = null,
                displayTime = startDate.atStartOfDay(),
                clockDisplay = false,
                frequencys = OFFLINE_VISIT_FREQUENCY
            )
        )
        this.mockDeleteAndAddRemindPlan(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = listOf(),
            frequencyHealthAllParams = frequencyHealthAllParams,
            frequencyIdMap = frequencyIdMap
        )
    }

    /**
     * 模拟答题结果
     */
    private fun mockDiabetesAdapter(
        patientId: BigInteger
    ) {
        // 糖尿病用药评估结果
        val drugExaminationPaperOptionList = mutableListOf<ExaminationPaperOption>()
        drugExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(340001),
                knOptionId = BigInteger.valueOf(34000101),
                knOptionLabel = "A",
                knMessage = "遵医嘱服药或使用胰岛素。"
            )
        )
        examinationApi.syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = patientId,
                knExaminationPaperCode = ExaminationCodeEnum.DIABETES_DRUG_PROGRAM.name,
                knExaminationPaperOptionList = drugExaminationPaperOptionList
            )
        )

        // 糖尿病饮食评估结果
        val dietExaminationPaperOptionList = mutableListOf<ExaminationPaperOption>()
        dietExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(360001),
                knOptionId = BigInteger.valueOf(36000101),
                knOptionLabel = null,
                knMessage = "A1"
            ),
        )
        dietExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(360002),
                knOptionId = BigInteger.valueOf(36000201),
                knOptionLabel = null,
                knMessage = "A2"
            ),
        )
        dietExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(360003),
                knOptionId = BigInteger.valueOf(36000301),
                knOptionLabel = null,
                knMessage = "A3"
            ),
        )

        examinationApi.syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = patientId,
                knExaminationPaperCode = ExaminationCodeEnum.DIET_EVALUATE_DIABETES.name,
                knExaminationPaperOptionList = dietExaminationPaperOptionList
            )
        )

        // 糖尿病运动禁忌结果
        val sportExaminationPaperOptionList = mutableListOf<ExaminationPaperOption>()
        sportExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(150001),
                knOptionId = BigInteger.valueOf(15000103),
                knOptionLabel = "每分钟的心跳次数超过130次"
            )
        )
        examinationApi.syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = patientId,
                knExaminationPaperCode = ExaminationCodeEnum.SPORT.name,
                knExaminationPaperOptionList = sportExaminationPaperOptionList
            )
        )
    }

    /**
     * 答题结果同步结果校验
     */
    private fun diabetesAdapterVerify(
        patientId: BigInteger,
        oldManageDetailExaminationAdapter: List<HsManageDetailExaminationAdapter>
    ) {

        //查询新方案和计划信息
        val newManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)

        // 查询新的问卷结果
        val manageDetailExaminationAdapter = examinationService.queryCurrentSchemeExaminationAdapterList(
            ExaminationService.QueryCurrentSchemeExaminationAdapterListParam(
                knPatientId = patientId,
                knHealthManageSchemeId = newManageSchemeAndPlanInfo.first.knId,
                knExaminationPaperCodeSet = DIABETES_EXAMINATION_CODE_LIST
            )
        )
        // 校验用药评估旧的结果和新的是否一致
        val oldDrugAdapterList = oldManageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.DIABETES_DRUG_PROGRAM.name
        }
        val newDrugAdapterList = manageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.DIABETES_DRUG_PROGRAM.name
        }

        Assertions.assertEquals(oldDrugAdapterList.size, newDrugAdapterList.size)
        Assertions.assertEquals(oldDrugAdapterList[0].knAnswerRecordId, newDrugAdapterList[0].knAnswerRecordId)
        Assertions.assertEquals(oldDrugAdapterList[0].knAnswerResultId, newDrugAdapterList[0].knAnswerResultId)
        Assertions.assertEquals(oldDrugAdapterList[0].knQuestionsId, newDrugAdapterList[0].knQuestionsId)
        Assertions.assertEquals(oldDrugAdapterList[0].knOptionId, newDrugAdapterList[0].knOptionId)
        Assertions.assertEquals(oldDrugAdapterList[0].knMessage, newDrugAdapterList[0].knMessage)

        // 校验饮食评估旧的结果和新的是否一致
        val oldDietAdapterList = oldManageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.DIET_EVALUATE_DIABETES.name
        }
        val newDietAdapterList = manageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.DIET_EVALUATE_DIABETES.name
        }

        Assertions.assertEquals(oldDietAdapterList.size, newDietAdapterList.size)
        Assertions.assertEquals(oldDietAdapterList[0].knAnswerRecordId, newDietAdapterList[0].knAnswerRecordId)
        Assertions.assertEquals(oldDietAdapterList[0].knAnswerResultId, newDietAdapterList[0].knAnswerResultId)
        Assertions.assertEquals(oldDietAdapterList[0].knQuestionsId, newDietAdapterList[0].knQuestionsId)
        Assertions.assertEquals(oldDietAdapterList[0].knOptionId, newDietAdapterList[0].knOptionId)
        Assertions.assertEquals(oldDietAdapterList[0].knMessage, newDietAdapterList[0].knMessage)

        Assertions.assertEquals(oldDietAdapterList[1].knAnswerRecordId, newDietAdapterList[1].knAnswerRecordId)
        Assertions.assertEquals(oldDietAdapterList[1].knAnswerResultId, newDietAdapterList[1].knAnswerResultId)
        Assertions.assertEquals(oldDietAdapterList[1].knQuestionsId, newDietAdapterList[1].knQuestionsId)
        Assertions.assertEquals(oldDietAdapterList[1].knOptionId, newDietAdapterList[1].knOptionId)
        Assertions.assertEquals(oldDietAdapterList[1].knMessage, newDietAdapterList[1].knMessage)

        Assertions.assertEquals(oldDietAdapterList[2].knAnswerRecordId, newDietAdapterList[2].knAnswerRecordId)
        Assertions.assertEquals(oldDietAdapterList[2].knAnswerResultId, newDietAdapterList[2].knAnswerResultId)
        Assertions.assertEquals(oldDietAdapterList[2].knQuestionsId, newDietAdapterList[2].knQuestionsId)
        Assertions.assertEquals(oldDietAdapterList[2].knOptionId, newDietAdapterList[2].knOptionId)
        Assertions.assertEquals(oldDietAdapterList[2].knMessage, newDietAdapterList[2].knMessage)

        // 校验用药评估旧的结果和新的是否一致
        val oldSportAdapterList = oldManageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.SPORT.name
        }
        val newSportAdapterList = manageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.SPORT.name
        }

        Assertions.assertEquals(oldSportAdapterList.size, newSportAdapterList.size)
        Assertions.assertEquals(oldSportAdapterList[0].knAnswerRecordId, newSportAdapterList[0].knAnswerRecordId)
        Assertions.assertEquals(oldSportAdapterList[0].knAnswerResultId, newSportAdapterList[0].knAnswerResultId)
        Assertions.assertEquals(oldSportAdapterList[0].knQuestionsId, newSportAdapterList[0].knQuestionsId)
        Assertions.assertEquals(oldSportAdapterList[0].knOptionId, newSportAdapterList[0].knOptionId)
        Assertions.assertEquals(oldSportAdapterList[0].knOptionLabel, newSportAdapterList[0].knOptionLabel)
    }

    private fun diabetesManageSchemeAndPersonalPlanVerify(
        patientId: BigInteger,
        startDate: LocalDate,
        stage: ManageStage,
        frequencyHealthAllParams: List<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>,
        remoteInvokeCount: Int,
        onlyAddRemoteInvokeCount: Int,
        oldJobId: Int?,
        newJobId: Int?
    ) {
        //查询新方案和计划信息
        val newManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //阶段
        Assertions.assertEquals(stage.name, newManageSchemeAndPlanInfo.first.knManagementStage)
        //开始时间
        Assertions.assertEquals(startDate, newManageSchemeAndPlanInfo.first.knStartDate)
        //结束时间
        Assertions.assertEquals(getEndDate(stage, startDate), newManageSchemeAndPlanInfo.first.knEndDate)
        //类型
        Assertions.assertEquals(HealthManageType.DIABETES.name, newManageSchemeAndPlanInfo.first.knHealthManageType)
        //患者标签
        Assertions.assertEquals("", newManageSchemeAndPlanInfo.first.knDiseaseExistsTag)
        //TODO: 暂且不校验 任务id
        // Assertions.assertEquals(newJobId, newManageSchemeAndPlanInfo.first.knJobId)

        //糖尿病包的空腹血糖测量计划
        val frequencyHealthAllParam5 =
            frequencyHealthAllParams.first { it.healthPlans[0].type == HealthPlanType.FASTING_BLOOD_GLUCOSE }
        var plan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.FASTING_BLOOD_GLUCOSE.name }
        Assertions.assertEquals(frequencyHealthAllParam5.healthPlans[0].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam5.healthPlans[0].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam5.healthPlans[0].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam5.healthPlans[0].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )

        //糖尿病包的餐前血糖测量计划
        val frequencyHealthAllParam6 =
            frequencyHealthAllParams.first { it.healthPlans[1].type == HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE }
        plan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.BEFORE_MEAL_BLOOD_GLUCOSE.name }
        Assertions.assertEquals(frequencyHealthAllParam6.healthPlans[1].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam6.healthPlans[1].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam6.healthPlans[1].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam6.healthPlans[1].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )
        //糖尿病包的餐后2h血糖测量计划
        val frequencyHealthAllParam7 =
            frequencyHealthAllParams.first { it.healthPlans[2].type == HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE }
        plan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.MEAL_TWO_HOUR_RANDOM_BLOOD_GLUCOSE.name }
        Assertions.assertEquals(frequencyHealthAllParam7.healthPlans[2].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam7.healthPlans[2].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam7.healthPlans[2].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam7.healthPlans[2].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )
        //糖尿病包的糖尿病随访
        val frequencyHealthAllParam8 =
            frequencyHealthAllParams.first { it.healthPlans[3].type == HealthPlanType.ONLINE_DIABETES }
        val onlineDiabetesPlanList =
            newManageSchemeAndPlanInfo.second.filter { it.knPlanType == HealthPlanType.ONLINE_DIABETES.name }

        println(frequencyHealthAllParam8.healthPlans)
        println(onlineDiabetesPlanList.size)
//        Assertions.assertEquals(frequencyHealthAllParam8.healthPlans.size, onlineDiabetesPlanList.size)
        Assertions.assertNotNull(
            onlineDiabetesPlanList[0].knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam8.healthPlans[3].type],
            getFrequencyIds(onlineDiabetesPlanList[0].knForeignPlanFrequencyIds)
        )
        //糖尿病包的行为习惯随访
        val frequencyHealthAllParam9 =
            frequencyHealthAllParams.first { it.healthPlans[4].type == HealthPlanType.DIABETES_BEHAVIOR_VISIT }
        plan = newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIABETES_BEHAVIOR_VISIT.name }
        Assertions.assertEquals(frequencyHealthAllParam9.healthPlans[4].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam9.healthPlans[4].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam9.healthPlans[4].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam9.healthPlans[4].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )
        //调用远程方法的参数拦截验证
        val frequencyCaptor: ArgumentCaptor<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam> =
            ArgumentCaptor.forClass(com.bjknrt.medication.remind.vo.FrequencyHealthAllParam::class.java)
        Mockito.verify(healthPlanClient, times(remoteInvokeCount)).upsertTypeFrequencyHealth(capture(frequencyCaptor))
        Assertions.assertEquals(remoteInvokeCount, frequencyCaptor.allValues.size)

        val onlyAddFrequencyCaptor: ArgumentCaptor<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam> =
            ArgumentCaptor.forClass(com.bjknrt.medication.remind.vo.FrequencyHealthAllParam::class.java)
        Mockito.verify(healthPlanClient, times(onlyAddRemoteInvokeCount)).batchAddHealthPlan(capture(onlyAddFrequencyCaptor))
        Assertions.assertEquals(onlyAddRemoteInvokeCount, onlyAddFrequencyCaptor.allValues.size)
        //job参数拦截校验
        oldJobId?.let {
            val frequencyCaptor2: ArgumentCaptor<Int> =
                ArgumentCaptor.forClass(Int::class.java)
            // TODO: Mockito.verify(jobInfoApi).pause(capture(frequencyCaptor2))
            Assertions.assertEquals(it, frequencyCaptor2.value)
        }
    }

    private fun diabetesCommonPlanVerify(
        patientId: BigInteger,
        oldPlanList: List<HsHsmHealthPlan>
    ) {
        //查询新方案和计划信息
        val newManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //健康科普计划
        val oldScienceHealthPlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name }
        val sciencePopularPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name }
        Assertions.assertEquals(oldScienceHealthPlan.knStartDate, sciencePopularPlan.knStartDate)
        Assertions.assertEquals(oldScienceHealthPlan.knEndDate, sciencePopularPlan.knEndDate)
        Assertions.assertEquals(oldScienceHealthPlan.knPlanType, sciencePopularPlan.knPlanType)
        Assertions.assertNotNull(
            sciencePopularPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldScienceHealthPlan.knForeignPlanFrequencyIds),
            getFrequencyIds(sciencePopularPlan.knForeignPlanFrequencyIds)
        )
        //饮食计划（糖尿病患者标签）
        val oldDietPlan = oldPlanList.first { it.knPlanType == HealthPlanType.DIET_PLAN.name }
        val dietPlan = newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIET_PLAN.name }
        Assertions.assertEquals(oldDietPlan.knStartDate, dietPlan.knStartDate)
        Assertions.assertEquals(oldDietPlan.knEndDate, dietPlan.knEndDate)
        Assertions.assertEquals(oldDietPlan.knPlanType, dietPlan.knPlanType)
        Assertions.assertNotNull(
            oldDietPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldDietPlan.knForeignPlanFrequencyIds),
            getFrequencyIds(dietPlan.knForeignPlanFrequencyIds)
        )
        //运动计划
        val oldExercisePlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name }
        val exercisePlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name }
        Assertions.assertEquals(oldExercisePlan.knStartDate, exercisePlan.knStartDate)
        Assertions.assertEquals(oldExercisePlan.knEndDate, exercisePlan.knEndDate)
        Assertions.assertEquals(oldExercisePlan.knPlanType, exercisePlan.knPlanType)
        Assertions.assertNotNull(
            exercisePlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldExercisePlan.knForeignPlanFrequencyIds),
            getFrequencyIds(exercisePlan.knForeignPlanFrequencyIds)
        )
        //饮食评估计划
        val oldDietEvaluatePlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_DIABETES.name }
        val dietEvaluatePlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_DIABETES.name }
        Assertions.assertEquals(oldDietEvaluatePlan.knStartDate, dietEvaluatePlan.knStartDate)
        Assertions.assertEquals(oldDietEvaluatePlan.knEndDate, dietEvaluatePlan.knEndDate)
        Assertions.assertEquals(oldDietEvaluatePlan.knPlanType, dietEvaluatePlan.knPlanType)
        Assertions.assertNotNull(
            dietEvaluatePlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldDietEvaluatePlan.knForeignPlanFrequencyIds),
            getFrequencyIds(dietEvaluatePlan.knForeignPlanFrequencyIds)
        )
        //糖尿病线下随访计划
        val oldOfflineDiabetesPlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.OFFLINE_DIABETES.name }
        val offlineDiabetesPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.OFFLINE_DIABETES.name }
        Assertions.assertEquals(oldOfflineDiabetesPlan.knStartDate, offlineDiabetesPlan.knStartDate)
        Assertions.assertEquals(oldOfflineDiabetesPlan.knEndDate, offlineDiabetesPlan.knEndDate)
        Assertions.assertEquals(oldOfflineDiabetesPlan.knPlanType, offlineDiabetesPlan.knPlanType)
        Assertions.assertNotNull(
            offlineDiabetesPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldOfflineDiabetesPlan.knForeignPlanFrequencyIds),
            getFrequencyIds(offlineDiabetesPlan.knForeignPlanFrequencyIds)
        )

        // 糖尿病未进行用药评估
        val oldDrugProgramPlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED.name }
        val drugProgramDiabetesPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED.name }
        Assertions.assertEquals(oldDrugProgramPlan.knStartDate, drugProgramDiabetesPlan.knStartDate)
        Assertions.assertEquals(oldDrugProgramPlan.knPlanType, drugProgramDiabetesPlan.knPlanType)
        Assertions.assertNotNull(
            drugProgramDiabetesPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldDrugProgramPlan.knForeignPlanFrequencyIds),
            getFrequencyIds(drugProgramDiabetesPlan.knForeignPlanFrequencyIds)
        )
    }

    private fun diabetesCommonPlanVerify(
        patientId: BigInteger,
        frequencyHealthAllParams: List<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {
        //查询新方案和计划信息
        val newManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //健康科普计划
        val frequencyHealthAllParam1 =
            frequencyHealthAllParams.first { it.healthPlans[0].type == HealthPlanType.SCIENCE_POPULARIZATION_PLAN }
        val sciencePopularPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name }
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleStartTime, sciencePopularPlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleEndTime, sciencePopularPlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].type.name, sciencePopularPlan.knPlanType)
        Assertions.assertNotNull(
            sciencePopularPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam1.healthPlans[0].type],
            getFrequencyIds(sciencePopularPlan.knForeignPlanFrequencyIds)
        )

        //饮食计划（仅有糖尿病患者标签）
        val frequencyHealthAllParam2 =
            frequencyHealthAllParams.first { it.healthPlans[1].externalKey == DietPlanTypeEnum.DIABETES.name }
        val dietPlan = newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIET_PLAN.name }
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleStartTime, dietPlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleEndTime, dietPlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].type.name, dietPlan.knPlanType)
        Assertions.assertNotNull(dietPlan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam2.healthPlans[1].type],
            getFrequencyIds(dietPlan.knForeignPlanFrequencyIds)
        )

        //运动计划
        val frequencyHealthAllParam3 =
            frequencyHealthAllParams.first { it.healthPlans[2].type == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED }
        val exercisePlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name }
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleStartTime, exercisePlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleEndTime, exercisePlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].type.name, exercisePlan.knPlanType)
        Assertions.assertNotNull(
            exercisePlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )

        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam3.healthPlans[2].type],
            getFrequencyIds(exercisePlan.knForeignPlanFrequencyIds)
        )

        // 糖尿病未进行用药评估
        val frequencyHealthAllParam5 =
            frequencyHealthAllParams.first { it.healthPlans[3].type == HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED }
        val drugProgramDiabetesPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIABETES_DRUG_PROGRAM_NOT_EVALUATED.name }
        Assertions.assertEquals(
            frequencyHealthAllParam5.healthPlans[3].cycleStartTime,
            drugProgramDiabetesPlan.knStartDate
        )
        Assertions.assertEquals(frequencyHealthAllParam5.healthPlans[3].type.name, drugProgramDiabetesPlan.knPlanType)
        Assertions.assertEquals(frequencyHealthAllParam5.healthPlans[3].name, NOT_EVALUATE_DRUG_PROGRAM_PLAN_NAME)

        //饮食评估
        val frequencyHealthAllParam3_1 =
            frequencyHealthAllParams.first { it.healthPlans[4].type == HealthPlanType.DIET_NOT_EVALUATED_DIABETES }
        val dietEvaluatePlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_DIABETES.name }
        Assertions.assertEquals(frequencyHealthAllParam3_1.healthPlans[4].cycleStartTime, dietEvaluatePlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam3_1.healthPlans[4].cycleEndTime, dietEvaluatePlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam3_1.healthPlans[4].type.name, dietEvaluatePlan.knPlanType)
        Assertions.assertNotNull(
            dietEvaluatePlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )

        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam3_1.healthPlans[4].type],
            getFrequencyIds(dietEvaluatePlan.knForeignPlanFrequencyIds)
        )

        //糖尿病线下随访计划
        val frequencyHealthAllParam4 =
            frequencyHealthAllParams.first { it.healthPlans[5].type == HealthPlanType.OFFLINE_DIABETES }
        val offlineDiabetesPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.OFFLINE_DIABETES.name }
        Assertions.assertEquals(frequencyHealthAllParam4.healthPlans[5].cycleStartTime, offlineDiabetesPlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam4.healthPlans[5].cycleEndTime, offlineDiabetesPlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam4.healthPlans[5].type.name, offlineDiabetesPlan.knPlanType)
        Assertions.assertNotNull(
            offlineDiabetesPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam4.healthPlans[5].type],
            getFrequencyIds(offlineDiabetesPlan.knForeignPlanFrequencyIds)
        )
    }

    /**
     * 高血压方案的模拟和校验方法抽取
     */
    private fun mockHypertensionCommonPlanData(
        patientId: BigInteger,
        startDate: LocalDate,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {
        val healthPlans = listOf(
            //科普计划模拟
            HealthPlanDTO(
                name = "每日一科普",
                type = com.bjknrt.health.scheme.vo.HealthPlanType.SCIENCE_POPULARIZATION_PLAN,
                subName = null,
                desc = "每日1篇",
                externalKey = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                group = SCIENCE_POPULARIZATION,
                frequencys = SCIENCE_POPULARIZATION_PLAN_FREQUENCIES
            ),
            //饮食计划模拟（高血压）
            HealthPlanDTO(
                name = DIET_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.DIET_PLAN,
                subName = null,
                desc = DIET_PLAN_DESC,
                externalKey = DietPlanTypeEnum.HYPERTENSION.name,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                group = DIET_PLAN_GROUP,
                frequencys = DIET_PLAN_FREQUENCIES
            ),
            //未进行运动评估的运动健康计划模拟
            HealthPlanDTO(
                name = NOT_EVALUATE_SPORT_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = NOT_EVALUATE_SPORT_PLAN_FREQUENCIES
            ),
            // 未进行用药评估模拟(高血压)
            HealthPlanDTO(
                name = NOT_EVALUATE_DRUG_PROGRAM_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.HYPERTENSION_DRUG_PROGRAM_NOT_EVALUATED,
                desc = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = NOT_EVALUATE_DRUG_PROGRAM_PLAN_FREQUENCIES
            ),
            //未进行饮食评估的运动健康计划模拟
            HealthPlanDTO(
                name = NOT_DIET_EVALUATE_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = NOT_DIET_EVALUATE_FREQUENCIES
            ),
            //高血压线下随访模拟（有高血压标签时才会调用）
            HealthPlanDTO(
                name = OFFLINE_HYPERTENSION_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.OFFLINE_HYPERTENSION,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = LocalDate.of(LocalDate.now().year, 1, 1).atStartOfDay(),
                cycleEndTime = null,
                displayTime = startDate.atStartOfDay(),
                clockDisplay = false,
                frequencys = OFFLINE_VISIT_FREQUENCY
            )
        )

        this.mockDeleteAndAddRemindPlan(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = listOf(),
            frequencyHealthAllParams = frequencyHealthAllParams,
            frequencyIdMap = frequencyIdMap
        )
    }

    private fun mockHypertensionManageSchemeData(patientId: BigInteger): Int {
        //定时任务模拟
        val newJobId = ThreadLocalRandom.current().nextInt()
        // todo: Mockito.`when`(jobInfoApi.add(any())).thenReturn(ReturnT(newJobId.toString()))

        //患者的五病标签模拟（仅有高血压患者标签）
        this.mockHypertensionPatient(patientId)

        return newJobId
    }

    private fun mockHypertensionManageSchemeData(
        patientId: BigInteger,
        manageSchemeAndPlanInfo: Pair<HsHealthSchemeManagementInfo, List<HsHsmHealthPlan>>
    ): Int {
        //添加定时任务的模拟
        val newJobId = ThreadLocalRandom.current().nextInt()
        //TODO: 添加定时任务的模拟

        //TODO: 移除定时任务的模拟
        val oldJobId = manageSchemeAndPlanInfo.first.knJobId
            ?: Assertions.fail(AppSpringUtil.getMessage("health-manage-scheme.job-id-is-null"))
        //TODO: 移除定时任务的模拟

        //TODO: JobContext中的方案id和jobId的模拟

        //获取患者信息的模拟（仅有高血压标签）
        this.mockHypertensionPatient(patientId)
        return newJobId
    }

    private fun mockHypertensionStageReportData(
        patientId: BigInteger,
        oldStartDate: LocalDate,
        oldEndDate: LocalDate,
        oldPlanList: List<HsHsmHealthPlan>
    ) {
        //获取方案周期内血压指标集合信息的模拟
        val now = LocalDateTime.now()
        Mockito.doReturn(
            PatientIndicatorListResult(
                bloodSugarListResult = listOf(
                    BloodSugarResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knMeasureAt = now
                    )
                ),
                bodyHeightListResult = listOf(
                    BodyHeightResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knBodyHeight = BigDecimal.valueOf(180),
                        knMeasureAt = now
                    )
                ),
                bodyWeightListResult = listOf(
                    BodyWeightResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knBodyWeight = BigDecimal.valueOf(70),
                        knMeasureAt = now
                    )
                ),
                knWaistlineListResult = listOf(
                    WaistLineResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knWaistline = BigDecimal.valueOf(50),
                        knMeasureAt = now
                    )
                ),
                bloodPressureListResult = listOf(
                    BloodPressureResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knMeasureAt = now,
                        knSystolicBloodPressure = BigDecimal.valueOf(10),
                        knDiastolicBloodPressure = BigDecimal.valueOf(10),
                    ),
                    BloodPressureResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now.minusDays(1),
                        knMeasureAt = now.minusDays(1),
                        knSystolicBloodPressure = BigDecimal.valueOf(20),
                        knDiastolicBloodPressure = BigDecimal.valueOf(20),
                    ),
                    BloodPressureResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now.minusDays(2),
                        knMeasureAt = now.minusDays(2),
                        knSystolicBloodPressure = BigDecimal.valueOf(30),
                        knDiastolicBloodPressure = BigDecimal.valueOf(30),
                    ),
                    BloodPressureResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now.minusDays(3),
                        knMeasureAt = now.minusDays(3),
                        knSystolicBloodPressure = BigDecimal.valueOf(40),
                        knDiastolicBloodPressure = BigDecimal.valueOf(40),
                    )
                ),
                bloodLipidsListResult = listOf(
                    BloodLipidsResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knMeasureAt = now
                    )
                ),
                smokeListResult = listOf(
                    SmokeResult(
                        knId = AppIdUtil.nextId(),
                        knCreatedAt = now,
                        knMeasureAt = now,
                        knNum = 5
                    )
                ),
            )
        ).`when`(indicatorClient).selectAnyIndicatorListForDpm(
            FindListParam(
                startTime = oldStartDate.atStartOfDay(),
                endTime = oldEndDate.atStartOfDay(),
                patientId = patientId
            )
        )

        //获取血压测量计划周期内的打卡次数的模拟
        val bloodPressurePlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.BLOOD_PRESSURE_MEASUREMENT.name }

        Mockito.doReturn(2).`when`(healthPlanClient).dateTimeGetClockIn(
            DateTimeGetClockInParams(
                healthPlanId = bloodPressurePlan.knForeignPlanId,
                startTime = oldStartDate.atStartOfDay(),
                endTime = oldEndDate.atStartOfDay().plusHours(-1),
            )
        )

        //阶段性报告-获取行为习惯评估记录的模拟
        Mockito.doReturn(
            listOf(
                LastAnswerRecord(
                    id = BigInteger.valueOf(1),
                    answerBy = patientId,
                    resultsTag = "",
                    resultsMsg = "",
                    totalScore = BigDecimal.TEN,
                    createdBy = BigInteger.ONE,
                    createdAt = LocalDateTime.now(),
                )
            )
        ).`when`(questionsAnswerClient).getLastAnswerRecordList(
            LastAnswerRecordListRequest(
                examinationPaperCode = HypertensionReportServiceImpl.BEHAVIOR_EXAMINATION_PAPER_CODE,
                answerBy = patientId,
                needNum = 1,
                startDate = oldStartDate.atStartOfDay(),
                endDate = oldEndDate.atStartOfDay()
            )
        )
    }

    private fun mockHypertensionPersonalPlanData(
        startDate: LocalDate,
        patientId: BigInteger,
        managementStage: ManageStage,
        isStandard: Boolean = false,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {
        val endDate = getEndDate(managementStage, startDate).atStartOfDay()

        //高血压包的血压测量计划模拟
        val healthPlans = mutableListOf(
            HealthPlanDTO(
                name = HealthManageHypertensionServiceImpl.MANAGEMENT_STAGE_TO_BLOOD_PRESSURE_PLAN_NAME_MAP[managementStage]
                    ?: "",
                type = com.bjknrt.health.scheme.vo.HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
                desc = HealthManageHypertensionServiceImpl.BLOOD_PRESSURE_MEASURE_DESC_MSG_MAP[managementStage],
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = endDate,
                frequencys = BLOOD_PRESSURE_FREQUENCY_MAP[managementStage]
            )
        )

        //高血压包的高血压随访计划模拟
        val bloodPressureVisitLastDay =
            if (isStandard) HealthManageHypertensionServiceImpl.BLOOD_PRESSURE_VISIT_LAST_DAY_3
            else HealthManageHypertensionServiceImpl.BLOOD_PRESSURE_VISIT_LAST_DAY_2
        val intervalDay =
            if (isStandard) HealthManageHypertensionServiceImpl.BLOOD_PRESSURE_VISIT_IS_STANDARD
            else HealthManageHypertensionServiceImpl.BLOOD_PRESSURE_VISIT_NOT_STANDARD
        val bloodPressureOnVisitFrequencies =
            if (isStandard) HealthManageHypertensionServiceImpl.BLOOD_PRESSURE_VISIT_FREQUENCIES_3_DAYS_1_SEQUENCE
            else HealthManageHypertensionServiceImpl.BLOOD_PRESSURE_VISIT_FREQUENCIES_2_DAYS_1_SEQUENCE

        var tempStartDateTime = startDate.atStartOfDay()

        val frequencyHealthParamsList = mutableListOf<HealthPlanDTO>()
        do {
            val tempEndDateTime = tempStartDateTime.plusDays(intervalDay)
            tempStartDateTime = tempEndDateTime.plusDays(bloodPressureVisitLastDay)
            val startFormat = LocalDateTimeUtil.format(
                tempStartDateTime,
                HealthManageHypertensionServiceImpl.MONTH_DAY_FORMAT
            )
            val endFormat = LocalDateTimeUtil.format(
                tempEndDateTime.plusDays(-1),
                HealthManageHypertensionServiceImpl.MONTH_DAY_FORMAT
            )

            val healthParams = HealthPlanDTO(
                name = HealthManageHypertensionServiceImpl.BLOOD_PRESSURE_VISIT_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.HYPERTENSION_VISIT,
                desc = "${startFormat}-${endFormat},完成${HealthManageHypertensionServiceImpl.BLOOD_PRESSURE_VISIT_FREQUENCY_NUM}次",
                cycleStartTime = tempStartDateTime,
                cycleEndTime = tempEndDateTime,
                frequencys = bloodPressureOnVisitFrequencies
            )
            tempStartDateTime = tempEndDateTime
            frequencyHealthParamsList.add(healthParams)
        } while (tempStartDateTime < endDate)

        healthPlans.addAll(frequencyHealthParamsList)

        //高血压包的行为习惯随访计划模拟
        val behaviorTempStartDateTime = endDate.plusDays(HealthManageHypertensionServiceImpl.BEHAVIOR_VISIT_LAST_DAY)

        val startFormat = LocalDateTimeUtil.format(
            behaviorTempStartDateTime,
            HealthManageHypertensionServiceImpl.MONTH_DAY_FORMAT
        )
        val endFormat = LocalDateTimeUtil.format(
            endDate.plusDays(-1),
            HealthManageHypertensionServiceImpl.MONTH_DAY_FORMAT
        )
        val healthParams = HealthPlanDTO(
            name = HealthManageHypertensionServiceImpl.BEHAVIOR_VISIT_PLAN_NAME,
            type = com.bjknrt.health.scheme.vo.HealthPlanType.BEHAVIOR_VISIT,
            desc = "${startFormat}-${endFormat},完成${HealthManageHypertensionServiceImpl.BEHAVIOR_VISIT_PLAN_FREQUENCY_NUM}次",
            cycleStartTime = behaviorTempStartDateTime,
            cycleEndTime = endDate,
            frequencys = HealthManageHypertensionServiceImpl.BEHAVIOR_VISIT_PLAN_FREQUENCIES
        )
        healthPlans.addAll(listOf(healthParams))
        this.mockAddRemindPlan(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = listOf(),
            frequencyHealthAllParams = frequencyHealthAllParams,
            frequencyIdMap = frequencyIdMap
        )

        this.mockReminderViewReportPlan(patientId, frequencyHealthAllParams, frequencyIdMap)
    }

    private fun mockHypertensionPatient(patientId: BigInteger) {
        Mockito.doReturn(
            PatientInfoResponse(
                id = patientId,
                name = "loginName",
                gender = Gender.MAN,
                phone = "1231312",
                idCard = "123",
                birthday = LocalDate.of(1996, 8, 23).atStartOfDay(),
                age = 26,
                synthesisDiseaseTag = PatientSynthesisTag.HIGH,
                hypertensionDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.EXISTS,
                diabetesDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
                acuteCoronaryDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
                cerebralStrokeDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
                copdDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
            )
        ).`when`(patientClient).getPatientInfo(patientId)
    }

    /**
     * 模拟答题结果
     */
    private fun mockHypertensionAdapter(
        patientId: BigInteger
    ) {
        // 高血压用药评估结果
        val drugExaminationPaperOptionList = mutableListOf<ExaminationPaperOption>()
        drugExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(330001),
                knOptionId = BigInteger.valueOf(33000101),
                knOptionLabel = "A",
                knMessage = "尊医嘱"
            )
        )
        examinationApi.syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = patientId,
                knExaminationPaperCode = ExaminationCodeEnum.HYPERTENSION_DRUG_PROGRAM.name,
                knExaminationPaperOptionList = drugExaminationPaperOptionList
            )
        )

        // 高血压饮食评估结果
        val dietExaminationPaperOptionList = mutableListOf<ExaminationPaperOption>()
        dietExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(350001),
                knOptionId = BigInteger.valueOf(35000101),
                knOptionLabel = null,
                knMessage = "A1"
            ),
        )
        dietExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(350002),
                knOptionId = BigInteger.valueOf(35000201),
                knOptionLabel = null,
                knMessage = "A2"
            ),
        )
        dietExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(350003),
                knOptionId = BigInteger.valueOf(35000301),
                knOptionLabel = null,
                knMessage = "A3"
            ),
        )

        examinationApi.syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = patientId,
                knExaminationPaperCode = ExaminationCodeEnum.DIET_EVALUATE_HYPERTENSION.name,
                knExaminationPaperOptionList = dietExaminationPaperOptionList
            )
        )

        // 高血压运动禁忌结果
        val sportExaminationPaperOptionList = mutableListOf<ExaminationPaperOption>()
        sportExaminationPaperOptionList.add(
            ExaminationPaperOption(
                knAnswerRecordId = AppIdUtil.nextId(),
                knAnswerResultId = AppIdUtil.nextId(),
                knQuestionsId = BigInteger.valueOf(150001),
                knOptionId = BigInteger.valueOf(15000103),
                knOptionLabel = "每分钟的心跳次数超过130次"
            )
        )
        examinationApi.syncCurrentSchemeExaminationAdapter(
            AddCurrentSchemeExaminationAdapterParam(
                knPatientId = patientId,
                knExaminationPaperCode = ExaminationCodeEnum.SPORT.name,
                knExaminationPaperOptionList = sportExaminationPaperOptionList
            )
        )
    }

    /**
     * 答题结果同步结果校验
     */
    private fun hypertensionAdapterVerify(
        patientId: BigInteger,
        oldManageDetailExaminationAdapter: List<HsManageDetailExaminationAdapter>
    ) {

        //查询新方案和计划信息
        val newManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)

        // 查询新的问卷结果
        val manageDetailExaminationAdapter = examinationService.queryCurrentSchemeExaminationAdapterList(
            ExaminationService.QueryCurrentSchemeExaminationAdapterListParam(
                knPatientId = patientId,
                knHealthManageSchemeId = newManageSchemeAndPlanInfo.first.knId,
                knExaminationPaperCodeSet = HYPERTENSION_EXAMINATION_CODE_LIST
            )
        )
        // 校验用药评估旧的结果和新的是否一致
        val oldDrugAdapterList = oldManageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.HYPERTENSION_DRUG_PROGRAM.name
        }
        val newDrugAdapterList = manageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.HYPERTENSION_DRUG_PROGRAM.name
        }

        Assertions.assertEquals(oldDrugAdapterList.size, newDrugAdapterList.size)
        Assertions.assertEquals(oldDrugAdapterList[0].knAnswerRecordId, newDrugAdapterList[0].knAnswerRecordId)
        Assertions.assertEquals(oldDrugAdapterList[0].knAnswerResultId, newDrugAdapterList[0].knAnswerResultId)
        Assertions.assertEquals(oldDrugAdapterList[0].knQuestionsId, newDrugAdapterList[0].knQuestionsId)
        Assertions.assertEquals(oldDrugAdapterList[0].knOptionId, newDrugAdapterList[0].knOptionId)
        Assertions.assertEquals(oldDrugAdapterList[0].knMessage, newDrugAdapterList[0].knMessage)

        // 校验饮食评估旧的结果和新的是否一致
        val oldDietAdapterList = oldManageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.DIET_EVALUATE_HYPERTENSION.name
        }
        val newDietAdapterList = manageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.DIET_EVALUATE_HYPERTENSION.name
        }

        Assertions.assertEquals(oldDietAdapterList.size, newDietAdapterList.size)
        Assertions.assertEquals(oldDietAdapterList[0].knAnswerRecordId, newDietAdapterList[0].knAnswerRecordId)
        Assertions.assertEquals(oldDietAdapterList[0].knAnswerResultId, newDietAdapterList[0].knAnswerResultId)
        Assertions.assertEquals(oldDietAdapterList[0].knQuestionsId, newDietAdapterList[0].knQuestionsId)
        Assertions.assertEquals(oldDietAdapterList[0].knOptionId, newDietAdapterList[0].knOptionId)
        Assertions.assertEquals(oldDietAdapterList[0].knMessage, newDietAdapterList[0].knMessage)

        Assertions.assertEquals(oldDietAdapterList[1].knAnswerRecordId, newDietAdapterList[1].knAnswerRecordId)
        Assertions.assertEquals(oldDietAdapterList[1].knAnswerResultId, newDietAdapterList[1].knAnswerResultId)
        Assertions.assertEquals(oldDietAdapterList[1].knQuestionsId, newDietAdapterList[1].knQuestionsId)
        Assertions.assertEquals(oldDietAdapterList[1].knOptionId, newDietAdapterList[1].knOptionId)
        Assertions.assertEquals(oldDietAdapterList[1].knMessage, newDietAdapterList[1].knMessage)

        Assertions.assertEquals(oldDietAdapterList[2].knAnswerRecordId, newDietAdapterList[2].knAnswerRecordId)
        Assertions.assertEquals(oldDietAdapterList[2].knAnswerResultId, newDietAdapterList[2].knAnswerResultId)
        Assertions.assertEquals(oldDietAdapterList[2].knQuestionsId, newDietAdapterList[2].knQuestionsId)
        Assertions.assertEquals(oldDietAdapterList[2].knOptionId, newDietAdapterList[2].knOptionId)
        Assertions.assertEquals(oldDietAdapterList[2].knMessage, newDietAdapterList[2].knMessage)

        // 校验用药评估旧的结果和新的是否一致
        val oldSportAdapterList = oldManageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.SPORT.name
        }
        val newSportAdapterList = manageDetailExaminationAdapter.filter {
            it.knExaminationPaperCode ==  ExaminationCodeEnum.SPORT.name
        }

        Assertions.assertEquals(oldSportAdapterList.size, newSportAdapterList.size)
        Assertions.assertEquals(oldSportAdapterList[0].knAnswerRecordId, newSportAdapterList[0].knAnswerRecordId)
        Assertions.assertEquals(oldSportAdapterList[0].knAnswerResultId, newSportAdapterList[0].knAnswerResultId)
        Assertions.assertEquals(oldSportAdapterList[0].knQuestionsId, newSportAdapterList[0].knQuestionsId)
        Assertions.assertEquals(oldSportAdapterList[0].knOptionId, newSportAdapterList[0].knOptionId)
        Assertions.assertEquals(oldSportAdapterList[0].knOptionLabel, newSportAdapterList[0].knOptionLabel)
    }
    private fun hypertensionManageSchemeAndPersonalPlanVerify(
        patientId: BigInteger,
        startDate: LocalDate,
        stage: ManageStage,
        frequencyHealthAllParams: List<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>,
        remoteInvokeCount: Int,
        onlyAddRemoteInvokeCount: Int,
        oldJobId: Int?,
        newJobId: Int?
    ) {
        //查询新方案和计划信息
        val newManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //阶段
        Assertions.assertEquals(stage.name, newManageSchemeAndPlanInfo.first.knManagementStage)
        //开始时间
        Assertions.assertEquals(startDate, newManageSchemeAndPlanInfo.first.knStartDate)
        //结束时间
        Assertions.assertEquals(getEndDate(stage, startDate), newManageSchemeAndPlanInfo.first.knEndDate)
        //类型
        Assertions.assertEquals(HealthManageType.HYPERTENSION.name, newManageSchemeAndPlanInfo.first.knHealthManageType)
        //患者标签
        Assertions.assertEquals("", newManageSchemeAndPlanInfo.first.knDiseaseExistsTag)
        //TODO: 暂时不校验 任务id
        // Assertions.assertEquals(newJobId, newManageSchemeAndPlanInfo.first.knJobId)

        //高血压包的高血压测量计划
        val frequencyHealthAllParam1 =
            frequencyHealthAllParams.first { it.healthPlans[0].type == HealthPlanType.BLOOD_PRESSURE_MEASUREMENT }
        var plan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.BLOOD_PRESSURE_MEASUREMENT.name }
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam1.healthPlans[0].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )

        //高血压包的高血压随访计划
        val frequencyHealthAllParam2 =
            frequencyHealthAllParams.first { it.healthPlans[1].type == HealthPlanType.HYPERTENSION_VISIT }
        plan = newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.HYPERTENSION_VISIT.name }
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam2.healthPlans[1].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )

        //高血压包的行为习惯随访计划
        val frequencyHealthAllParam3 =
            frequencyHealthAllParams.first { it.healthPlans[2].type == HealthPlanType.BEHAVIOR_VISIT }
        plan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.BEHAVIOR_VISIT.name }
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam3.healthPlans[2].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )

        //调用远程方法的参数拦截验证
        val frequencyCaptor: ArgumentCaptor<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam> =
            ArgumentCaptor.forClass(com.bjknrt.medication.remind.vo.FrequencyHealthAllParam::class.java)
        Mockito.verify(healthPlanClient, times(remoteInvokeCount)).upsertTypeFrequencyHealth(capture(frequencyCaptor))
        Assertions.assertEquals(remoteInvokeCount, frequencyCaptor.allValues.size)
        // 仅添加远程调用拦截
        val onlyAddFrequencyCaptor: ArgumentCaptor<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam> =
            ArgumentCaptor.forClass(com.bjknrt.medication.remind.vo.FrequencyHealthAllParam::class.java)
        Mockito.verify(healthPlanClient, times(onlyAddRemoteInvokeCount)).batchAddHealthPlan(capture(onlyAddFrequencyCaptor))
        Assertions.assertEquals(onlyAddRemoteInvokeCount, onlyAddFrequencyCaptor.allValues.size)

        //jobId拦截校验
        oldJobId?.let {
            val frequencyCaptor2: ArgumentCaptor<Int> =
                ArgumentCaptor.forClass(Int::class.java)
            //todo: Mockito.verify(jobInfoApi).pause(capture(frequencyCaptor2))
            Assertions.assertEquals(oldJobId, frequencyCaptor2.value)
        }
    }

    private fun hypertensionCommonPlanVerify(
        patientId: BigInteger,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {
        //查询新方案和计划信息
        val newManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //健康科普计划
        val frequencyHealthAllParam1 =
            frequencyHealthAllParams.first { it.healthPlans[0].type == HealthPlanType.SCIENCE_POPULARIZATION_PLAN }
        val sciencePopularPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name }
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleStartTime, sciencePopularPlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleEndTime, sciencePopularPlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].type.name, sciencePopularPlan.knPlanType)
        Assertions.assertNotNull(
            sciencePopularPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam1.healthPlans[0].type],
            getFrequencyIds(sciencePopularPlan.knForeignPlanFrequencyIds)
        )

        //饮食计划（仅有高血压患者标签）
        val frequencyHealthAllParam2 =
            frequencyHealthAllParams.first { it.healthPlans[1].externalKey == DietPlanTypeEnum.HYPERTENSION.name }
        val dietPlan = newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIET_PLAN.name }
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleStartTime, dietPlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleEndTime, dietPlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].type.name, dietPlan.knPlanType)
        Assertions.assertNotNull(dietPlan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam2.healthPlans[1].type],
            getFrequencyIds(dietPlan.knForeignPlanFrequencyIds)
        )

        //运动计划
        val frequencyHealthAllParam3 =
            frequencyHealthAllParams.first { it.healthPlans[2].type == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED }
        val exercisePlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name }
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleStartTime, exercisePlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleEndTime, exercisePlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].type.name, exercisePlan.knPlanType)
        Assertions.assertNotNull(
            exercisePlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )

        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam3.healthPlans[2].type],
            getFrequencyIds(exercisePlan.knForeignPlanFrequencyIds)
        )

        // 高血压未进行用药评估计划
        val frequencyHealthAllParam5 =
            frequencyHealthAllParams.first { it.healthPlans[3].type == HealthPlanType.HYPERTENSION_DRUG_PROGRAM_NOT_EVALUATED }
        val drugProgramPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.HYPERTENSION_DRUG_PROGRAM_NOT_EVALUATED.name }
        Assertions.assertEquals(frequencyHealthAllParam5.healthPlans[3].name, NOT_EVALUATE_DRUG_PROGRAM_PLAN_NAME)
        Assertions.assertEquals(frequencyHealthAllParam5.healthPlans[3].cycleStartTime, drugProgramPlan.knStartDate)

        //未进行饮食评估
        val frequencyHealthAllParam3_1 =
            frequencyHealthAllParams.first { it.healthPlans[4].type == HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION }
        val dietEvaluatePlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION.name }
        Assertions.assertEquals(frequencyHealthAllParam3_1.healthPlans[4].cycleStartTime, dietEvaluatePlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam3_1.healthPlans[4].cycleEndTime, dietEvaluatePlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam3_1.healthPlans[4].type.name, dietEvaluatePlan.knPlanType)
        Assertions.assertNotNull(
            dietEvaluatePlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )

        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam3_1.healthPlans[4].type],
            getFrequencyIds(dietEvaluatePlan.knForeignPlanFrequencyIds)
        )

        //高血压线下随访计划
        val frequencyHealthAllParam4 =
            frequencyHealthAllParams.first { it.healthPlans[5].type == HealthPlanType.OFFLINE_HYPERTENSION }
        val offlineHypertensionPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.OFFLINE_HYPERTENSION.name }
        Assertions.assertEquals(
            frequencyHealthAllParam4.healthPlans[5].cycleStartTime,
            offlineHypertensionPlan.knStartDate
        )
        Assertions.assertEquals(frequencyHealthAllParam4.healthPlans[5].cycleEndTime, offlineHypertensionPlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam4.healthPlans[5].type.name, offlineHypertensionPlan.knPlanType)
        Assertions.assertNotNull(
            offlineHypertensionPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam4.healthPlans[5].type],
            getFrequencyIds(offlineHypertensionPlan.knForeignPlanFrequencyIds)
        )
    }

    private fun hypertensionCommonPlanVerify(
        patientId: BigInteger,
        oldPlanList: List<HsHsmHealthPlan>
    ) {
        //查询新方案和计划信息
        val newManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //健康科普计划
        val oldScienceHealthPlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name }
        val sciencePopularPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name }
        Assertions.assertEquals(oldScienceHealthPlan.knStartDate, sciencePopularPlan.knStartDate)
        Assertions.assertEquals(oldScienceHealthPlan.knEndDate, sciencePopularPlan.knEndDate)
        Assertions.assertEquals(oldScienceHealthPlan.knPlanType, sciencePopularPlan.knPlanType)
        Assertions.assertNotNull(
            sciencePopularPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldScienceHealthPlan.knForeignPlanFrequencyIds),
            getFrequencyIds(sciencePopularPlan.knForeignPlanFrequencyIds)
        )

        //饮食计划（高血压患者标签）
        val oldDietPlan = oldPlanList.first { it.knPlanType == HealthPlanType.DIET_PLAN.name }
        val dietPlan = newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIET_PLAN.name }
        Assertions.assertEquals(oldDietPlan.knStartDate, dietPlan.knStartDate)
        Assertions.assertEquals(oldDietPlan.knEndDate, dietPlan.knEndDate)
        Assertions.assertEquals(oldDietPlan.knPlanType, dietPlan.knPlanType)
        Assertions.assertNotNull(dietPlan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            getFrequencyIds(oldDietPlan.knForeignPlanFrequencyIds),
            getFrequencyIds(dietPlan.knForeignPlanFrequencyIds)
        )

        //运动计划
        val oldExercisePlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name }
        val exercisePlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name }
        Assertions.assertEquals(oldExercisePlan.knStartDate, exercisePlan.knStartDate)
        Assertions.assertEquals(oldExercisePlan.knEndDate, exercisePlan.knEndDate)
        Assertions.assertEquals(oldExercisePlan.knPlanType, exercisePlan.knPlanType)
        Assertions.assertNotNull(
            exercisePlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldExercisePlan.knForeignPlanFrequencyIds),
            getFrequencyIds(exercisePlan.knForeignPlanFrequencyIds)
        )

        //饮食评估
        val oldDietEvaluatePlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION.name }
        val dietEvaluatePLan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIET_NOT_EVALUATED_HYPERTENSION.name }
        Assertions.assertEquals(oldDietEvaluatePlan.knStartDate, dietEvaluatePLan.knStartDate)
        Assertions.assertEquals(oldDietEvaluatePlan.knEndDate, dietEvaluatePLan.knEndDate)
        Assertions.assertEquals(oldDietEvaluatePlan.knPlanType, dietEvaluatePLan.knPlanType)
        Assertions.assertNotNull(
            dietEvaluatePLan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldDietEvaluatePlan.knForeignPlanFrequencyIds),
            getFrequencyIds(dietEvaluatePLan.knForeignPlanFrequencyIds)
        )

        //高血压线下随访计划
        val oldOfflineHypertensionPlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.OFFLINE_HYPERTENSION.name }
        val offlineHypertensionPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.OFFLINE_HYPERTENSION.name }
        Assertions.assertEquals(oldOfflineHypertensionPlan.knStartDate, offlineHypertensionPlan.knStartDate)
        Assertions.assertEquals(oldOfflineHypertensionPlan.knEndDate, offlineHypertensionPlan.knEndDate)
        Assertions.assertEquals(oldOfflineHypertensionPlan.knPlanType, offlineHypertensionPlan.knPlanType)
        Assertions.assertNotNull(
            offlineHypertensionPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldOfflineHypertensionPlan.knForeignPlanFrequencyIds),
            getFrequencyIds(offlineHypertensionPlan.knForeignPlanFrequencyIds)
        )

        // 高血压未进行用药评估计划
        val oldDrugProgramPlan =
            oldPlanList.first { it.knPlanType == HealthPlanType.HYPERTENSION_DRUG_PROGRAM_NOT_EVALUATED.name }
        val drugProgramPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.HYPERTENSION_DRUG_PROGRAM_NOT_EVALUATED.name }
        Assertions.assertEquals(oldDrugProgramPlan.knStartDate, drugProgramPlan.knStartDate)
        Assertions.assertEquals(oldDrugProgramPlan.knPlanType, drugProgramPlan.knPlanType)
        Assertions.assertNotNull(
            drugProgramPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            getFrequencyIds(oldDrugProgramPlan.knForeignPlanFrequencyIds),
            getFrequencyIds(drugProgramPlan.knForeignPlanFrequencyIds)
        )
    }

    /**
     * 高血压和糖尿病方案公共方法
     */
    private fun getManageSchemeAndPlanInfo(patientId: BigInteger): Pair<HsHealthSchemeManagementInfo, List<HsHsmHealthPlan>> {
        val currentDate = LocalDateTime.now()
        //查询健康管理方案
        val managementInfo = healthSchemeManagementInfoTable.select()
            .where(HsHealthSchemeManagementInfoTable.KnPatientId eq patientId.arg)
            .where(HsHealthSchemeManagementInfoTable.KnCreatedAt lte currentDate.arg)
            .order(HsHealthSchemeManagementInfoTable.KnId, Order.Desc)
            .findOne()
            ?: Assertions.fail(AppSpringUtil.getMessage("patient-health-manage-scheme.not-found"))

        //查询所有健康计划
        val planList = hsmHealthPlanTable.select()
            .where(HsHsmHealthPlanTable.KnSchemeManagementId eq managementInfo.knId)
            .order(HsHsmHealthPlanTable.KnId, Order.Desc)
            .find()
            ?: Assertions.fail(AppSpringUtil.getMessage("health-plan.not-found"))
        return Pair(managementInfo, planList)
    }

    private fun mockDiabetesPatient(patientId: BigInteger) {
        Mockito.doReturn(
            PatientInfoResponse(
                id = patientId,
                name = "loginName",
                gender = Gender.MAN,
                phone = "1231312",
                idCard = "123",
                birthday = LocalDate.of(1996, 8, 23).atStartOfDay(),
                age = 26,
                synthesisDiseaseTag = PatientSynthesisTag.HIGH,
                hypertensionDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
                diabetesDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.EXISTS,
                acuteCoronaryDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
                cerebralStrokeDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
                copdDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
            )
        ).`when`(patientClient).getPatientInfo(patientId)
    }

    private fun getEndDate(manageStage: ManageStage, startDate: LocalDate): LocalDate {
        return when (manageStage) {
            ManageStage.INITIAL_STAGE -> startDate.plusDays(SEVEN_DAY)
            ManageStage.STABLE_STAGE -> startDate.plusDays(FOURTEEN_DAY)
            ManageStage.METAPHASE_STABLE_STAGE -> startDate.plusDays(EIGHTY_FOUR_DAY)
            else -> startDate.plusDays(SEVEN_DAY)
        }
    }

    /**
     * 此方法是先删除,后添加健康计划
     */
    private fun mockDeleteAndAddRemindPlan(
        patientId: BigInteger,
        healthPlans: List<HealthPlanDTO>,
        drugPlans: List<DrugPlanDTO>,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {
        val remindFrequencyHealthAllParam = buildRemindFrequencyHealthAllParam(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = drugPlans
        )
        val mockHealthPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        val mockDrugPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        remindFrequencyHealthAllParam.healthPlans.forEach {
            mockHealthPlanResult.add(
                UpsertHealthFrequencyResult(
                    id = AppIdUtil.nextId(),
                    type = com.bjknrt.medication.remind.vo.HealthPlanType.valueOf(it.type.name),
                    cycleStartTime = it.cycleStartTime?: LocalDateTime.now(),
                    cycleEndTime = it.cycleEndTime,
                    frequencyIds = listOf(AppIdUtil.nextId())
                )
            )
        }
        remindFrequencyHealthAllParam.drugPlans.forEach {
            mockDrugPlanResult.add(
                UpsertHealthFrequencyResult(
                    id = AppIdUtil.nextId(),
                    type = com.bjknrt.medication.remind.vo.HealthPlanType.valueOf(it.type.name),
                    cycleStartTime = it.cycleStartTime,
                    cycleEndTime = it.cycleEndTime,
                    frequencyIds = null
                )
            )
        }
        Mockito.doReturn(
            BatchHealthPlanResult(
                healthPlans = mockHealthPlanResult,
                drugPlans = mockDrugPlanResult
            )
        ).`when`(healthPlanClient).upsertTypeFrequencyHealth(remindFrequencyHealthAllParam)

        frequencyHealthAllParams.add(remindFrequencyHealthAllParam)

        val typeGroup = mockHealthPlanResult.groupBy { it.type }
        for (type in typeGroup) {
            val typeVal = type.value
            val lists: List<List<Id>?> = typeVal.map { it.frequencyIds }
            val collect: MutableList<Id> = lists.stream().flatMap { u -> u?.stream() }.collect(Collectors.toList())
            frequencyIdMap[type.key] = collect
        }
    }

    /**
     * 此方法是仅添加健康计划的mock
     */
    private fun mockAddRemindPlan(
        patientId: BigInteger,
        healthPlans: List<HealthPlanDTO>,
        drugPlans: List<DrugPlanDTO>,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {
        val remindFrequencyHealthAllParam = buildRemindFrequencyHealthAllParam(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = drugPlans
        )
        val mockHealthPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        val mockDrugPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        remindFrequencyHealthAllParam.healthPlans.forEach {
            mockHealthPlanResult.add(
                UpsertHealthFrequencyResult(
                    id = AppIdUtil.nextId(),
                    type = com.bjknrt.medication.remind.vo.HealthPlanType.valueOf(it.type.name),
                    cycleStartTime = it.cycleStartTime?: LocalDateTime.now(),
                    cycleEndTime = it.cycleEndTime,
                    frequencyIds = listOf(AppIdUtil.nextId())
                )
            )
        }
        remindFrequencyHealthAllParam.drugPlans.forEach {
            mockDrugPlanResult.add(
                UpsertHealthFrequencyResult(
                    id = AppIdUtil.nextId(),
                    type = com.bjknrt.medication.remind.vo.HealthPlanType.valueOf(it.type.name),
                    cycleStartTime = it.cycleStartTime,
                    cycleEndTime = it.cycleEndTime,
                    frequencyIds = null
                )
            )
        }
        Mockito.doReturn(
            BatchHealthPlanResult(
                healthPlans = mockHealthPlanResult,
                drugPlans = mockDrugPlanResult
            )
        ).`when`(healthPlanClient).batchAddHealthPlan(remindFrequencyHealthAllParam)

        frequencyHealthAllParams.add(remindFrequencyHealthAllParam)

        val typeGroup = mockHealthPlanResult.groupBy { it.type }
        for (type in typeGroup) {
            val typeVal = type.value
            val lists: List<List<Id>?> = typeVal.map { it.frequencyIds }
            val collect: MutableList<Id> = lists.stream().flatMap { u -> u?.stream() }.collect(Collectors.toList())
            frequencyIdMap[type.key] = collect
        }
    }

    /**
     * 脑卒中方案的模拟和校验方法抽取
     */
    private fun mockCerebralStrokeManageSchemeData(patientId: BigInteger) {
        //患者的五病标签模拟（有脑卒中患者标签）
        this.mockCerebralStrokePatient(patientId)
    }

    private fun mockCerebralStrokePatient(patientId: BigInteger) {
        Mockito.doReturn(
            PatientInfoResponse(
                id = patientId,
                name = "loginName",
                gender = Gender.MAN,
                phone = "1231312",
                idCard = "123",
                birthday = LocalDate.of(1996, 8, 23).atStartOfDay(),
                age = 26,
                synthesisDiseaseTag = PatientSynthesisTag.HIGH,
                hypertensionDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
                diabetesDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
                acuteCoronaryDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
                cerebralStrokeDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.EXISTS,
                copdDiseaseTag = com.bjknrt.doctor.patient.management.vo.PatientTag.HIGH,
            )
        ).`when`(patientClient).getPatientInfo(patientId)
    }

    private fun mockCerebralStrokeCommonPlanData(
        patientId: BigInteger,
        startDate: LocalDate,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {
        //科普计划模拟
        var healthPlans = listOf(
            HealthPlanDTO(
                name = "每日一科普",
                type = com.bjknrt.health.scheme.vo.HealthPlanType.SCIENCE_POPULARIZATION_PLAN,
                subName = null,
                desc = "每日1篇",
                externalKey = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                group = SCIENCE_POPULARIZATION,
                frequencys = SCIENCE_POPULARIZATION_PLAN_FREQUENCIES
            ),
            //饮食计划模拟（冠心病/脑卒中标签）
            HealthPlanDTO(
                name = DIET_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.DIET_PLAN,
                subName = null,
                desc = DIET_PLAN_DESC,
                externalKey = DietPlanTypeEnum.ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE.name,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                group = DIET_PLAN_GROUP,
                frequencys = DIET_PLAN_FREQUENCIES
            ),
            //未进行运动评估的运动健康计划模拟
            HealthPlanDTO(
                name = NOT_EVALUATE_SPORT_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = startDate.atStartOfDay(),
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = NOT_EVALUATE_SPORT_PLAN_FREQUENCIES
            )
        )
        this.mockDeleteAndAddRemindPlan(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = listOf(),
            frequencyHealthAllParams = frequencyHealthAllParams,
            frequencyIdMap = frequencyIdMap
        )
    }

    private fun mockCerebralStrokePersonalPlanData(
        startDate: LocalDate,
        patientId: BigInteger,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {

        val startDateTime = startDate.atStartOfDay()
        val healthPlans = listOf(
            //脑卒中包的血压测量计划模拟
            HealthPlanDTO(
                name = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BLOOD_PRESSURE_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
                desc = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BLOOD_PRESSURE_MEASURE_DESC,
                cycleStartTime = startDateTime,
                cycleEndTime = null,
                frequencys = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BLOOD_PRESSURE_FREQUENCY
            ),
            //脑卒中包的出院随访计划模拟
            HealthPlanDTO(
                name = HealthManageCerebralStrokeServiceImpl.LEAVE_HOSPITAL_VISIT_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.LEAVE_HOSPITAL_VISIT,
                desc = null,
                cycleStartTime = startDateTime.plusDays(-1),
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = HealthManageCerebralStrokeServiceImpl.LEAVE_HOSPITAL_VISIT_PLAN_FREQUENCY
            ),
            //脑卒中包的提醒康复训练计划模拟
            HealthPlanDTO(
                name = HealthManageCerebralStrokeServiceImpl.REHABILITATION_TRAINING_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.REHABILITATION_TRAINING_REMIND,
                subName = "",
                desc = HealthManageCerebralStrokeServiceImpl.REHABILITATION_TRAINING_PLAN_DESC,
                cycleStartTime = startDateTime,
                cycleEndTime = null,
                clockDisplay = false,
                frequencys = HealthManageCerebralStrokeServiceImpl.REHABILITATION_TRAINING_SEQUENCE
            )
        )



        this.mockDeleteAndAddRemindPlan(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = listOf(),
            frequencyHealthAllParams = frequencyHealthAllParams,
            frequencyIdMap = frequencyIdMap
        )
    }

    private fun cerebralStrokeSchemeAndPersonalPlanVerify(
        patientId: BigInteger,
        startDate: LocalDate,
        frequencyHealthAllParams: List<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>,
        remoteInvokeCount: Int
    ) {
        //查询方案和关联的所有计划信息
        val oldManageSchemeAndNewPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //开始时间
        Assertions.assertEquals(startDate, oldManageSchemeAndNewPlanInfo.first.knStartDate)
        //类型
        Assertions.assertEquals(
            HealthManageType.CEREBRAL_STROKE.name,
            oldManageSchemeAndNewPlanInfo.first.knHealthManageType
        )
        //患者标签
        Assertions.assertEquals("",oldManageSchemeAndNewPlanInfo.first.knDiseaseExistsTag)

        //脑卒中包的高血压测量计划
        val frequencyHealthAllParam1 =
            frequencyHealthAllParams.first { it.healthPlans[0].type == HealthPlanType.BLOOD_PRESSURE_MEASUREMENT }
        var plan =
            oldManageSchemeAndNewPlanInfo.second.first { it.knPlanType == HealthPlanType.BLOOD_PRESSURE_MEASUREMENT.name }
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam1.healthPlans[0].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )

        //脑卒中包的出院随访计划
        val frequencyHealthAllParam2 =
            frequencyHealthAllParams.first { it.healthPlans[1].type == HealthPlanType.LEAVE_HOSPITAL_VISIT }
        plan =
            oldManageSchemeAndNewPlanInfo.second.first { it.knPlanType == HealthPlanType.LEAVE_HOSPITAL_VISIT.name }
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam2.healthPlans[1].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )

        //脑卒中包的提醒康复训练计划
        val frequencyHealthAllParam3 =
            frequencyHealthAllParams.first { it.healthPlans[2].type == HealthPlanType.REHABILITATION_TRAINING_REMIND }
        plan =
            oldManageSchemeAndNewPlanInfo.second.first { it.knPlanType == HealthPlanType.REHABILITATION_TRAINING_REMIND.name }
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleStartTime, plan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleEndTime, plan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].type.name, plan.knPlanType)
        Assertions.assertNotNull(plan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam3.healthPlans[2].type],
            getFrequencyIds(plan.knForeignPlanFrequencyIds)
        )

        //调用远程方法的参数拦截验证
        val frequencyCaptor: ArgumentCaptor<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam> =
            ArgumentCaptor.forClass(com.bjknrt.medication.remind.vo.FrequencyHealthAllParam::class.java)
        Mockito.verify(healthPlanClient, times(remoteInvokeCount)).upsertTypeFrequencyHealth(capture(frequencyCaptor))
        Assertions.assertEquals(remoteInvokeCount, frequencyCaptor.allValues.size)
    }

    private fun cerebralStrokeCommonPlanVerify(
        patientId: BigInteger,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {
        //查询新方案和计划信息
        val newManageSchemeAndPlanInfo = getManageSchemeAndPlanInfo(patientId)
        //健康科普计划
        val frequencyHealthAllParam1 =
            frequencyHealthAllParams.first { it.healthPlans[0].type == HealthPlanType.SCIENCE_POPULARIZATION_PLAN }
        val sciencePopularPlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.SCIENCE_POPULARIZATION_PLAN.name }
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleStartTime, sciencePopularPlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].cycleEndTime, sciencePopularPlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam1.healthPlans[0].type.name, sciencePopularPlan.knPlanType)
        Assertions.assertNotNull(
            sciencePopularPlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam1.healthPlans[0].type],
            getFrequencyIds(sciencePopularPlan.knForeignPlanFrequencyIds)
        )

        //饮食计划（有冠心病/脑卒中患者标签）
        val frequencyHealthAllParam2 =
            frequencyHealthAllParams.first { it.healthPlans[1].externalKey == DietPlanTypeEnum.ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE.name }
        val dietPlan = newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.DIET_PLAN.name }
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleStartTime, dietPlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].cycleEndTime, dietPlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam2.healthPlans[1].type.name, dietPlan.knPlanType)
        Assertions.assertNotNull(dietPlan.knForeignPlanId, AppSpringUtil.getMessage("health-plan.foreign-id.not-found"))
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam2.healthPlans[1].type],
            getFrequencyIds(dietPlan.knForeignPlanFrequencyIds)
        )

        //运动计划
        val frequencyHealthAllParam3 =
            frequencyHealthAllParams.first { it.healthPlans[2].type == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED }
        val exercisePlan =
            newManageSchemeAndPlanInfo.second.first { it.knPlanType == HealthPlanType.EXERCISE_PROGRAM_NOT_EVALUATED.name }
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleStartTime, exercisePlan.knStartDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].cycleEndTime, exercisePlan.knEndDate)
        Assertions.assertEquals(frequencyHealthAllParam3.healthPlans[2].type.name, exercisePlan.knPlanType)
        Assertions.assertNotNull(
            exercisePlan.knForeignPlanId,
            AppSpringUtil.getMessage("health-plan.foreign-id.not-found")
        )
        Assertions.assertEquals(
            frequencyIdMap[frequencyHealthAllParam3.healthPlans[2].type],
            getFrequencyIds(exercisePlan.knForeignPlanFrequencyIds)
        )
    }


    private fun mockReminderViewReportPlan(
        patientId: BigInteger,
        frequencyHealthAllParams: MutableList<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam>,
        frequencyIdMap: MutableMap<HealthPlanType, List<BigInteger>>
    ) {

        val healthPlans = listOf(
            HealthPlanDTO(
                name = AbstractHealthManageService.REMINDER_VIEW_REPORT_PLAN_NAME,
                type = com.bjknrt.health.scheme.vo.HealthPlanType.REMINDER_VIEW_REPORT,
                subName = null,
                desc = null,
                externalKey = null,
                cycleStartTime = LocalDate.now().atStartOfDay(),
                cycleEndTime = null,
                displayTime = LocalDate.now().atStartOfDay(),
                frequencys = AbstractHealthManageService.REMINDER_VIEW_REPORT_FREQUENCY
            )
        )
        this.mockAddRemindPlan(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = listOf(),
            frequencyHealthAllParams = frequencyHealthAllParams,
            frequencyIdMap = frequencyIdMap
        )
    }
}

