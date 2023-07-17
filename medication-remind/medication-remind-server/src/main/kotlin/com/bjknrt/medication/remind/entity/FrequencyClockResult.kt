package com.bjknrt.medication.remind.entity

import java.time.LocalDateTime

/**
 * frequencyTime 1周3次中的1
 * frequencyTimeUnit 1周3次中的周
 * frequencyNum 1周3次中的3
 * frequencyNumUnit 1周3次中的次
 * frequencyMaxNum 最大打卡次数
 * actualNum: 有效打卡次数
 */
data class FrequencyClockResult (

    var frequencyTime: kotlin.Int,

    var frequencyTimeUnit: TimeServiceUnit,

    var frequencyNum: kotlin.Int,

    var frequencyNumUnit: TimeServiceUnit,

    var frequencyMaxNum: Int,

    var actualNum: kotlin.Int,

    var cycleStartTime: LocalDateTime,

    var cycleEndTime: LocalDateTime,
) {

}