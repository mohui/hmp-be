package com.bjknrt.medication.remind.entity

import java.time.LocalDateTime

/**
 * start: 开始时间
 * end: 结束时间
 * clockIn: 实际有效打卡数量
 * clockInStatus: 是否符合打卡需求
 */
data class ClockDayList(
    val start: LocalDateTime,
    val end: LocalDateTime,
    var clockIn: Int = 0,
    var clockInStatus: Boolean = false
) {
}