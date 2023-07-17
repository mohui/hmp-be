package com.bjknrt.medication.remind.entity

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class HealthPlanRule(

    val id: Id,

    val frequencyTime: kotlin.Int,

    val frequencyTimeUnit: TimeServiceUnit,

    val frequencyNum: kotlin.Int,

    val frequencyNumUnit: TimeServiceUnit,

    val frequencyMaxNum: Int,

    var children: HealthPlanRule? = null
) {

    companion object {
        val EMPTY = HealthPlanRule(
            id = BigInteger.ZERO,
            frequencyTime = 0,
            frequencyTimeUnit = TimeServiceUnit.DAYS,
            frequencyNum = 0,
            frequencyNumUnit = TimeServiceUnit.DAYS,
            frequencyMaxNum = 0
        )
    }

}


/**
 * Java的ChronoUnit类纳秒到十年,加次
 * Values: NANOS,MICROS,MILLIS,SECONDS,MINUTES,HOURS,HALF_DAYS,DAYS,WEEKS,MONTHS,YEARS,DECADES,SEQUENCE
 */
enum class TimeServiceUnit(val value: kotlin.String) {

    /**
     * 纳秒
     */
    @JsonProperty("NANOS")
    NANOS("NANOS"),

    /**
     * 微秒
     */
    @JsonProperty("MICROS")
    MICROS("MICROS"),

    /**
     * 毫秒
     */
    @JsonProperty("MILLIS")
    MILLIS("MILLIS"),

    /**
     * 秒
     */
    @JsonProperty("SECONDS")
    SECONDS("SECONDS"),

    /**
     * 分钟
     */
    @JsonProperty("MINUTES")
    MINUTES("MINUTES"),

    /**
     * 小时
     */
    @JsonProperty("HOURS")
    HOURS("HOURS"),

    /**
     * 半天
     */
    @JsonProperty("HALF_DAYS")
    HALF_DAYS("HALF_DAYS"),

    /**
     * 天
     */
    @JsonProperty("DAYS")
    DAYS("DAYS"),

    /**
     * 周
     */
    @JsonProperty("WEEKS")
    WEEKS("WEEKS"),

    /**
     * 月
     */
    @JsonProperty("MONTHS")
    MONTHS("MONTHS"),

    /**
     * 年
     */
    @JsonProperty("YEARS")
    YEARS("YEARS"),

    /**
     * 次
     */
    @JsonProperty("SEQUENCE")
    SEQUENCE("SEQUENCE")

}