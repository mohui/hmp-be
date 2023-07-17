package com.bjknrt.health.scheme.util

import com.bjknrt.medication.remind.api.HealthPlanApi
import com.bjknrt.medication.remind.vo.DateTimeGetClockInParams
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

@Component
class ClockInRateUtils(
    val healthPlanClient: HealthPlanApi
) {

    /**
     * 抗阻运动打卡率得分
     *  打卡率 ＜ 25%（0分）
     *  25%≤打卡率＜75%（2分）
     *  打卡率≥75%（5分）
     * @param planIdList 健康计划Id集合
     * @param expectedClockInNumber 期望打卡次数
     * @param healthManageStartDate 健康方案开始时间
     * @param healthManageEndDate 健康方案结束时间
     */
    fun getResistanceExerciseRateScore(
        planIdList: List<BigInteger>,
        expectedClockInNumber: Int,
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime,
    ): Int {
        val clockInNumber = getClockInNumber(planIdList, healthManageStartDate, healthManageEndDate)

        return when (getRate(BigDecimal(clockInNumber), BigDecimal(expectedClockInNumber))) {
            in 0 until 25 -> 0
            in 25 until 75 -> 2
            else -> 5
        }
    }

    /**
     * 有氧运动打卡率得分
     *  打卡率 ＜ 25%（0分）
     *  25%≤打卡率＜75%（2分）
     *  打卡率≥75%（5分）
     * @param planIdList 健康计划Id集合
     * @param expectedClockInNumber 期望打卡次数
     * @param healthManageStartDate 健康方案开始时间
     * @param healthManageEndDate 健康方案结束时间
     */
    fun getCardioPunchRateScore(
        planIdList: List<BigInteger>,
        expectedClockInNumber: Int,
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime
    ): Int {
        val clockInNumber = getClockInNumber(planIdList, healthManageStartDate, healthManageEndDate)

        return when (getRate(BigDecimal(clockInNumber), BigDecimal(expectedClockInNumber))) {
            in 0 until 25 -> 0
            in 25 until 75 -> 2
            else -> 5
        }
    }

    /**
     * 饮食打卡率得分
     *  打卡率 ＜ 25%（0分）
     *  25%≤打卡率＜75%（5分）
     *  打卡率≥75%（10分）
     * @param planIdList 健康计划Id集合
     * @param expectedClockInNumber 期望打卡次数
     * @param healthManageStartDate 健康方案开始时间
     * @param healthManageEndDate 健康方案结束时间
     */
    fun getDietPunchRateScore(
        planIdList: List<BigInteger>,
        expectedClockInNumber: Int,
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime
    ): Int {
        val clockInNumber = getClockInNumber(planIdList, healthManageStartDate, healthManageEndDate)

        return when (getRate(BigDecimal(clockInNumber), BigDecimal(expectedClockInNumber))) {
            in 0 until 25 -> 0
            in 25 until 75 -> 5
            else -> 10
        }
    }


    /**
     * 呼吸操打卡率得分
     *  打卡率 ＜ 25%（0分）
     *  25%≤打卡率＜75%（5分）
     *  打卡率≥75%（10分）
     * @param planIdList 健康计划Id集合
     * @param expectedClockInNumber 期望打卡次数
     * @param healthManageStartDate 健康方案开始时间
     * @param healthManageEndDate 健康方案结束时间
     */
    fun getRespiratoryRateScore(
        planIdList: List<BigInteger>,
        expectedClockInNumber: Int,
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime
    ): Int {
        val clockInNumber = getClockInNumber(planIdList, healthManageStartDate, healthManageEndDate)

        return when (getRate(BigDecimal(clockInNumber), BigDecimal(expectedClockInNumber))) {
            in 0 until 25 -> 0
            in 25 until 75 -> 5
            else -> 10
        }
    }


    /**
     * 每日科普打卡率得分
     *  打卡率 ＜ 25%（0分）
     *  25%≤打卡率＜75%（5分）
     *  打卡率≥75%（10分）
     * @param planIdList 健康计划Id集合
     * @param expectedClockInNumber 期望打卡次数
     * @param healthManageStartDate 健康方案开始时间
     * @param healthManageEndDate 健康方案结束时间
     */
    fun getPopularScienceRateScore(
        planIdList: List<BigInteger>,
        expectedClockInNumber: Int,
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime
    ): Int {
        val clockInNumber = getClockInNumber(planIdList, healthManageStartDate, healthManageEndDate)

        return when (getRate(BigDecimal(clockInNumber), BigDecimal(expectedClockInNumber))) {
            in 0 until 25 -> 0
            in 25 until 75 -> 5
            else -> 10
        }
    }


    /**
     * 获取打卡次数
     * @param planIdList 健康计划Id集合
     * @param healthManageStartDate 健康方案开始时间
     * @param healthManageEndDate 健康方案结束时间
     */
    fun getClockInNumber(
        planIdList: List<BigInteger>,
        healthManageStartDate: LocalDateTime,
        healthManageEndDate: LocalDateTime
    ): Int {
        return planIdList.map { foreignPlanId ->
            getClockNumber(
                foreignPlanId,
                healthManageStartDate,
                healthManageEndDate
            )
        }
            .fold(0) { acc, item -> acc + item }
    }

    /**
     * 获取打卡次数
     * @param foreignPlanId 打卡时间
     * @param startTime 健康方案开始时间
     * @param endTime 健康方案结束时间
     * @return 打卡次数
     */
    fun getClockNumber(
        foreignPlanId: BigInteger,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Int {
        //获取打卡次数
        return healthPlanClient.dateTimeGetClockIn(
            DateTimeGetClockInParams(
                healthPlanId = foreignPlanId,
                startTime = startTime,
                endTime = endTime
            )
        )
    }

    /**
     * 打卡率得分
     */
    fun getRateScore(
        rate: Int,
        action: (rate: Int) -> Int
    ): Int {
        return action(rate)
    }
}
