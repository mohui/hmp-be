package com.bjknrt.medication.remind.entity

import com.bjknrt.medication.remind.vo.HealthPlanType
import java.time.LocalDateTime

data class DrugHealthPlanDTO(

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

    /**
     * 打卡完成后是否显示
     */
    val isClockDisplay: Boolean,

    /**
     * 状态: true: 可用, false: 不可用
     */
    val isUsed: Boolean,

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
     * 创建时间
     */
    var createdAt: LocalDateTime,

    /**
     * 修改时间
     */
    var updatedAt: LocalDateTime,

    /**
     * 创建人
     */
    var createdBy: java.math.BigInteger? = null,

    /**
     * 修改人
     */
    var updatedBy: java.math.BigInteger? = null,

    /**
     * 计划开始时间
     */
    var cycleStartTime: LocalDateTime? = null,

    /**
     * 计划结束时间
     */
    var cycleEndTime: LocalDateTime? = null,

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
     * 显示时间
     */
    var displayTime: LocalDateTime,

    /**
     * 饮食计划
     */
    var externalKey: String? = null,

    /**
     * 分组
     */
    var group: String? = null
    ) {
}