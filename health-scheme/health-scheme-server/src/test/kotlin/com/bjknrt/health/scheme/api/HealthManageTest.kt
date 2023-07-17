package com.bjknrt.health.scheme.api

import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.scheme.AbstractContainerBaseTest
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfo
import com.bjknrt.health.scheme.HsHealthSchemeManagementInfoTable
import com.bjknrt.health.scheme.HsHsmHealthPlanTable
import com.bjknrt.health.scheme.service.health.impl.HealthManageFacadeServiceImpl
import com.bjknrt.health.scheme.transfer.buildDrugPlanDTO
import com.bjknrt.health.scheme.transfer.buildHealthPlanDTO
import com.bjknrt.health.scheme.transfer.buildRemindFrequencyHealthAllParam
import com.bjknrt.health.scheme.vo.*
import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.vo.BatchHealthPlanResult
import com.bjknrt.medication.remind.vo.UpsertHealthFrequencyResult
import com.bjknrt.user.permission.centre.api.HealthServiceApi
import me.danwi.sqlex.core.query.arg
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.kotlin.capture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@AutoConfigureMockMvc
class HealthManageTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var manageApi: ManageApi

    @Autowired
    lateinit var healthManageFacadeServiceImpl: HealthManageFacadeServiceImpl

    @MockBean
    lateinit var healthServiceClient: HealthServiceApi

    @Autowired
    lateinit var hsHealthSchemeManagementInfoTable: HsHealthSchemeManagementInfoTable

    @Autowired
    lateinit var hsHsmHealthPlanTable: HsHsmHealthPlanTable

    @MockBean
    lateinit var healthPlanClient: HealthPlanApi

    /**
     * 测试频率是周一到周日的健康计划添加
     */
    @Test
    fun upsertTypeFrequencyHealthTest() {
        // 患者ID
        val patientId = AppIdUtil.nextId()
        // 方案ID
        val manageId = AppIdUtil.nextId()
        //  准备数据
        prepareData(patientId, manageId)

        // 执行
        val weeks = mutableListOf<Week>();
        weeks.add(Week.SUNDAY)
        // 拼接其他频率参数
        val healthPlans = FrequencyHealthParams(
            name = "高血压计划",
            type = HealthPlanType.HYPERTENSION_VISIT,
            patientId = patientId,
            id = null,
            subName = null,
            desc = null,
            externalKey = null,
            cycleStartTime = LocalDateTime.now(),
            cycleEndTime = null,
            displayTime = LocalDateTime.now(),
            group = null,
            clockDisplay = true,
            frequencys = null
        )

        // 准备添加的参数
        val drugPlan = AddDrugRemindParams(
            patientId = patientId,
            drugName = "药品1",
            isUsed = true,
            time = LocalTime.now().truncatedTo(ChronoUnit.MINUTES),
            frequencys = weeks,
            type = HealthPlanType.HYPERTENSION_DRUG_PROGRAM,
            cycleStartTime = LocalDateTime.now(),
            cycleEndTime = null
        );
        val upsertHealthPlanParam = AddHealthPlanParam(
            healthPlans = listOf(healthPlans),
            drugPlans = listOf(drugPlan)
        )

        // 模拟添加频率为几周几次类型的健康计划类型
        val remindFrequencyHealthAllParam1 = buildRemindFrequencyHealthAllParam(
            patientId = patientId,
            healthPlans = buildHealthPlanDTO(upsertHealthPlanParam.healthPlans),
            drugPlans = buildDrugPlanDTO(upsertHealthPlanParam.drugPlans)
        )

        // 模拟结果
        val mockHealthPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        val mockDrugPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        for (it in upsertHealthPlanParam.healthPlans) {
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
        upsertHealthPlanParam.drugPlans.forEach {
            mockDrugPlanResult.add(
                UpsertHealthFrequencyResult(
                    id = AppIdUtil.nextId(),
                    type = com.bjknrt.medication.remind.vo.HealthPlanType.valueOf(it.type.name),
                    cycleStartTime = it.cycleStartTime,
                    cycleEndTime = it.cycleEndTime,
                    frequencyIds = listOf(AppIdUtil.nextId())
                )
            )
        }
        Mockito.`when`(
            healthPlanClient.upsertTypeFrequencyHealth(remindFrequencyHealthAllParam1)
        ).thenReturn(
            BatchHealthPlanResult(
                healthPlans = mockHealthPlanResult,
                drugPlans = mockDrugPlanResult
            )
        )
        // 调用添加接口
        manageApi.upsertTypeFrequencyHealth(upsertHealthPlanParam)

        // 校验
        // 根据方案id和计划类型查询健康计划
        val healthPlanResponse = hsHsmHealthPlanTable
            .select()
            .where(HsHsmHealthPlanTable.KnSchemeManagementId.eq(arg(manageId)))
            .where(HsHsmHealthPlanTable.KnPlanType.eq(arg(healthPlans.type.name)))
            .where(HsHsmHealthPlanTable.IsDel.eq(arg(false)))
            .find()
        // 断言
        Assertions.assertEquals(1, healthPlanResponse.size)
        Assertions.assertEquals(mockHealthPlanResult[0].id, healthPlanResponse[0].knForeignPlanId)

        // 根据方案id和类型查询健康计划
        val drugPlanResponse = hsHsmHealthPlanTable
            .select()
            .where(HsHsmHealthPlanTable.KnSchemeManagementId.eq(arg(manageId)))
            .where(HsHsmHealthPlanTable.KnPlanType.eq(arg(drugPlan.type.name)))
            .where(HsHsmHealthPlanTable.IsDel.eq(arg(false)))
            .find()
        // 断言
        Assertions.assertEquals(1, drugPlanResponse.size)
        Assertions.assertEquals(mockDrugPlanResult[0].id, drugPlanResponse[0].knForeignPlanId)
    }

    @Test
    fun addHealthPlanTest() {
        // 患者ID
        val patientId = AppIdUtil.nextId()
        // 方案ID
        val manageId = AppIdUtil.nextId()
        //  准备数据
        prepareData(patientId, manageId)

        // 执行
        val weeks = mutableListOf<Week>();
        weeks.add(Week.SUNDAY)
        // 拼接其他频率参数
        val healthPlan = FrequencyHealthParams(
            name = "测试仅添加健康计划",
            type = HealthPlanType.HYPERTENSION_VISIT,
            patientId = patientId,
            id = null,
            subName = "仅添加",
            desc = null,
            externalKey = null,
            cycleStartTime = LocalDateTime.now(),
            cycleEndTime = null,
            displayTime = LocalDateTime.now(),
            group = null,
            clockDisplay = true,
            frequencys = listOf(
                Frequency(
                    frequencyTime = 4,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 4,
                    frequencyNumUnit = FrequencyNumUnit.WEEKS,
                    frequencyMaxNum = 4,
                    children = Frequency(
                        frequencyTime = 1,
                        frequencyTimeUnit = TimeUnit.WEEKS,
                        frequencyNum = 1,
                        frequencyNumUnit = FrequencyNumUnit.DAYS,
                        frequencyMaxNum = 1
                    ),
                ),
                Frequency(
                    frequencyTime = 1,
                    frequencyTimeUnit = TimeUnit.WEEKS,
                    frequencyNum = 1,
                    frequencyNumUnit = FrequencyNumUnit.DAYS,
                    frequencyMaxNum = 1
                )
            )
        )

        // 准备添加的参数
        val drugPlan = AddDrugRemindParams(
            patientId = patientId,
            drugName = "高血压药品",
            isUsed = true,
            time = LocalTime.now().truncatedTo(ChronoUnit.MINUTES),
            frequencys = weeks,
            type = HealthPlanType.HYPERTENSION_DRUG_PROGRAM,
            cycleStartTime = LocalDateTime.now(),
            cycleEndTime = null
        );
        val upsertHealthPlanParam = AddHealthPlanParam(
            healthPlans = listOf(healthPlan),
            drugPlans = listOf(drugPlan)
        )

        // 模拟添加频率为几周几次类型的健康计划类型
        val remindFrequencyHealthAllParam1 = buildRemindFrequencyHealthAllParam(
            patientId = patientId,
            healthPlans = buildHealthPlanDTO(upsertHealthPlanParam.healthPlans),
            drugPlans = buildDrugPlanDTO(upsertHealthPlanParam.drugPlans)
        )

        // 模拟结果
        val mockHealthPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        val mockDrugPlanResult = mutableListOf<UpsertHealthFrequencyResult>()
        for (it in upsertHealthPlanParam.healthPlans) {
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
        upsertHealthPlanParam.drugPlans.forEach {
            mockDrugPlanResult.add(
                UpsertHealthFrequencyResult(
                    id = AppIdUtil.nextId(),
                    type = com.bjknrt.medication.remind.vo.HealthPlanType.valueOf(it.type.name),
                    cycleStartTime = it.cycleStartTime,
                    cycleEndTime = it.cycleEndTime,
                    frequencyIds = listOf(AppIdUtil.nextId())
                )
            )
        }
        Mockito.`when`(
            healthPlanClient.batchAddHealthPlan(remindFrequencyHealthAllParam1)
        ).thenReturn(
            BatchHealthPlanResult(
                healthPlans = mockHealthPlanResult,
                drugPlans = mockDrugPlanResult
            )
        )
        // 调用添加接口
        manageApi.addHealthPlan(upsertHealthPlanParam)

        // 拦截参数
        val argumentCaptorAdd: ArgumentCaptor<com.bjknrt.medication.remind.vo.FrequencyHealthAllParam> =
            ArgumentCaptor.forClass(com.bjknrt.medication.remind.vo.FrequencyHealthAllParam::class.java)
        Mockito.verify(healthPlanClient).batchAddHealthPlan(capture(argumentCaptorAdd))

        // 校验
        Assertions.assertEquals(remindFrequencyHealthAllParam1.healthPlans.size, argumentCaptorAdd.value.healthPlans.size)
        Assertions.assertEquals(remindFrequencyHealthAllParam1.drugPlans.size, argumentCaptorAdd.value.drugPlans.size)
        // 几周几次
        Assertions.assertEquals(
            remindFrequencyHealthAllParam1.healthPlans[0].name,
            argumentCaptorAdd.value.healthPlans[0].name
        )
        Assertions.assertEquals(
            remindFrequencyHealthAllParam1.healthPlans[0].type,
            argumentCaptorAdd.value.healthPlans[0].type
        )
        Assertions.assertEquals(
            remindFrequencyHealthAllParam1.healthPlans[0].patientId,
            argumentCaptorAdd.value.healthPlans[0].patientId
        )
        Assertions.assertEquals(
            remindFrequencyHealthAllParam1.healthPlans[0].frequencys,
            argumentCaptorAdd.value.healthPlans[0].frequencys
        )
        // 周几几点
        Assertions.assertEquals(
            remindFrequencyHealthAllParam1.drugPlans[0].drugName,
            argumentCaptorAdd.value.drugPlans[0].drugName
        )
        Assertions.assertEquals(
            remindFrequencyHealthAllParam1.drugPlans[0].type,
            argumentCaptorAdd.value.drugPlans[0].type
        )
        Assertions.assertEquals(
            remindFrequencyHealthAllParam1.drugPlans[0].patientId,
            argumentCaptorAdd.value.drugPlans[0].patientId
        )


        // 根据方案id和计划类型查询健康计划
        val healthPlanResponse = hsHsmHealthPlanTable
            .select()
            .where(HsHsmHealthPlanTable.KnSchemeManagementId.eq(arg(manageId)))
            .where(HsHsmHealthPlanTable.KnPlanType.eq(arg(healthPlan.type.name)))
            .where(HsHsmHealthPlanTable.IsDel.eq(arg(false)))
            .find()
        // 断言
        Assertions.assertEquals(mockHealthPlanResult[0].id, healthPlanResponse[0].knForeignPlanId)

        // 根据方案id和类型查询健康计划
        val drugPlanResponse = hsHsmHealthPlanTable
            .select()
            .where(HsHsmHealthPlanTable.KnSchemeManagementId.eq(arg(manageId)))
            .where(HsHsmHealthPlanTable.KnPlanType.eq(arg(drugPlan.type.name)))
            .where(HsHsmHealthPlanTable.IsDel.eq(arg(false)))
            .find()
        // 断言
        Assertions.assertEquals(mockDrugPlanResult[0].id, drugPlanResponse[0].knForeignPlanId)
    }

    /**
     * 创建方案
     */
    private fun prepareData(patientId: BigInteger, manageId: BigInteger) {
        val hsHealthSchemeManagementInfo = HsHealthSchemeManagementInfo.builder()
            .setKnId(manageId)
            .setKnStartDate(LocalDate.now().plusDays(1))
            .setKnCreatedBy(patientId)
            .setKnPatientId(patientId)
            .setKnHealthManageType(HealthManageType.HYPERTENSION.name)
            .setKnDiseaseExistsTag("HYPERTENSION")
            .setKnManagementStage(null)
            .setIsDel(false)
            .setKnCreatedAt(LocalDateTime.now())
            .setKnEndDate(null)
            .setKnJobId(null)
            .setKnReportOutputDate(null)
            .build()
        hsHealthSchemeManagementInfoTable.save(hsHealthSchemeManagementInfo)
    }



}
