package com.bjknrt.medication.remind.service

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.medication.remind.MrHealthPlan
import com.bjknrt.medication.remind.entity.*
import com.bjknrt.medication.remind.vo.*
import me.danwi.sqlex.core.type.PagedResult
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

interface HealthPlanService {
    // 健康计划添加修改
    fun upsertHealth(frequencyHealthParams: FrequencyHealthParams): MrHealthPlan?

    // 打卡
    fun clockIn(id: BigInteger, clockAt: LocalDateTime? = null)

    // 打卡历史记录
    fun clockInHistory(clockInHistoryParam: ClockInHistoryParam): List<ClockInHistoryResult>

    // 健康计划规则添加修改
    fun upsertFrequency(frequency: List<FrequencyParams>): List<Id>

    // 通过患者ID和type获取健康计划id列表
    fun typeGetHealthIds(patientId: Id, type: HealthPlanType): List<Id>

    // 获取健康计划今日打卡情况列表
    fun getHealthPlanTodayClockList(patientId: Id): List<PlanClockResult>

    // 获取计算周期开始时间结束时间和已过去的周期数
    fun calculationCycle(
        chronoNum: Int,
        chronoUnit: ChronoUnit,
        startDateTime: LocalDateTime,
        now: LocalDateTime = LocalDateTime.now()
    ): CalculationCycleResult

    // 通过健康计划id列表获取其规则
    fun getHealthPlanFrequency(ids: List<Id>): Map<Id, List<HealthPlanRule>>

    // 统计打分
    fun getClockIn(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        healthPlanId: Id,
        frequency: HealthPlanRule,
        now: LocalDateTime = LocalDateTime.now()
    ): FrequencyClockResult

    fun listSort(planList: List<PlanClockResult>): List<PlanClockResult>

    fun patientIdGetHealthPlanList(patientId: Id): List<HealthPlanEntity>

    // 根据主键ID数组获取健康计划
    fun idsGetHealthPlanList(ids: List<Id>): List<HealthPlanEntity>

    fun arrangementHealthPlanList(
        healthPlanModels: List<HealthPlanEntity>,
        patientId: Id? = null
    ): List<HealthPlanEntity>

    /**
     * 查询拥有健康计划的人员id
     */
    fun pagePatientIds(pageNo: Long, pageSize: Long): PagedResult<BigInteger>

    /**
     * 根据开始时间和结束时间, 健康计划ID获取打卡次数
     */
    fun dateTimeGetClockIn(dateTimeGetClockInParams: DateTimeGetClockInParams): Int
}