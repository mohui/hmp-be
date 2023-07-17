package com.bjknrt.medication.remind.api

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.medication.remind.AbstractContainerBaseTest
import com.bjknrt.medication.remind.MrHealthPlanTable
import com.bjknrt.medication.remind.vo.*
import com.bjknrt.security.test.AppSecurityTestUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@TestMethodOrder(MethodOrderer.MethodName::class)
class MedicationRemindTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: MedicationRemindApi

    @Autowired
    lateinit var mrHealthPlanTable: MrHealthPlanTable

    val patientId = 100000.toBigInteger()

    @BeforeEach
    fun before() {
        AppSecurityTestUtil.setCurrentUserInfo(
            userId = patientId,
        )
    }

    /**
     * To test MedicationRemindApi.upsert
     */
    @Test
    fun interfaceTest() {
        // 测试 添加接口
        // 当前日期时间
        val currentTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES)

        var weeks = mutableListOf<Week>();
        weeks.add(Week.SUNDAY);

        val upsertRequest: UpsertParams = UpsertParams("阿莫西林", currentTime, weeks)
        val response: kotlin.Boolean = api.upsert(upsertRequest)
        assertEquals(true, response)


        // 测试 列表接口
        weeks = mutableListOf<Week>();
        weeks.add(Week.SUNDAY);


        val str: Inner = Inner(1.toBigInteger(), "阿莫西林", currentTime, true, weeks)

        val list = mutableListOf<Inner>();
        list.add(str)

        val responseList: List<Inner> = api.list()
        assertEquals(1, responseList.size)
        assertEquals("阿莫西林", responseList[0].drugName)
        assertEquals(currentTime, responseList[0].time)
        assertEquals(weeks, responseList[0].weeks)

        // 测试 修改接口
        weeks = mutableListOf<Week>();
        weeks.add(Week.SUNDAY);

        val upsertRequest2: UpsertParams = UpsertParams("阿莫西林", currentTime, weeks, 1.toBigInteger())
        val responseUpdate: kotlin.Boolean = api.upsert(upsertRequest2)
        assertEquals(true, responseUpdate)

        // 测试 修改状态接口
        val updStatusRequest: UpdStatusParams = UpdStatusParams(1.toBigInteger(), false)
        val responseUpdStatus: kotlin.Boolean = api.updStatus(updStatusRequest)
        assertEquals(true, responseUpdStatus)

        // 测试批量修改状态接口
        val updBatchStatusParams = UpdBatchStatusParams(
            id = listOf(1.toBigInteger()),
            status = true
        )
        api.updBatchStatus(updBatchStatusParams)
        val findOneHealthPlanTable = mrHealthPlanTable.findByKnId(1.toBigInteger())
        assertEquals(true, findOneHealthPlanTable?.isUsed == true)

        // 测试 删除接口
        val id: Id = 1.toBigInteger()
        val responseDelete: kotlin.Boolean = api.del(id)
        assertEquals(true, responseDelete)

        val upsertRequest1: UpsertParams = UpsertParams("阿莫西林", currentTime, weeks, id = 111.toBigInteger())
        api.upsert(upsertRequest1)

        api.batchDel(BatchDelParam(patientId = patientId, type = HealthPlanType.DRUG))

        val findOneHealthPlanTable1 = mrHealthPlanTable.findByKnId(111.toBigInteger())
        assertEquals(true, findOneHealthPlanTable1?.isDel == true)
    }
}
