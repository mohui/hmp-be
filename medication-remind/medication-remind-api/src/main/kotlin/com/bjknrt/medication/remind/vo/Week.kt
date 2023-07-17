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
* 
* Values: MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY
*/
enum class Week(val value: kotlin.String) {

    /**
     * 周一
     */
    @JsonProperty("MONDAY") MONDAY("MONDAY"),
    
    /**
     * 周二
     */
    @JsonProperty("TUESDAY") TUESDAY("TUESDAY"),
    
    /**
     * 周三
     */
    @JsonProperty("WEDNESDAY") WEDNESDAY("WEDNESDAY"),
    
    /**
     * 周四
     */
    @JsonProperty("THURSDAY") THURSDAY("THURSDAY"),
    
    /**
     * 周五
     */
    @JsonProperty("FRIDAY") FRIDAY("FRIDAY"),
    
    /**
     * 周六
     */
    @JsonProperty("SATURDAY") SATURDAY("SATURDAY"),
    
    /**
     * 周日
     */
    @JsonProperty("SUNDAY") SUNDAY("SUNDAY")
    
}

