package com.bjknrt.medication.remind.entity

import com.bjknrt.medication.remind.vo.HealthPlanType
import java.time.LocalDateTime

/**
 * 健康计划今日打卡返回值
 */
data class PlanClockResult(

    val id: java.math.BigInteger,

    val name: kotlin.String,

    val type: HealthPlanType,

    val clockIn: kotlin.Boolean,

    val isClockDisplay: Boolean,

    val subName: kotlin.String? = null,

    var desc: kotlin.String? = null,

    val time: java.time.LocalTime?,

    var cycleStartTime: LocalDateTime,

    var cycleEndTime: LocalDateTime?,

    var externalKey: String?,

    var group: String? = null,

    var frequency: List<HealthPlanRule>?,

    var todayFrequency: FrequencyClockResult? = null,

    var subFrequency: FrequencyClockResult? = null,

    var mainFrequency: FrequencyClockResult? = null

    ) {

}