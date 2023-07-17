package com.bjknrt.medication.remind.api

import com.bjknrt.article.service.api.ArticleApi
import com.bjknrt.article.service.vo.ArticleAndCardRecommendParam
import com.bjknrt.article.service.vo.ArticleRecommendResponse
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.medication.remind.AbstractContainerBaseTest
import com.bjknrt.medication.remind.MrClockInTable
import com.bjknrt.medication.remind.MrFrequencyTable
import com.bjknrt.medication.remind.MrHealthPlanTable
import com.bjknrt.medication.remind.job.NoticeService
import com.bjknrt.medication.remind.service.HealthPlanService
import com.bjknrt.medication.remind.vo.*
import com.bjknrt.security.test.AppSecurityTestUtil
import com.bjknrt.wechat.service.api.PatientApi
import com.bjknrt.wechat.service.vo.DischargeVisitNotify
import com.bjknrt.wechat.service.vo.PatientNotifyDrugPostRequestInner
import com.bjknrt.wechat.service.vo.PatientNotifyIndicatorScheduledPostRequestInner
import com.bjknrt.wechat.service.vo.VisitNotify
import me.danwi.sqlex.core.query.arg
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.reset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class HealthPlanTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: HealthPlanApi

    @Autowired
    lateinit var medicationRemindApi: MedicationRemindApi

    @Autowired
    lateinit var mrClockInTable: MrClockInTable

    @Autowired
    lateinit var mrFrequencyTable: MrFrequencyTable

    @Autowired
    lateinit var mrHealthPlanTable: MrHealthPlanTable

    @Autowired
    lateinit var healthPlanService: HealthPlanService

    @Autowired
    lateinit var noticeService: NoticeService

    @MockBean
    lateinit var articleServer: ArticleApi

    @MockBean
    lateinit var notifyPatientClient: PatientApi

    // 患者ID
    val patientId = 0.toBigInteger();
    // 健康方案第一条数据主键id
    val healthPlanId0: BigInteger = 10000000.toBigInteger()
    // 健康方案第二至四条数据主键id
    val healthPlanId1: BigInteger = 10000001.toBigInteger()
    val healthPlanId11: BigInteger = 10000011.toBigInteger()
    val healthPlanId12: BigInteger = 10000012.toBigInteger()
    val healthPlanId13: BigInteger = 10000013.toBigInteger()
    val healthPlanId14: BigInteger = 10000014.toBigInteger()
    val healthPlanId15: BigInteger = 10000015.toBigInteger()
    val healthPlanId16: BigInteger = 10000016.toBigInteger()
    val healthPlanId17: BigInteger = 10000017.toBigInteger()
    val healthPlanId100: BigInteger = 10000100.toBigInteger()

    // 调用远程
    val healthPlanId101: BigInteger = BigInteger.valueOf(10000101)
    val healthPlanId102: BigInteger = BigInteger.valueOf(10000102)
    val healthPlanId103: BigInteger = BigInteger.valueOf(10000103)
    val healthPlanId104: BigInteger = BigInteger.valueOf(10000104)
    val healthPlanId105: BigInteger = BigInteger.valueOf(10000105)
    val healthPlanId106: BigInteger = BigInteger.valueOf(10000106)
    val healthPlanId107: BigInteger = BigInteger.valueOf(10000107)
    val healthPlanId108: BigInteger = BigInteger.valueOf(10000108)
    val healthPlanId109: BigInteger = BigInteger.valueOf(10000109)

    @BeforeEach
    fun before() {
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = patientId,
        )
    }

    /**
     * To test HealthPlanApi.clockIn
     */
    @Test
    fun clockInTest() {
        // 获取科普文章列表
        Mockito.`when`(
            articleServer.recommend(
                ArticleAndCardRecommendParam(
                    pageNo = 1,
                    pageSize = 1,
                    readerId = patientId
                )
            )
        ).thenReturn(
            PagedResult(
                totalPage = 1,
                pageSize = 1,
                pageNo = 1,
                total = 1,
                _data = listOf(
                    ArticleRecommendResponse(
                        id = 1574310306312617984.toBigInteger(),
                        title = "文章标题",
                        isCard = false
                    )
                )
            )
        )
        // 今天开始时间
        val todayStartDate = LocalDateTime.now().toLocalDate().atStartOfDay()
        // 今天结束时间
        val todayEndDate = LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()

        // 规则
        val frequency = Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 3,
            frequencyNumUnit = FrequencyNumUnit.DAYS,
            frequencyMaxNum = 3,
            children = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 2,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 3,
            )
        )
        val twoFrequency = Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 2,
            frequencyNumUnit = FrequencyNumUnit.DAYS,
            frequencyMaxNum = 2,
        )
        val listFrequency = mutableListOf<Frequency>()

        listFrequency.add(frequency)
        listFrequency.add(twoFrequency)

        // 第一条数据拼接
        val frequencyHealthParams = FrequencyHealthParams(
            name = "有氧运动",
            type = HealthPlanType.BEHAVIOR_VISIT,
            patientId = patientId,
            id = healthPlanId0,
            subName = "游泳",
            desc = "每周三次",
            group = "HELLO",
            cycleStartTime = todayStartDate,
            cycleEndTime = todayEndDate,
            frequencys = listFrequency
        )

        /**
         * 测试单条添加接口
         */
        api.upsertFrequencyHealth(frequencyHealthParams)
        // 根据主键ID查询一条数据, 如果查询出结果, 插入成功
        val list = mrHealthPlanTable.findByKnId(healthPlanId0)
        // 根据主键比较, 如果一致则添加成功
        assertEquals(healthPlanId0, list?.knId)
        assertTrue(list?.knGroup == "HELLO")

        // 查看健康计划规则,查看是否添加成功
        val frequencyResult = mrFrequencyTable.select().where(MrFrequencyTable.KnHealthPlanId.eq(arg(healthPlanId0))).find()
        assertEquals(true, frequencyResult.size == 3)
        for (frequencyIt in frequencyResult) {
            if (frequencyIt.knFrequencyNumUnit == frequencyIt.knFrequencyTimeUnit) {
                assertTrue(frequencyIt.knFrequencyMaxNum == frequencyIt.knFrequencyTime)
            }

            if (frequencyIt.knFrequencyNumUnit != frequencyIt.knFrequencyTimeUnit) {
                assertTrue(frequencyIt.knFrequencyMaxNum >= frequencyIt.knFrequencyNum)
            }
        }

        /**
         * 打卡
         */
        api.clockIn(healthPlanId0)
        val clockInList = mrClockInTable.select().where(MrClockInTable.KnHealthPlanId.eq(arg(healthPlanId0))).find()
        assertEquals(true, clockInList.isNotEmpty())
        /**
         * 测试根据时间获取打卡次数
         */
        val getClockInCount = api.dateTimeGetClockIn(DateTimeGetClockInParams(
            startTime = todayStartDate,
            endTime = todayEndDate,
            healthPlanId = healthPlanId0
        ))
        assertEquals(1, getClockInCount)
        /**
         * 补卡
         */
        val yesterday = LocalDateTime.now().plusDays(-1).truncatedTo(ChronoUnit.MILLIS)
        api.batchIdClockIn(
            BatchClockInParams(
                ids = listOf(healthPlanId0),
                clockAt = yesterday
            )
        )
        val clockInList1 = mrClockInTable.select().where(MrClockInTable.KnHealthPlanId.eq(arg(healthPlanId0))).find()
        val findClockIn = clockInList1.find { it.knClockAt == yesterday && it.knClockAt != it.knCreatedAt }
        assertTrue(findClockIn != null)

        // 打卡历史
        val clockHistory = api.clockInHistory(ClockInHistoryParam(patientId = patientId, date = todayStartDate.toLocalDate()))
        assertTrue(clockHistory.isNotEmpty())

        val frequencyGetClockInResult = api.frequencyGetClockIn(FrequencyGetClockInParam(
            id = healthPlanId0,
            now = yesterday,
            frequency = Frequency(
                frequencyTime = 1,
                frequencyTimeUnit = TimeUnit.DAYS,
                frequencyNum = 2,
                frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                frequencyMaxNum = 2
            )
        ))
        assertTrue(frequencyGetClockInResult == 1)


        /**
         * 根据type批量添加测试
         */
        val allFrequencyHealthParams = FrequencyHealthParams(
            name = "有氧运动",
            type = HealthPlanType.BEHAVIOR_VISIT,
            patientId = patientId,
            id = healthPlanId1,
            subName = "跑步",
            desc = "每周三次",
            cycleStartTime = todayStartDate,
            cycleEndTime = null,
            frequencys = listFrequency
        )
        val allAddList = mutableListOf<FrequencyHealthParams>()
        allAddList.add(allFrequencyHealthParams)

        // 调用根据type批量添加接口
        val frequencyHealthAllParam = FrequencyHealthAllParam(
            healthPlans = allAddList,
            drugPlans = listOf()
        )

        // 注意,批量添加的时候逻辑删除了第一条添加的数据
        api.upsertTypeFrequencyHealth(frequencyHealthAllParam)

        // 查询批量健康计划列表,判断是否添加成功
        val allList = mrHealthPlanTable.findByKnId(healthPlanId1)
        assertEquals(healthPlanId1, allList?.knId)

        // 查看健康计划规则,查看是否添加成功
        val allFrequencyResult = mrFrequencyTable.select().where(MrFrequencyTable.KnHealthPlanId.eq(arg(healthPlanId1))).find()
        assertEquals(true, allFrequencyResult.size  == 3)

        val listResponse: List<HealthPlan> = api.list()
        // 查找批量添加的是否存在, 第一条单独添加的已经逻辑删除了,是以列表在不受其他影响下应该会只有一条
        val findListResponse =  listResponse.find { it.id == healthPlanId1 }
        assertEquals(true, findListResponse != null)

        /**
         * 测试通过患者ID查询列表
         */
        val patientIdGetHealthPlan = api.patientIdGetList(PatientIdGetListParam(patientId = patientId))
        assertEquals(true, patientIdGetHealthPlan.isNotEmpty())

        /**
         * 测试通过ID查询列表
         */
        val idGetList = api.idGetList(listOf(healthPlanId1))
        assertEquals(true, idGetList.isNotEmpty())

        /**
         * 测试批量添加接口
         * 1. 定义几周几次的健康计划
         * 2. 定义周几几点的健康计划
         * 3. 调用批量添加接口
         * 4. 校验返回值
         */
        // 1. 定义几周几次的健康计划
        val healthPlan1 = FrequencyHealthParams(
            name = "高血压随访",
            type = HealthPlanType.HYPERTENSION_VISIT,
            patientId = patientId,
            subName = "随访",
            desc = "每周三次",
            cycleStartTime = LocalDateTime.now().toLocalDate().atStartOfDay(),
            cycleEndTime = null,
            frequencys = listOf(
                Frequency(
                    frequencyTime = 4,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
                    frequencyMaxNum = 1,
                    children = null
                )
            )
        )
        val healthPlanList = listOf(healthPlan1)

        val drugPlanList = listOf(
            AddDrugRemindParams(
                drugName = "药品1",
                isUsed = true,
                time = LocalTime.now().truncatedTo(ChronoUnit.MINUTES),
                frequencys = listOf(Week.MONDAY, Week.FRIDAY),
                subName = "剂量1",
                patientId = patientId,
                type = HealthPlanType.HYPERTENSION_DRUG_PROGRAM,
                cycleStartTime = LocalDateTime.now(),
                cycleEndTime = null
            ),
            AddDrugRemindParams(
                drugName = "药品2",
                isUsed = true,
                time = LocalTime.now().truncatedTo(ChronoUnit.MINUTES),
                frequencys = listOf(Week.MONDAY, Week.SUNDAY),
                subName = "剂量2",
                patientId = patientId,
                type = HealthPlanType.HYPERTENSION_DRUG_PROGRAM,
                cycleStartTime = LocalDateTime.now(),
                cycleEndTime = null
            )
        )
        // 2. 定义周几几点的健康计划
        val batchAdd = FrequencyHealthAllParam(
            healthPlans = healthPlanList,
            drugPlans = drugPlanList
        )

        // 3. 调用批量添加接口
        val batchAddResponse1 = api.upsertTypeFrequencyHealth (batchAdd);
        // 健康计划返回值
        val healthPlanAddResult = batchAddResponse1.healthPlans
        // 用药计划返回值
        val drugPlanAddResult = batchAddResponse1.drugPlans

        // 4.1 调用返回值条数是否和添加条数相同
        assertEquals(healthPlanList.size, healthPlanAddResult.size)
        assertEquals(drugPlanList.size, drugPlanAddResult.size)

        val healthPlanResultIds = batchAddResponse1.healthPlans.map { it.id }
        val drugPlanResultIds = batchAddResponse1.drugPlans.map { it.id }
        // 根据返回的ID查询几周几次的健康计划列表
        val healthPlanModel = api.idGetList(healthPlanResultIds)
        val healthDrugPlanModel = api.idGetList(drugPlanResultIds)
        // 查询出来的条数相同
        assertEquals(healthPlanList.size, healthPlanModel.size)
        assertEquals(drugPlanList.size, healthDrugPlanModel.size)

        // 对比频率为几周几次查询出来的结果和添加的是否相同
        assertEquals(healthPlanAddResult[0].id, healthPlanModel[0].id)
        assertEquals(healthPlan1.name, healthPlanModel[0].name)
        assertEquals(healthPlan1.subName, healthPlanModel[0].subName)
        assertEquals(healthPlan1.type, healthPlanModel[0].type)
        assertEquals(healthPlan1.desc, healthPlanModel[0].desc)

        // 对比频率为周几几点查询出来的结果和添加的是否相同
        assertEquals(drugPlanAddResult[0].id, healthDrugPlanModel[0].id)
        assertEquals(drugPlanList[0].drugName, healthDrugPlanModel[0].name)
        assertEquals(drugPlanList[0].subName, healthDrugPlanModel[0].subName)
        assertEquals(drugPlanList[0].type, healthDrugPlanModel[0].type)
        assertEquals(drugPlanList[0].time, healthDrugPlanModel[0].time)
        assertEquals(drugPlanAddResult[1].id, healthDrugPlanModel[1].id)
        assertEquals(drugPlanList[1].drugName, healthDrugPlanModel[1].name)
        assertEquals(drugPlanList[1].subName, healthDrugPlanModel[1].subName)
        assertEquals(drugPlanList[1].type, healthDrugPlanModel[1].type)
        assertEquals(drugPlanList[1].time, healthDrugPlanModel[1].time)

        // 测试批量删除
        medicationRemindApi.batchDeleteByTypeAndPatientId(
            listOf(
                TypesAndPatientParam(
                    patientId = patientId,
                    types = listOf(healthPlan1.type, drugPlanList[0].type)
                )
            )
        )

        val delFindList1 = api.idGetList(healthPlanResultIds)
        val delFindList2 = api.idGetList(drugPlanResultIds)
        // 查询删除后的列表
        assertEquals(0, delFindList1.size)
        assertEquals(0, delFindList2.size)
    }

    /**
     * 测试各种频率下的测试
     * 不能一年几周, 一月几周
     * 1. 1年几月
     * 2. 1年几天
     * 3. 1年几次
     * 4. 1月几天
     * 5. 1月几次
     * 6. 1周几天
     * 7. 1周几次
     * 8. 1天几次
     */
    @Sql("/health-plan.sql")
    @Test
    fun listTest() {
        // 获取科普文章列表
        Mockito.`when`(
            articleServer.recommend(
                ArticleAndCardRecommendParam(
                    pageNo = 1,
                    pageSize = 1,
                    readerId = patientId
                )
            )
        ).thenReturn(
            PagedResult(
                totalPage = 1,
                pageSize = 1,
                pageNo = 1,
                total = 1,
                _data = listOf(
                    ArticleRecommendResponse(
                        id = 1574310306312617984.toBigInteger(),
                        title = "文章标题",
                        isCard = false
                    )
                )
            )
        )

        val listResponse: List<HealthPlan> = api.list()
        // healthPlanId11 要求  1年3月  一年5天, 打卡记录是: 当天, 加1天, 加2天, 加1月, 加2月,加3月
        val findHealthPlanId11 = listResponse.find { it.id == healthPlanId11}
        // 1年5天
        assertEquals(6, findHealthPlanId11?.subFrequency?.actualNum)
        // 1年3月
        assertEquals(4, findHealthPlanId11?.mainFrequency?.actualNum)


        // healthPlanId12 要求 1年3次, 1月5次, 打卡记录是: 当天打卡3次, 加1天, 加2天, 加3天
        val findHealthPlanId12 = listResponse.find { it.id == healthPlanId12}
        // 一月5次
        assertEquals(6, findHealthPlanId12?.subFrequency?.actualNum)
        // 一年3次
        assertEquals(6, findHealthPlanId12?.mainFrequency?.actualNum)

        // healthPlanId13 1月3天, 一周5次, 打卡记录是: 昨天打卡2次, 当天打卡2次, 加1天, 加2天
        val findHealthPlanId13 = listResponse.find { it.id == healthPlanId13}
        // 一周5次
        assertEquals(6, findHealthPlanId13?.subFrequency?.actualNum)
        // 一月3天
        assertEquals(4, findHealthPlanId13?.mainFrequency?.actualNum)

        // healthPlanId14 开始时间: 当前-2天, 4周2周，1周3天, 3天2次, 打卡记录是: 昨天打卡2次, 当天打卡1次, 加1天, 加6,7,8天
        val findHealthPlanId14 = listResponse.find { it.id == healthPlanId14}
        // 3天2次
        assertEquals(3, findHealthPlanId14?.subFrequency?.actualNum)
        // 4周2周，1周3天
        assertEquals(2, findHealthPlanId14?.mainFrequency?.actualNum)


        // healthPlanId15 1周4天 1天2次, 1天2次. 打卡记录是: 减2天打卡3次, 减1天打卡3次, 当天打卡3次, 加1天打卡3次
        val findHealthPlanId15 = listResponse.find { it.id == healthPlanId15}
        // 1天2次
        assertEquals(3, findHealthPlanId15?.subFrequency?.actualNum)
        // 1周4天 1天两次
        assertEquals(4, findHealthPlanId15?.mainFrequency?.actualNum)

        // healthPlanId16 4周4周 1周2天 1天2次, 2周3天 1天2次
        val findHealthPlanId16 = listResponse.find { it.id == healthPlanId16}
        // 2周3天 1天2次
        assertEquals(4, findHealthPlanId16?.subFrequency?.actualNum)
        // 4周4周 1周2天 1天2次
        assertEquals(2, findHealthPlanId16?.mainFrequency?.actualNum)

        // healthPlanId17 1天3次
        val findHealthPlanId17 = listResponse.find { it.id == healthPlanId17}
        assertEquals(3, findHealthPlanId17?.mainFrequency?.actualNum)
    }

    @Test
    fun idGetList() {
        // 添加一条数据
        // 明天开始时间
        val startDate = LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()

        // 规则
        val frequency = Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 3,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 3,
            children = null
        )

        val listFrequency = mutableListOf<Frequency>()

        listFrequency.add(frequency)

        // 第一条数据拼接
        val frequencyHealthParams = FrequencyHealthParams(
            name = "有氧运动",
            type = HealthPlanType.BEHAVIOR_VISIT,
            patientId = patientId,
            id = healthPlanId100,
            subName = "游泳",
            desc = "每周三次",
            group = "HELLO",
            cycleStartTime = startDate,
            cycleEndTime = null,
            frequencys = listFrequency
        )

        api.upsertFrequencyHealth(frequencyHealthParams)

        val idGetListResult = api.idGetList(listOf(healthPlanId100))

        val findHealthPlanId100 = idGetListResult.find { it.id == healthPlanId100}
        assertEquals(true, idGetListResult.isNotEmpty())
        assertEquals(startDate, findHealthPlanId100?.cycleStartTime)
    }

    @Test
    fun notifyTest() {
        mrHealthPlanTable.delete().execute()

        drug()

        // 线下随访测试
        offlineVisit()

        // 线上随访测试
        onlineVisit()

        // 测量计划测试
        measure()

        // 出院随访
        leaveHospitalVisitTest()
    }

    private fun drug() {
        reset(notifyPatientClient)
        val weeks = mutableListOf<Week>()
        weeks.add(Week.MONDAY);
        weeks.add(Week.TUESDAY);
        weeks.add(Week.WEDNESDAY);
        weeks.add(Week.THURSDAY);
        weeks.add(Week.FRIDAY);
        weeks.add(Week.SATURDAY);
        weeks.add(Week.SUNDAY);

        val drug1 = UpsertParams(
            drugName = "药1",
            time = LocalTime.now(),
            frequencys = weeks,
            id = healthPlanId107
        )
        val drug2 = UpsertParams(
            drugName = "药1",
            time = LocalTime.now().plusMinutes(1),
            frequencys = weeks,
            id = healthPlanId108
        )
        val drug3 = UpsertParams(
            drugName = "药1",
            time = LocalTime.now().plusMinutes(2),
            frequencys = weeks,
            id = healthPlanId109
        )
        val drug4 = UpsertParams(
            drugName = "药1",
            time = LocalTime.now().plusMinutes(3),
            frequencys = weeks,
            id = healthPlanId109
        )
        val drug5 = UpsertParams(
            drugName = "药1",
            time = LocalTime.now().plusMinutes(4),
            frequencys = weeks,
            id = healthPlanId109
        )

        medicationRemindApi.upsert(drug1)
        medicationRemindApi.upsert(drug2)
        medicationRemindApi.upsert(drug3)
        medicationRemindApi.upsert(drug4)
        medicationRemindApi.upsert(drug5)

        // 添加高血压用药数据
        val batchAdd = FrequencyHealthAllParam(
            healthPlans = listOf(),
            drugPlans = listOf(
                AddDrugRemindParams(
                    drugName = "药品1",
                    isUsed = true,
                    time = LocalTime.now(),
                    frequencys = weeks,
                    subName = "剂量1",
                    patientId = patientId,
                    type = HealthPlanType.HYPERTENSION_DRUG_PROGRAM,
                    cycleStartTime = LocalDateTime.now(),
                    cycleEndTime = null
                )
            )
        )

        api.upsertTypeFrequencyHealth (batchAdd);


        Mockito
            .doReturn(null)
            .`when`(notifyPatientClient).patientNotifyDrugPost(any())

        noticeService.remind(listOf(patientId))

        val listCaptor: ArgumentCaptor<List<PatientNotifyDrugPostRequestInner>> = ArgumentCaptor.forClass(
            MutableList::class.java as Class<List<PatientNotifyDrugPostRequestInner>>
        )

        Mockito.verify(notifyPatientClient).patientNotifyDrugPost(capture(listCaptor))

        assertEquals(true, listCaptor.value.size == 2)

    }

    /**
     * 线下随访添加
     */
    private fun offlineVisit() {
        reset(notifyPatientClient)

        val current = LocalDate.now().atStartOfDay()
        val endDate = current.plusMonths(3)

        val twoMonthAgo = current.plusMonths(-2)

        val endOfflineStart = LocalDate.of(twoMonthAgo.year, twoMonthAgo.month, 1).atStartOfDay()

        val frequency = Frequency(
            frequencyTime = 3,
            frequencyTimeUnit = TimeUnit.MONTHS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 1
        )
        val offlineVisit1 = FrequencyHealthParams(
            id = healthPlanId101,
            name = "线下慢阻肺测试",
            type = HealthPlanType.OFFLINE_COPD,
            patientId = patientId,
            subName = "线下随访开始时间,展示时间是今天",
            desc = "三月一次",
            group = "",
            displayTime = current,
            cycleStartTime = current,
            cycleEndTime = endDate,
            frequencys = listOf(frequency)
        )

        val offlineVisit2 = FrequencyHealthParams(
            id = healthPlanId102,
            name = "线下糖尿病测试",
            type = HealthPlanType.OFFLINE_DIABETES,
            patientId = patientId,
            subName = "开始时间是三个月前, 结束时间应该是今天",
            desc = "三月一次",
            group = "",
            displayTime = endOfflineStart,
            cycleStartTime = endOfflineStart,
            cycleEndTime = null,
            frequencys = listOf(frequency)
        )

        api.upsertFrequencyHealth(offlineVisit1)
        api.upsertFrequencyHealth(offlineVisit2)

        val timeCycle = healthPlanService.calculationCycle(
            frequency.frequencyTime,
            ChronoUnit.valueOf(frequency.frequencyTimeUnit.name),
            current,
            LocalDateTime.now()
        )
        val offlineStartDesc = "${timeCycle.start.format(DateTimeFormatter.ofPattern("MM.dd"))}-${
            timeCycle.end.plusSeconds(-1)?.format(
                DateTimeFormatter.ofPattern("MM.dd")
            )
        }完成"

        val mockParams1 = listOf(
            VisitNotify(
                id = healthPlanId101,
                patientId = patientId,
                name = offlineVisit1.name,
                start = current,
                end = endDate,
                remark = offlineStartDesc
            )
        )
        // 测试
        Mockito.doReturn(null).`when`(notifyPatientClient).patientNotifyVisitOfflinePost(mockParams1)

        noticeService.offlineVisitStartDate(listOf(patientId))

        val listCaptor: ArgumentCaptor<List<VisitNotify>> = ArgumentCaptor.forClass(
            MutableList::class.java as Class<List<VisitNotify>>
        )

        Mockito.verify(notifyPatientClient).patientNotifyVisitOfflinePost(capture(listCaptor))
        assertEquals(mockParams1[0].id, listCaptor.allValues[0][0].id)
        assertEquals(mockParams1[0].patientId, listCaptor.allValues[0][0].patientId)


        // 结束时间测试
        reset(notifyPatientClient)

        val timeEndCycle = healthPlanService.calculationCycle(
            frequency.frequencyTime,
            ChronoUnit.valueOf(frequency.frequencyTimeUnit.name),
            endOfflineStart,
            LocalDateTime.now()
        )

        val offlineEndDesc = "${timeEndCycle.start.format(DateTimeFormatter.ofPattern("MM.dd"))}-${
            timeEndCycle.end.plusSeconds(-1)?.format(
                DateTimeFormatter.ofPattern("MM.dd")
            )
        }完成"

        val mockParams2 = listOf(
            VisitNotify(
                id = healthPlanId102,
                patientId = patientId,
                name = offlineVisit2.name,
                start = timeEndCycle.start,
                end = timeEndCycle.end,
                remark = offlineEndDesc
            )
        )

        // 线下随访结束时间
        Mockito.doReturn(null).`when`(notifyPatientClient).patientNotifyVisitOfflineFinalPost(mockParams2)
        val day = LocalDate.now().dayOfMonth
        noticeService.offlineVisitEndDate(listOf(patientId), day)

        // 拦截mock做校验
        val listCaptor2: ArgumentCaptor<List<VisitNotify>> = ArgumentCaptor.forClass(
            MutableList::class.java as Class<List<VisitNotify>>
        )

        Mockito.verify(notifyPatientClient).patientNotifyVisitOfflineFinalPost(capture(listCaptor2))
        assertEquals(mockParams2[0].id, listCaptor2.allValues[0][0].id)
        assertEquals(mockParams2[0].patientId, listCaptor2.allValues[0][0].patientId)
    }

    /**
     * 线上随访添加
     */
    private fun onlineVisit() {
        reset(notifyPatientClient)
        val current = LocalDate.now().atStartOfDay()
        val endDate = current.plusMonths(3)
        val frequency = Frequency(
            frequencyTime = 3,
            frequencyTimeUnit = TimeUnit.MONTHS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 1
        )
        val onlineVisit1 = FrequencyHealthParams(
            id = healthPlanId103,
            name = "线上慢阻肺测试",
            type = HealthPlanType.ONLINE_COPD,
            patientId = patientId,
            subName = "线上随访开始时间,展示时间是今天",
            desc = "三月一次",
            group = "",
            displayTime = current,
            cycleStartTime = current,
            cycleEndTime = endDate,
            frequencys = listOf(frequency)
        )

        val onlineVisit2 = FrequencyHealthParams(
            id = healthPlanId104,
            name = "线上糖尿病测试",
            type = HealthPlanType.ONLINE_DIABETES,
            patientId = patientId,
            subName = "开始时间,展示时间是今天",
            desc = "三月一次",
            group = "",
            displayTime = current,
            cycleStartTime = current,
            cycleEndTime = endDate,
            frequencys = listOf(frequency)
        )

        api.upsertFrequencyHealth(onlineVisit1)
        api.upsertFrequencyHealth(onlineVisit2)

        val mockParams = listOf(
            VisitNotify(
                id = healthPlanId103,
                patientId = onlineVisit1.patientId,
                name = onlineVisit1.name,
                start = current,
                end = endDate,
                remark = onlineVisit1.desc?:"三月一次"
            ),
            VisitNotify(
                id = healthPlanId104,
                patientId = onlineVisit2.patientId,
                name = onlineVisit2.name,
                start = current,
                end = endDate,
                remark = onlineVisit2.desc?:"三月一次"
            )
        )

        Mockito.doReturn(null).`when`(notifyPatientClient).patientNotifyVisitOnlinePost(mockParams)
        // 调用远程
        noticeService.onlineVisitStartDate(listOf(patientId))

        val listCaptor: ArgumentCaptor<List<VisitNotify>> = ArgumentCaptor.forClass(
            MutableList::class.java as Class<List<VisitNotify>>
        )

        Mockito.verify(notifyPatientClient).patientNotifyVisitOnlinePost(capture(listCaptor))
        assertEquals(mockParams[0].id, listCaptor.allValues[0][0].id)
        assertEquals(mockParams[0].patientId, listCaptor.allValues[0][0].patientId)
        assertEquals(mockParams[1].id, listCaptor.allValues[0][1].id)
        assertEquals(mockParams[1].patientId, listCaptor.allValues[0][1].patientId)
    }

    /**
     * 测量计划
     */
    private fun measure() {
        reset(notifyPatientClient)
        val current = LocalDate.now().atStartOfDay()
        val endDate = current.plusMonths(3)
        val frequency = Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 1
        )
        val measureVisit1 = FrequencyHealthParams(
            id = healthPlanId105,
            name = "测血压",
            type = HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
            patientId = patientId,
            subName = "开始时间,展示时间是今天",
            desc = "测测血压",
            group = "",
            displayTime = current,
            cycleStartTime = current,
            cycleEndTime = endDate,
            frequencys = listOf(frequency)
        )

        api.upsertFrequencyHealth(measureVisit1)

        val mockParams = listOf(
            PatientNotifyIndicatorScheduledPostRequestInner(
                id = healthPlanId105,
                patientId = measureVisit1.patientId,
                name = measureVisit1.name,
                desc = measureVisit1.desc?:"测测血压"
            )
        )
        Mockito
            .doReturn(null)
            .`when`(notifyPatientClient).patientNotifyIndicatorScheduledPost(mockParams)

        noticeService.measure(listOf(patientId))

        val listCaptor: ArgumentCaptor<List<PatientNotifyIndicatorScheduledPostRequestInner>> = ArgumentCaptor.forClass(
            MutableList::class.java as Class<List<PatientNotifyIndicatorScheduledPostRequestInner>>
        )

        Mockito.verify(notifyPatientClient).patientNotifyIndicatorScheduledPost(capture(listCaptor))
        assertEquals(mockParams[0].id, listCaptor.allValues[0][0].id)
        assertEquals(mockParams[0].patientId, listCaptor.allValues[0][0].patientId)
    }

    /**
     * 出院随访测试
     */
    private fun leaveHospitalVisitTest() {
        reset(notifyPatientClient)
        val current = LocalDate.now().atStartOfDay()
        val endDate = current.plusMonths(3)
        val frequency = Frequency(
            frequencyTime = 1,
            frequencyTimeUnit = TimeUnit.WEEKS,
            frequencyNum = 1,
            frequencyNumUnit = FrequencyNumUnit.SEQUENCE,
            frequencyMaxNum = 1
        )
        val leaveHospitalVisit1 = FrequencyHealthParams(
            id = healthPlanId106,
            name = "护院随访",
            type = HealthPlanType.LEAVE_HOSPITAL_VISIT,
            patientId = patientId,
            subName = "开始时间,展示时间是今天",
            desc = "出院随访",
            group = "",
            displayTime = current,
            cycleStartTime = current,
            cycleEndTime = endDate,
            frequencys = listOf(frequency)
        )
        Mockito
            .doReturn(null)
            .`when`(notifyPatientClient).patientNotifyVisitDischargePost(
                DischargeVisitNotify(
                    id = leaveHospitalVisit1.id?: healthPlanId106,
                    patientId = leaveHospitalVisit1.patientId
                )
            )

        api.upsertFrequencyHealth(leaveHospitalVisit1)

        val frequencyCaptor: ArgumentCaptor<DischargeVisitNotify> =
            ArgumentCaptor.forClass(DischargeVisitNotify::class.java)

        Mockito.verify(notifyPatientClient).patientNotifyVisitDischargePost(capture(frequencyCaptor))
        assertEquals(healthPlanId106, frequencyCaptor.allValues[0].id)
        assertEquals(patientId, frequencyCaptor.allValues[0].patientId)
    }
}

