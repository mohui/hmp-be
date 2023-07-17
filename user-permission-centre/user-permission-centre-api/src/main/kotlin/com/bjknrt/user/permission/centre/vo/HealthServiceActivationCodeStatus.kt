package com.bjknrt.user.permission.centre.vo

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
* 激活码状态
* Values: DEFINED,USED,FORBIDDEN
*/
enum class HealthServiceActivationCodeStatus(val value: kotlin.String) {

    /**
     * 待使用
     */
    @JsonProperty("DEFINED") DEFINED("DEFINED"),
    
    /**
     * 已使用
     */
    @JsonProperty("USED") USED("USED"),
    
    /**
     * 已禁用
     */
    @JsonProperty("FORBIDDEN") FORBIDDEN("FORBIDDEN")
    
}

