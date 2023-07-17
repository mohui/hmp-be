package com.bjknrt.health.scheme.api

import cn.hutool.core.date.LocalDateTimeUtil
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.framework.util.AppSpringUtil
import com.bjknrt.health.indicator.api.IndicatorApi
import com.bjknrt.health.indicator.vo.BatchIndicator
import com.bjknrt.health.indicator.vo.FromTag
import com.bjknrt.health.scheme.*
import com.bjknrt.health.scheme.entity.DrugPlanDTO
import com.bjknrt.health.scheme.entity.HealthPlanDTO
import com.bjknrt.health.scheme.service.ClockInService
import com.bjknrt.health.scheme.service.health.HealthSchemeManageService
import com.bjknrt.health.scheme.service.health.impl.HealthManageCerebralStrokeServiceImpl
import com.bjknrt.health.scheme.transfer.buildRemindFrequencyHealthAllParam
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.api.MedicationRemindApi
import com.bjknrt.medication.remind.vo.*
import com.bjknrt.medication.remind.vo.HealthPlanType
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.user.permission.centre.api.HealthServiceApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

class VisitTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: VisitApi

    @Autowired
    lateinit var hsHealthSchemeManagementInfoTable: HsHealthSchemeManagementInfoTable

    @Autowired
    lateinit var hsHsmHealthPlanTable: HsHsmHealthPlanTable

    @Autowired
    lateinit var healthSchemeManageService: HealthSchemeManageService

    @Autowired
    lateinit var hsCerebralStrokeLeaveHospitalVisitTable: HsCerebralStrokeLeaveHospitalVisitTable

    @MockBean
    lateinit var medicationRemindClient: MedicationRemindApi

    @MockBean
    lateinit var indicatorServer: IndicatorApi

    @MockBean
    lateinit var patientClient: PatientApi

    @MockBean
    lateinit var healthPlanClient: HealthPlanApi

    @MockBean
    lateinit var healthServiceClient: HealthServiceApi

    @MockBean
    lateinit var clockInService: ClockInService

    // 收缩压
    val signsSbp = BigDecimal.valueOf(120)
    // 舒张压
    val signsDbp = BigDecimal.valueOf(90)
    val currentId = 1.toBigInteger();
    val patientId = 1.toBigInteger();
    val currentDate = LocalDateTime.now();
    val doubleNum = BigDecimal.valueOf(122.23)
    val bigDecimalNumber0 = BigDecimal.ZERO
    val bigDecimalNumber1 = BigDecimal.TEN
    val intNum = 12
    val drugs = listOf(
        DrugsInner("药品名称", 2, "2g", AppIdUtil.nextId(), "生产厂家")
    )
    @BeforeEach
    fun before() {
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = AppIdUtil.nextId()
        )
    }

    @Test
    fun T2dmTest() {
        // 添加糖尿病
        val tParams = T2dmAddParams(
            patientId = patientId,
            signsSbp = signsSbp,
            signsDbp = signsDbp,
            patientName = "患者",
            doctorId = currentId,
            doctorName = "医生名称",
            followWay = FllowWay.FAMILY,
            followDate = currentDate,
            isSymptomNone = true,
            isSymptomDrink = true,
            isSymptomEat = true,
            isSymptomDiuresis = false,
            isSymptomBlurred = false,
            isSymptomInfection = false,
            isSymptomHandFeetNumbness = true,
            isSymptomLowerExtremityEdema = true,
            isSymptomWeightLossDiagnosed = true,
            symptomOther = "other",
            signsHeight = doubleNum,
            signsWeight = doubleNum,
            signsBim = doubleNum,
            signsAdpLeft = SignsAdp.NORMAL,
            signsAdpRight = SignsAdp.NORMAL,
            lifeCigarettesPerDay = 3,
            lifeAlcoholPerDay = 4.0.toBigDecimal(),
            lifeSportPerWeek = bigDecimalNumber0,
            lifeSportPerTime = bigDecimalNumber0,
            lifeFoodSituation = bigDecimalNumber0,
            lifeMentalAdjustment = LifeState.BAD,
            lifeFollowMedicalAdvice = LifeState.GOOD,
            glu = doubleNum,
            hbA1c = null,
            hbA1cDate = null,
            assistOther = "zhifubao",
            drugCompliance = DrugCompliance.GAP,
            isAdr = true,
            adrDesc = "hello",
            rhg = Rhg.OCCASIONALLY,
            visitClass = VisitClass.COMPLICATION,
            insulinName = "nihao",
            insulinDesc = "haha",
            isHasReferral = true,
            referralReason = "aaa",
            referralAgencies = "hehe",
            nextVisit = currentDate,
            drugs = drugs
        )

        val batchAddIndicatorParam = BatchIndicator(
            patientId = tParams.patientId,
            fromTag = FromTag.DOCTOR_VISIT,
            knBodyHeight = tParams.signsHeight,
            knBodyWeight = tParams.signsWeight,
            knBmi = tParams.signsBim,
            knSystolicBloodPressure = tParams.signsSbp,
            knDiastolicBloodPressure = tParams.signsDbp,
            knFastingBloodSandalwood = tParams.glu
        )

        Mockito.doNothing().`when`(indicatorServer).batchAddIndicator(batchAddIndicatorParam)

        // 列表查询条件
        val listTParams = VisitListParams(patientId, 1, 100);

        // 糖尿病添加
        api.t2dmAdd(tParams)
        // 糖尿病列表
        val tList: PagedResult<VisitList> = api.t2dmList(listTParams)
        Assertions.assertEquals(true, tList.total > 0)

        // 糖尿病Id
        val tId = tList._data?.first()?.id!!

        // 测试糖尿病详情
        val tDetail: T2dmVisit = api.t2dmDetail(tId)
        Assertions.assertEquals("患者", tDetail.patientName)

        val gTListParam = VisitListParam(patientId, 1, 100, null)

        // 测试五病随访列表
        val gTResult: PagedResult<VisitList> = api.visitList(VisitListParam(patientId, 1, 100, null))
        Assertions.assertEquals(true, gTResult.total > 0)


        // 测试糖尿病非必填
        val tParams1 = T2dmAddParams(
            patientId = patientId,
            signsSbp = signsSbp,
            signsDbp = signsDbp,
            patientName = "患者",
            followWay = FllowWay.FAMILY,
            followDate = currentDate,
            isSymptomNone = true,
            isSymptomDrink = true,
            isSymptomEat = true,
            isSymptomDiuresis = false,
            isSymptomBlurred = false,
            isSymptomInfection = false,
            isSymptomHandFeetNumbness = true,
            isSymptomLowerExtremityEdema = true,
            isSymptomWeightLossDiagnosed = true,
            signsWeight = doubleNum,
            signsAdpLeft = SignsAdp.NORMAL,
            signsAdpRight = SignsAdp.NORMAL,
            lifeCigarettesPerDay = 3,
            lifeAlcoholPerDay = 4.0.toBigDecimal(),
            lifeSportPerWeek = bigDecimalNumber0,
            lifeSportPerTime = bigDecimalNumber0,
            lifeFoodSituation = bigDecimalNumber0,
            lifeMentalAdjustment = LifeState.BAD,
            lifeFollowMedicalAdvice = LifeState.GOOD,
            glu = doubleNum,
            hbA1c = null,
            hbA1cDate = null,
            drugCompliance = DrugCompliance.GAP,
            isAdr = true,
            rhg = Rhg.OCCASIONALLY
        )
        // 添加糖尿病
        api.t2dmAdd(tParams1)

        val gTResult1: PagedResult<VisitList> =
            api.visitList(
                VisitListParam(
                    patientId,
                    1,
                    100,
                    listOf(
                        VisitType.ACUTE_CORONARY_DISEASE,
                        VisitType.COPD,
                        VisitType.LEAVE_HOSPITAL_VISIT,
                        VisitType.DIABETES,
                        VisitType.CEREBRAL_STROKE,
                        VisitType.HYPERTENSION
                    )
                )
            )
        Assertions.assertEquals(true, gTResult1.total > 1)
    }

    /**
     * 高血压测试
     */
    @Test
    fun htnTest() {
        // 添加高血压
        val gParams = HtnAddParams(
            patientId = patientId,
            // 血压 - 收缩压. 单位: mmHg
            signsSbp = signsSbp,
            // 血压 - 舒张压. 单位: mmHg
            signsDbp = signsDbp,
            patientName = "患者名称",
            doctorId = currentId,
            doctorName = "医生名称",
            // 随访方式
            followWay = FllowWay.FAMILY,
            followDate = currentDate,
            // 无症状
            isSymptomNone = true,
            // 头痛头晕
            isSymptomDizzinessHeadache = false,
            // // 恶心呕吐
            isSymptomNauseaVomiting = true,
            // 眼花耳鸣
            isSymptomBlurredTinnitus = true,
            // 呼吸困难
            isSymptomBreathing = true,
            // 心悸胸闷
            isSymptomPalpitationsToc = false,
            // 鼻衄出血不止
            isSymptomNoseBleed = true,
            // 四肢发麻
            isSymptomNumbness = true,
            // 下肢水肿
            isSymptomLowerExtremityEdema = true,
            // 其它
            symptomOther = "other",
            // 身高
            signsHeight = doubleNum,
            // 体重
            signsWeight = doubleNum,
            // 心率. 单位: 次/分钟
            signsHeartRate = doubleNum,
            signsBim = doubleNum,
            // 体征 - 其他
            signsOther = "stringOther",
            // 日吸烟量. 单位: 支
            lifeCigarettesPerDay = intNum,
            // 日饮酒量. 单位: 两
            lifeAlcoholPerDay = bigDecimalNumber0,
            // 每周运动次数. 单位: 次
            lifeSportPerWeek = intNum,
            // 每次运动时间. 单位: 分钟
            lifeSportPerTime = bigDecimalNumber0,
            // 摄盐情况. 1: 轻; 2: 中; 3: 重
            lifeSaltSituation = LifeSalt.LIGHT,
            // 心理调整. 1: 良好; 2: 一般; 3: 差
            lifeMentalAdjustment = LifeState.BAD,
            // 遵医行为. 1: 良好; 2: 一般; 3: 差
            lifeFollowMedicalAdvice = LifeState.BAD,
            // 辅助检查
            abbreviationsAboutExamination = "examination",
            // 服药依从性. 1: 规律; 2: 间断; 3: 不服药
            drugCompliance = DrugCompliance.GAP,
            // 有无药物不良反应
            isAdr = false,
            // 药物不良反应描述
            adrDesc = "hello",
            // 此次随访分类. 1: 控制满意; 2: 控制不满意; 3: 不良反应; 4: 并发症
            visitClass = VisitClass.COMPLICATION,
            // 是否转诊
            isReferral = true,
            // 转诊原因
            referralReason = "reason",
            // 转诊机构及科别
            referralAgencies = "agencies",
            nextVisit = currentDate,
            drugs = drugs
        )

        val batchAddIndicatorParam = BatchIndicator(
            patientId = gParams.patientId,
            fromTag = FromTag.DOCTOR_VISIT,
            knBodyHeight = gParams.signsHeight,
            knBodyWeight = gParams.signsWeight,
            knBmi = gParams.signsBim,
            knSystolicBloodPressure = gParams.signsSbp,
            knDiastolicBloodPressure = gParams.signsDbp,
            knHeartRate = gParams.signsHeartRate.toInt()
        )

        Mockito.doNothing().`when`(indicatorServer).batchAddIndicator(batchAddIndicatorParam)

        // 列表查询条件
        val listGParams = VisitListParams(patientId, 1, 100);

        // 高血压添加
        api.htnAdd(gParams)
        // 高血压列表
        val hListResult: PagedResult<VisitList> = api.htnList(listGParams)
        // 判断高血压列表是否有值
        Assertions.assertEquals(true, hListResult.total > 0)
        val gId = hListResult._data?.first()?.id!!

        // 高血压详情测试
        val detailHResult: HtnVisit = api.htnDetail(gId)
        Assertions.assertEquals("患者名称", detailHResult.patientName)

        // 测试非必填
        val gParams1 =  HtnAddParams(
            patientId = patientId,
            // 血压 - 收缩压. 单位: mmHg
            signsSbp = signsSbp,
            // 血压 - 舒张压. 单位: mmHg
            signsDbp = signsDbp,
            patientName = "患者名称",
            doctorId = currentId,
            doctorName = "医生名称",
            // 随访方式
            followWay = FllowWay.FAMILY,
            followDate = currentDate,
            // 无症状
            isSymptomNone = true,
            // 头痛头晕
            isSymptomDizzinessHeadache = false,
            // // 恶心呕吐
            isSymptomNauseaVomiting = true,
            // 眼花耳鸣
            isSymptomBlurredTinnitus = true,
            // 呼吸困难
            isSymptomBreathing = true,
            // 心悸胸闷
            isSymptomPalpitationsToc = false,
            // 鼻衄出血不止
            isSymptomNoseBleed = true,
            // 四肢发麻
            isSymptomNumbness = true,
            // 下肢水肿
            isSymptomLowerExtremityEdema = true,
            // 体重
            signsWeight = doubleNum,
            // 心率. 单位: 次/分钟
            signsHeartRate = doubleNum,
            // 体征 - 其他
            signsOther = "stringOther",
            // 日吸烟量. 单位: 支
            lifeCigarettesPerDay = intNum,
            // 日饮酒量. 单位: 两
            lifeAlcoholPerDay = bigDecimalNumber0,
            // 每周运动次数. 单位: 次
            lifeSportPerWeek = intNum,
            // 每次运动时间. 单位: 分钟
            lifeSportPerTime = bigDecimalNumber0,
            // 摄盐情况. 1: 轻; 2: 中; 3: 重
            lifeSaltSituation = LifeSalt.LIGHT,
            // 遵医行为. 1: 良好; 2: 一般; 3: 差
            lifeFollowMedicalAdvice = LifeState.BAD,
            // 服药依从性. 1: 规律; 2: 间断; 3: 不服药
            drugCompliance = DrugCompliance.GAP,
            // 有无药物不良反应
            isAdr = false
        )
        api.htnAdd(gParams1)

        val hListResult1: PagedResult<VisitList> = api.htnList(listGParams)
        Assertions.assertEquals(true, hListResult1.total > 1)
    }

    /**
     * 冠心病测试
     */
    @Test
    fun acuteCoronaryTest() {
        // 添加冠心病
        val addParams = CoronaryAddParams(
            patientId = patientId,
            patientName = "患者名称",
            doctorId = currentId,
            doctorName = "医生名称",
            followWay = FllowWay.ONLINE,
            followDate = currentDate,
            // 无症状
            isSymptomNone = false,
            // 心慌
            isSymptomPains = true,
            // 头晕
            isSymptomDizziness = false,
            // 恶心
            isSymptomNausea = false,
            // 胸口处压榨性疼痛或憋闷感或紧缩感
            isSymptomPalpitationsToc = false,
            // 颈部或喉咙感觉发紧
            isSymptomThroatTight = false,
            // 其他
            symptomOther = null,
            // 血压 - 收缩压
            signsSbp = signsSbp,
            // 血压 - 舒张压
            signsDbp = signsDbp,
            // 体重
            signsWeight = doubleNum,
            // 建议体重
            recommendWeight = doubleNum,
            // 体质指数
            signsBim = doubleNum,
            // 建议体质指数
            recommendBim = doubleNum,
            // 心率
            signsHeartRate = doubleNum,
            // 日吸烟量
            lifeCigarettesPerDay = intNum,
            // 建议日吸烟量
            recommendCigarettesPer = intNum,
            // 日饮酒量
            lifeAlcoholPerDay = doubleNum,
            // 建议饮酒量
            recommendAlcoholPer = doubleNum,
            // 每周运动次数
            lifeSportPerWeek = intNum,
            // 每次运动时间
            lifeSportPerTime = doubleNum,
            // 建议每周运动次数
            recommendSportPerWeek = intNum,
            // 建议每次运动时间
            recommendSportPerTime = doubleNum,
            // 摄盐情况
            lifeSaltSituation = LifeSalt.LIGHT,
            // 建议摄盐情况
            recommendSaltSituation = LifeSalt.LIGHT,
            // 心理调整
            lifeMentalAdjustment = LifeState.BAD,
            // 遵医行为
            lifeFollowMedicalAdvice = LifeState.BAD,
            // 服药依从性
            drugCompliance = DrugCompliance.GAP,
            // 辅助检查
            abbreviationsAboutExamination = "abbreviationsAboutExamination",
            // 有无药物不良反应
            isDrugNone = false,
            // 肌肉痛
            isDrugMyalgia = false,
            // 肌肉无力
            isDrugMuscleWeakness = true,
            // 眼白或皮肤发黄
            isDrugSkinYellowness = false,
            // 其他: 药物不良反应描述
            drugOther = null,
            // 无: 出血
            isBleedNone = false,
            // 鼻出血
            isBleedNose = true,
            // 牙龈出血
            isBleedGums = false,
            // 多次黑褐色大便
            isBleedShit = false,
            // 其他: 描述
            bleedOther = null,
            // 此次随访分类
            visitClass = VisitClass.COMPLICATION,
            // 是否转诊
            isReferral = true,
            // 转诊原因
            referralReason = null,
            // 转诊机构及科别
            referralAgencies = null,
            // 下次随访日期
            nextVisit = null,
            drugs = drugs
        )

        val batchAddIndicatorParam = BatchIndicator(
            patientId = addParams.patientId,
            fromTag = FromTag.DOCTOR_VISIT,
            knBodyHeight = addParams.signsHeight,
            knBodyWeight = addParams.signsWeight,
            knBmi = addParams.signsBim,
            knSystolicBloodPressure = addParams.signsSbp,
            knDiastolicBloodPressure = addParams.signsDbp,
            knHeartRate = addParams.signsHeartRate.toInt()
        )

        Mockito.doNothing().`when`(indicatorServer).batchAddIndicator(batchAddIndicatorParam)


        // 列表查询条件
        val listGParams = VisitListParams(patientId, 1, 100);

        // 添加
        api.acuteCoronaryAdd(addParams)

        // 列表
        val listResult: PagedResult<VisitList> = api.acuteCoronaryList(listGParams)

        // 判断列表是否有值
        Assertions.assertEquals(true, listResult.total > 0)
        val detailId = listResult._data?.first()?.id!!

        // 详情
        val detailResult: AcuteCoronaryVisit = api.acuteCoronaryDetail(detailId)
        Assertions.assertEquals("患者名称", detailResult.patientName)

        // 测试非必填
        val addParams1 = CoronaryAddParams(
            patientId = patientId,
            patientName = "患者名称",
            followWay = FllowWay.ONLINE,
            followDate = currentDate,
            // 无症状
            isSymptomNone = false,
            // 心慌
            isSymptomPains = true,
            // 头晕
            isSymptomDizziness = false,
            // 恶心
            isSymptomNausea = false,
            // 胸口处压榨性疼痛或憋闷感或紧缩感
            isSymptomPalpitationsToc = false,
            // 颈部或喉咙感觉发紧
            isSymptomThroatTight = false,
            // 其他
            symptomOther = null,
            // 血压 - 收缩压
            signsSbp = signsSbp,
            // 血压 - 舒张压
            signsDbp = signsDbp,
            // 体重
            signsWeight = doubleNum,
            // 体质指数
            signsBim = doubleNum,
            // 心率
            signsHeartRate = doubleNum,
            // 日吸烟量
            lifeCigarettesPerDay = intNum,
            // 日饮酒量
            lifeAlcoholPerDay = doubleNum,
            // 每周运动次数
            lifeSportPerWeek = intNum,
            // 每次运动时间
            lifeSportPerTime = doubleNum,
            // 摄盐情况
            lifeSaltSituation = LifeSalt.LIGHT,
            // 心理调整
            lifeMentalAdjustment = LifeState.BAD,
            // 遵医行为
            lifeFollowMedicalAdvice = LifeState.BAD,
            // 服药依从性
            drugCompliance = DrugCompliance.GAP,
            // 有无药物不良反应
            isDrugNone = false,
            // 肌肉痛
            isDrugMyalgia = false,
            // 肌肉无力
            isDrugMuscleWeakness = true,
            // 眼白或皮肤发黄
            isDrugSkinYellowness = false,
            // 其他: 药物不良反应描述
            drugOther = null,
            // 无: 出血
            isBleedNone = false,
            // 鼻出血
            isBleedNose = true,
            // 牙龈出血
            isBleedGums = false,
            // 多次黑褐色大便
            isBleedShit = false,
            drugs = drugs
        )
        api.acuteCoronaryAdd(addParams1)

        val listResult1: PagedResult<VisitList> = api.acuteCoronaryList(listGParams)
        Assertions.assertEquals(true, listResult1.total > 1)
    }


    /**
     * 慢阻肺
     */
    @Test
    fun copdTest() {

        // 添加慢阻肺
        val addParams = CopdAddParams(
            patientId = patientId,
            patientName = "患者名称",
            doctorId = currentId,
            doctorName = "医生名称",
            followWay = FllowWay.FAMILY,
            followDate = currentDate,
            isWheezingEffort = true,
            isWheezingWalkFast = false,
            isWheezingStroll = false,
            isWheezingHundredMeters = false,
            isWheezingLittle = false,
            isSymptomCough = false,
            isSymptomPurulentSputum = false,
            isSymptomInappetence = true,
            isSymptomAbdominalDistention = false,
            isSymptomBreathing = false,
            isSymptomSystemicEdema = false,
            isSymptomNone = false,
            symptomOther = null,
            // 近期脉搏氧饱和度为
            pulseOxygenSaturation = bigDecimalNumber1,
            pulse = bigDecimalNumber1,
            signsSbp = signsSbp,
            signsDbp = signsDbp,
            signsHeight = doubleNum,
            signsWeight = doubleNum,

            isCigarettesPer = false,
            // 日吸烟量. 单位: 支
            lifeCigarettesPerDay = intNum,
            // 建议日吸烟量
            recommendCigarettesPer = intNum,
            // 不饮酒
            isAlcoholPer = true,
            lifeAlcoholPerDay = bigDecimalNumber0,
            recommendAlcoholPer = bigDecimalNumber0,
            // 每周运动次数
            lifeSportPerWeek = intNum,
            lifeSportPerTime = bigDecimalNumber1,
            recommendSportPerWeek = intNum,
            recommendSportPerTime = bigDecimalNumber1,
            // 摄盐情况
            lifeSaltSituation = LifeSalt.LIGHT,
            // 遵医行为
            lifeFollowMedicalAdvice = LifeState.BAD,
            // 服药依从性
            drugCompliance = DrugCompliance.GAP,

            isReactionsPains = true,
            isReactionsConvulsion = false,
            isReactionsDizzinessHeadache = false,
            isReactionsNauseaVomiting = false,
            isReactionsNone = false,
            reactionsOther = null,
            // 肺功能
            pulmonaryFunction = doubleNum,
            drugs = drugs,
        )


        val batchAddIndicatorParam = BatchIndicator(
            patientId = addParams.patientId,
            fromTag = FromTag.DOCTOR_VISIT,
            knBodyHeight = addParams.signsHeight,
            knBodyWeight = addParams.signsWeight,
            knBmi = null,
            knSystolicBloodPressure = addParams.signsSbp,
            knDiastolicBloodPressure = addParams.signsDbp,
            knPulseOximetry = addParams.pulseOxygenSaturation,
            knPulse = addParams.pulse.intValueExact(),
        )

        Mockito.doNothing().`when`(indicatorServer).batchAddIndicator(batchAddIndicatorParam)


        // 列表查询条件
        val listGParams = VisitListParams(patientId, 1, 100);

        // 添加
        api.copdAdd(addParams)

        // 列表
        val listResult: PagedResult<VisitList> = api.copdList(listGParams)

        // 判断列表是否有值
        Assertions.assertEquals(true, listResult.total > 0)
        val detailId = listResult._data?.first()?.id!!

        // 详情
        val detailResult: CopdVisit = api.copdDetail(detailId)
        Assertions.assertEquals("患者名称", detailResult.patientName)

        // 测试非必填
        val addParams1 = CopdAddParams(
            patientId = patientId,
            patientName = "患者名称",
            followWay = FllowWay.FAMILY,
            followDate = currentDate,
            isWheezingEffort = true,
            isWheezingWalkFast = false,
            isWheezingStroll = false,
            isWheezingHundredMeters = false,
            isWheezingLittle = false,
            isSymptomCough = false,
            isSymptomPurulentSputum = false,
            isSymptomInappetence = true,
            isSymptomAbdominalDistention = false,
            isSymptomBreathing = false,
            isSymptomSystemicEdema = false,
            isSymptomNone = false,
            symptomOther = null,
            // 近期脉搏氧饱和度为
            pulseOxygenSaturation = bigDecimalNumber1,
            pulse = bigDecimalNumber1,
            signsSbp = signsSbp,
            signsDbp = signsDbp,
            signsHeight = doubleNum,
            signsWeight = doubleNum,

            isCigarettesPer = false,
            lifeCigarettesPerDay = intNum,
            // 不饮酒
            isAlcoholPer = true,
            lifeAlcoholPerDay = bigDecimalNumber0,
            // 遵医行为
            lifeFollowMedicalAdvice = LifeState.BAD,
            // 服药依从性
            drugCompliance = DrugCompliance.GAP,
            // 摄盐情况. 1: 轻; 2: 中; 3: 重
            lifeSaltSituation = LifeSalt.LIGHT,

            // 每周运动次数
            lifeSportPerWeek = intNum,
            // 每次运动时间. 单位: 分钟
            lifeSportPerTime = bigDecimalNumber1,
            // 肺功能
            pulmonaryFunction = doubleNum,
            isReactionsPains = true,
            isReactionsConvulsion = false,
            isReactionsDizzinessHeadache = false,
            isReactionsNauseaVomiting = false,
            isReactionsNone = false,
            reactionsOther = null,
            drugs = drugs,
        );
        api.copdAdd(addParams1)

        val listResult1: PagedResult<VisitList> = api.copdList(listGParams)
        Assertions.assertEquals(true, listResult1.total > 1)
    }

    /**
     * 脑卒中测试
     */
    @Test
    fun cerebralStrokeTest() {
        val visit = CerebralStrokeVisit(
            patientId = AppIdUtil.nextId(),
            patientName = "患者名称",
            doctorId = AppIdUtil.nextId(),
            doctorName = "医生名称",
            followWay = FllowWay.FAMILY,
            followDate = currentDate,
            mrsScore = BigDecimal(80.00),
            barthelScore = BigDecimal(70.00),
            eq5dScore = BigDecimal(60.00),
            preventionDrug = PreventionDrug.AA,
            isLipidLoweringTherapy = true,
            isRegularMedication = true,
            isHighRiskFactorsStandard = true,
            isPainCauseHead = true,
            isPainCauseArthrosis = true,
            isPainCauseShoulder = true,
            isPainCauseNerve = true,
            isPainCauseNone = false,
            isFall = true,
            normalRecoverDays = 10,
            normalRecoverFrequency = 10,
            normalRecoverTime = BigDecimal(100.00),
            isNormalRecoverSport = true,
            isNormalRecoverWork = true,
            isNormalRecoverAcknowledge = true,
            isNormalRecoverParole = true,
            isNormalRecoverSwallow = true,
            isNormalRecoverNone = false,
            intelligentRecoverDays = 20,
            intelligentRecoverFrequency = 20,
            intelligentRecoverTime = BigDecimal(200.00),
            isIntelligentRecoverBci = true,
            isIntelligentRecoverRobot = true,
            isIntelligentRecoverBalance = true,
            isIntelligentRecoverVirtualReality = true,
            isIntelligentRecoverOther = true,
            isIntelligentRecoverNone = false
        )

        Mockito.doNothing().`when`(clockInService).saveClockIn(
            visit.patientId,
            listOf(com.bjknrt.health.scheme.vo.HealthPlanType.CEREBRAL_STROKE),
            LocalDateTime.now()
        )

        api.cerebralStrokeAdd(visit)

        val pagedResult = api.cerebralStrokeList(
            VisitListParams(
                visit.patientId,
                1,
                10
            )
        )
        Assertions.assertEquals(1L, pagedResult.total)
        Assertions.assertEquals(true, pagedResult.total > 0)
    }

    /**
     * 脑卒中出院随访测试
     */
    @Test
    fun cerebralStrokeLeaveHospitalTest() {
        val monthNum = 3L
        // 先删除, 避免受到其他数据影响
        hsCerebralStrokeLeaveHospitalVisitTable.delete().execute()
        // 脑卒中出院随访添加参数
        val visit = CerebralStrokeLeaveHospitalVisitAddParam(
            patientId = patientId,
            patientName = "患者名称",
            doctorId = AppIdUtil.nextId(),
            doctorName = "医生名称",
            followWay = FllowWay.FAMILY,
            followDate = currentDate,
            knGender = Gender.MAN,
            knAge = intNum,
            knMedicalRecordNumber = "1345",
            knImageNo = "影像号",
            knTelephone = "18788887777",
            knAdmissionDate = LocalDate.now(),
            knLeaveHospitalDate = LocalDate.now(),
            knOaSoiBrainInfarctionSite = InfarctionSite.BRAIN_BEFORE,
            knOaBrainInfarctionToast = ToastTyping.CE,
            knOaPreMorbidMrsScore = BigDecimal.ZERO,
            knOaMrsScore = BigDecimal.ZERO,
            knOaNiHssScore = doubleNum,
            knOaBarthelScore = doubleNum,
            knOaEq5dScore = doubleNum,
            knLhIsStandardMedication = StandardSecondaryPrevention.NOT_STANDARD_MEDICATION,
            knLhIsStandardLipidLoweringTreat = true,
            knLhIsThrombolytic = false,
            knLhAcuteStageCerebralInfarction = false,
            knLhIsMergeSeriousIllness = false,
            knLhIsStandardHighRiskElementControl = false,
            knLhTotalRehabilitationTime = doubleNum,
            knLhMrsScore = doubleNum,
            knLhBarthelScore = doubleNum,
            knLhEq5dScore = doubleNum,
            knDysfunctionSportObstacle = false,
            knDysfunctionCognitionObstacle = false,
            knDysfunctionSpeechObstacle = false,
            knDysfunctionSwallowObstacle = true
        )

        // 补充健康方案管理表数据
        val hsHealthSchemeManagementInfoModels = HsHealthSchemeManagementInfo.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnStartDate(LocalDate.now().plusDays(1))
            .setKnCreatedBy(BigInteger.ZERO)
            .setKnPatientId(patientId)
            .setKnHealthManageType(HealthManageType.CEREBRAL_STROKE.name)
            .setKnDiseaseExistsTag("CEREBRAL_STROKE")
            .setKnManagementStage(null)
            .setIsDel(false)
            .setKnJobId(null)
            .setKnEndDate(null)
            .setKnManagementStage(null)
            .setKnCreatedAt(currentDate)
            .setKnReportOutputDate(null)
            .build()
        hsHealthSchemeManagementInfoTable.save(hsHealthSchemeManagementInfoModels)

        val healthManage = healthSchemeManageService.getCurrentValidHealthSchemeManagementInfoStartWithCreatedAt(patientId)
            ?: Assertions.fail(AppSpringUtil.getMessage("health-plan.not-found"))

        val hsHsmHealthPlanModel = HsHsmHealthPlan.builder()
            .setKnId(AppIdUtil.nextId())
            .setKnPlanType(HealthPlanType.LEAVE_HOSPITAL_VISIT.name)
            .setKnStartDate(currentDate.plusDays(-2))
            .setKnSchemeManagementId(healthManage.knId)
            .setKnForeignPlanId(BigInteger.ZERO)
            .setKnJobId(null)
            .setIsDel(false)
            .setKnEndDate(currentDate.plusDays(2))
            .setKnForeignPlanFrequencyIds("")
            .build()
        hsHsmHealthPlanTable.save(hsHsmHealthPlanModel)

        val healthPlanId = AppIdUtil.nextId()
        // 测试打卡
        Mockito.doReturn(
            listOf(
                HealthPlan(
                    id = healthPlanId,
                    name = "脑卒中出院",
                    type = HealthPlanType.LEAVE_HOSPITAL_VISIT,
                    clockIn = true,
                    cycleStartTime = currentDate.plusDays(-2)
                )
            )
        ).`when`(healthPlanClient).batchIdClockIn(
            BatchClockInParams(
                listOf(healthPlanId),
                clockAt = currentDate
            )
        )

        this.mockCerebralStrokeHealthPlan(currentDate.toLocalDate(), monthNum)

        val newJobId = ThreadLocalRandom.current().nextInt()
        //TODO: Mockito.`when`(jobInfoApi.add(Mockito.any())).thenReturn(ReturnT(newJobId.toString()))


        // 测试添加接口
        api.cerebralStrokeLeaveHospitalVisitAdd(visit)

        // 测试列表接口
        val pagedResult = api.cerebralStrokeLeaveHospitalVisitList(
            VisitListParams(
                visit.patientId,
                1,
                10
            )
        )
        // 列表数据存在
        Assertions.assertEquals(1L, pagedResult.total)

        // ID必须存在, 否则添加失败
        val detailId = pagedResult._data?.first()?.id!!
        // 根据列表接口的ID查询详情接口
        val detailResult = api.cerebralStrokeLeaveHospitalVisitDetail(detailId)
        Assertions.assertEquals("患者名称", detailResult.patientName)

    }

    private fun mockCerebralStrokeHealthPlan(startDate:LocalDate, monthNum: Long) {
        val healthPlans = mutableListOf<HealthPlanDTO>()
        this.mockRemindPlan(currentDate.toLocalDate(), monthNum).let {
            healthPlans.addAll(it)
        }
        this.mockCerebralStrokeBehaviorVisitPlan(currentDate.toLocalDate(), monthNum).let {
            healthPlans.addAll(it)
        }
        this.mockMrsVisitPlan(currentDate.toLocalDate(), monthNum).let {
            healthPlans.addAll(it)
        }
        this.mockBarthelVisitPlan(currentDate.toLocalDate(), monthNum).let {
            healthPlans.addAll(it)
        }
        this.mockEq5DVisitPlan(currentDate.toLocalDate(), monthNum).let {
            healthPlans.addAll(it)
        }
        this.mockitoSaveHealthPlanAndRemindPlan(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = listOf()
        )
    }

    /**
     * mock脑卒中计划 计划条件
     */
    private fun mockRemindPlan(startDate:LocalDate, monthNum: Long):List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_VISIT_PLAN_NAME,
            type = com.bjknrt.health.scheme.vo.HealthPlanType.CEREBRAL_STROKE,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_VISIT_SEQUENCE
        )

       return listOf(params)
    }

    /**
     * mock脑卒中行为习惯 计划条件
     */
    private fun mockCerebralStrokeBehaviorVisitPlan(startDate:LocalDate, monthNum: Long):List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BEHAVIOR_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BEHAVIOR_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BEHAVIOR_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BEHAVIOR_VISIT_PLAN_NAME,
            type = com.bjknrt.health.scheme.vo.HealthPlanType.CEREBRAL_STROKE_BEHAVIOR_VISIT,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BEHAVIOR_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BEHAVIOR_VISIT_SEQUENCE
        )
        return listOf(params)
    }

    /**
     * mRS 计划条件
     */
    private fun mockMrsVisitPlan(startDate:LocalDate, monthNum: Long):List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_MRS_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_MRS_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_MRS_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_MRS_VISIT_PLAN_NAME,
            type = com.bjknrt.health.scheme.vo.HealthPlanType.MRS,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_MRS_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_MRS_VISIT_SEQUENCE
        )
       return listOf(params)
    }

    /**
     * Barthel 计划条件
     */
    private fun mockBarthelVisitPlan(startDate:LocalDate, monthNum: Long):List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BARTHEL_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BARTHEL_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BARTHEL_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BARTHEL_VISIT_PLAN_NAME,
            type = com.bjknrt.health.scheme.vo.HealthPlanType.BARTHEL,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BARTHEL_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_BARTHEL_VISIT_SEQUENCE
        )
       return listOf(params)
    }

    /**
     * EQ_5D 计划条件
     */
    private fun mockEq5DVisitPlan(startDate:LocalDate, monthNum: Long):List<HealthPlanDTO> {
        val planEndDate = startDate.plusMonths(monthNum)
        val planStartDate = planEndDate.plusDays(HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_EQ5D_VISIT_LAST_DAY_15)
        val planStartDateFormat = LocalDateTimeUtil.format(
            planStartDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_EQ5D_VISIT_MONTH_DAY_FORMAT
        )
        val planEndDateFormat = LocalDateTimeUtil.format(
            planEndDate,
            HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_EQ5D_VISIT_MONTH_DAY_FORMAT
        )
        val params = HealthPlanDTO(
            name = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_EQ5D_VISIT_PLAN_NAME,
            type = com.bjknrt.health.scheme.vo.HealthPlanType.EQ_5_D,
            subName = "",
            desc = "$planStartDateFormat-$planEndDateFormat,完成${HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_EQ5D_VISIT_FREQUENCY_NUM}次",
            cycleStartTime = planStartDate.atStartOfDay(),
            cycleEndTime = planEndDate.atStartOfDay(),
            group = null,
            frequencys = HealthManageCerebralStrokeServiceImpl.CEREBRAL_STROKE_EQ5D_VISIT_SEQUENCE
        )

       return listOf(params)
    }

    /**
     * mock公共处理
     */
    private fun mockitoSaveHealthPlanAndRemindPlan(
        patientId: BigInteger,
        healthPlans: List<HealthPlanDTO>,
        drugPlans: List<DrugPlanDTO>
    ): List<HsHsmHealthPlan> {
        //调用远程健康计划进行创建
        val remindFrequencyHealthAllParam = buildRemindFrequencyHealthAllParam(
            patientId = patientId,
            healthPlans = healthPlans,
            drugPlans = drugPlans
        )
        val healthPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        val drugPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        remindFrequencyHealthAllParam.healthPlans.forEach {
            healthPlanResult.add(
                UpsertHealthFrequencyResult(
                    id = AppIdUtil.nextId(),
                    type = HealthPlanType.valueOf(it.type.name),
                    cycleStartTime = it.cycleStartTime?: LocalDateTime.now(),
                    cycleEndTime = it.cycleEndTime,
                    frequencyIds = listOf(AppIdUtil.nextId())
                )
            )
        }
        remindFrequencyHealthAllParam.drugPlans.forEach {
            drugPlanResult.add(
                UpsertHealthFrequencyResult(
                    id = AppIdUtil.nextId(),
                    type = HealthPlanType.valueOf(it.type.name),
                    cycleStartTime = it.cycleStartTime,
                    cycleEndTime = it.cycleEndTime,
                    frequencyIds = listOf(AppIdUtil.nextId())
                )
            )
        }
        Mockito.doReturn(
            BatchHealthPlanResult(
                healthPlans = healthPlanResult,
                drugPlans = drugPlanResult
            )
        ).`when`(healthPlanClient).upsertTypeFrequencyHealth(remindFrequencyHealthAllParam)
        return listOf()
    }
}