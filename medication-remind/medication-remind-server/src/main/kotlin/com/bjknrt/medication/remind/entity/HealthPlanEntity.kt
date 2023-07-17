package com.bjknrt.medication.remind.entity

import com.bjknrt.medication.remind.vo.HealthPlanType
import java.time.LocalDateTime

data class HealthPlanEntity(

    /**
     * 主键ID
     */
    val id: java.math.BigInteger,

    /**
     * 患者ID
     */
    val patientId: java.math.BigInteger,

    /**
     * 计划名称
     */
    val name: String,

    /**
     * 计划类型
     */
    val type: HealthPlanType,

    val clockIn: Boolean,

    /**
     * 打卡完成后是否显示
     */
    val isClockDisplay: Boolean,

    /**
     * 状态: true: 可用, false: 不可用
     */
    val isUsed: Boolean,

    /**
     * 周期开始时间
     */
    var cycleStartTime: LocalDateTime,

    /**
     * 周一
     */
    val isMonday: Boolean,

    /**
     * 周二
     */
    val isTuesday: Boolean,

    /**
     * 周三
     */
    val isWednesday: Boolean,

    /**
     * 周四
     */
    val isThursday: Boolean,

    /**
     * 周五
     */
    val isFriday: Boolean,

    /**
     * 周六
     */
    val isSaturday: Boolean,

    /**
     * 周日
     */
    val isSunday: Boolean,

    /**
     * 是否显示
     */
    var display: Boolean,

    /**
     * 是否打卡完成
     */
    var isCycleClockIn: Boolean,

    /**
     * 计划名称-标题
     */
    val subName: String? = null,

    /**
     * 计划描述
     */
    var desc: String? = null,

    /**
     * 时间
     */
    val time: java.time.LocalTime?,

    /**
     * 周期结束时间
     */
    var cycleEndTime: LocalDateTime? = null,

    /**
     * 显示时间
     */
    var displayTime: LocalDateTime,

    /**
     * 饮食计划
     */
    var externalKey: String? = null,

    /**
     * 分钟
     */
    var group: String? = null,

    /**
     * 频率
     */
    var frequency: List<HealthPlanRule>? = null,

    var todayFrequency: FrequencyClockResult? = null,

    var subFrequency: FrequencyClockResult? = null,

    var mainFrequency: FrequencyClockResult? = null,

    ) {

}