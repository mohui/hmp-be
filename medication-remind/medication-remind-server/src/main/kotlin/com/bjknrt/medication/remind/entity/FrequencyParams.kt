package com.bjknrt.medication.remind.entity

import com.bjknrt.medication.remind.vo.FrequencyNumUnit
import com.bjknrt.medication.remind.vo.TimeUnit

/**
 * id: 主键
 * healthPlanId: 主表ID
 * explainId: 上级ID
 * frequencyTime 一周两次 中的 一
 * frequencyTimeUnit 一周两次 中的 周
 * frequencyNum 一周三次 中的 三
 * frequencyNumUnit 一周三次 中的 次
 * frequencyMaxNum 最大打卡次数
 */
class FrequencyParams(
    val id: java.math.BigInteger,
    val healthPlanId: java.math.BigInteger,
    val explainId: java.math.BigInteger?,
    val frequencyTime: kotlin.Int,
    val frequencyTimeUnit: TimeUnit,
    val frequencyNum: kotlin.Int,
    val frequencyNumUnit: FrequencyNumUnit,
    val frequencyMaxNum: Int
) {
}