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
* 性别（MAN-男，WOMAN-女,UNKNOWN-未知）
* Values: MAN,WOMAN,UNKNOWN
*/
enum class Gender(val value: kotlin.String) {

    /**
     * 男
     */
    @JsonProperty("MAN") MAN("MAN"),
    
    /**
     * 女
     */
    @JsonProperty("WOMAN") WOMAN("WOMAN"),
    
    /**
     * 未知
     */
    @JsonProperty("UNKNOWN") UNKNOWN("UNKNOWN")
    
}

