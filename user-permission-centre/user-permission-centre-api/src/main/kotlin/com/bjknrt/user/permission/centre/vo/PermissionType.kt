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
* 权限类型
* Values: PAGE,URL,API
*/
enum class PermissionType(val value: kotlin.String) {

    /**
     * 页面权限
     */
    @JsonProperty("PAGE") PAGE("PAGE"),
    
    /**
     * 路径权限
     */
    @JsonProperty("URL") URL("URL"),
    
    /**
     * 接口权限
     */
    @JsonProperty("API") API("API")
    
}

