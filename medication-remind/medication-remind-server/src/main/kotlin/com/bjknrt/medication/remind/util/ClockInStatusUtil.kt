package com.bjknrt.medication.remind.util

import com.bjknrt.medication.remind.entity.FrequencyClockResult

/**
 * 根据频率判断是否发送消息推送, 逻辑和当天打卡是否标灰相同
 * 1. todayFrequency 如果有值, 判断实际打卡次数和要求打卡次数
 * 2. todayFrequency 没有值, 判断 subFrequency 的实际打卡次数和要求打卡次数
 */
fun clockInStatusUtil(todayFrequency: FrequencyClockResult?, subFrequency: FrequencyClockResult?): Boolean {
    return todayFrequency?.let {
        it.actualNum >= it.frequencyMaxNum
    }?: subFrequency?.let {
        it.actualNum >= it.frequencyMaxNum
    }?: false
}