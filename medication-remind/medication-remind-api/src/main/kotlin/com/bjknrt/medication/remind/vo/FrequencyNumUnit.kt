package com.bjknrt.medication.remind.vo

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

/**
* Java的ChronoUnit类纳秒到十年,加次
* Values: NANOS,MICROS,MILLIS,SECONDS,MINUTES,HOURS,HALF_DAYS,DAYS,WEEKS,MONTHS,YEARS,SEQUENCE
*/
enum class FrequencyNumUnit(val value: kotlin.String) {

    /**
     * 纳秒
     */
    @JsonProperty("NANOS") NANOS("NANOS"),
    
    /**
     * 微秒
     */
    @JsonProperty("MICROS") MICROS("MICROS"),
    
    /**
     * 毫秒
     */
    @JsonProperty("MILLIS") MILLIS("MILLIS"),
    
    /**
     * 秒
     */
    @JsonProperty("SECONDS") SECONDS("SECONDS"),
    
    /**
     * 分钟
     */
    @JsonProperty("MINUTES") MINUTES("MINUTES"),
    
    /**
     * 小时
     */
    @JsonProperty("HOURS") HOURS("HOURS"),
    
    /**
     * 半天
     */
    @JsonProperty("HALF_DAYS") HALF_DAYS("HALF_DAYS"),
    
    /**
     * 天
     */
    @JsonProperty("DAYS") DAYS("DAYS"),
    
    /**
     * 周
     */
    @JsonProperty("WEEKS") WEEKS("WEEKS"),
    
    /**
     * 月
     */
    @JsonProperty("MONTHS") MONTHS("MONTHS"),
    
    /**
     * 年
     */
    @JsonProperty("YEARS") YEARS("YEARS"),
    
    /**
     * 次
     */
    @JsonProperty("SEQUENCE") SEQUENCE("SEQUENCE")
    
}

