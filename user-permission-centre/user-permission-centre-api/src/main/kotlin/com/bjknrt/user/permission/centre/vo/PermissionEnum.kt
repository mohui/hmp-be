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
* 
* Values: HOME,USER_MANAGE,ALL_PATIENT,MY_PATIENT,ROLE_MANAGE,HEALTHY_ARTICLE,OPERATION_LOG,SERVICE_MANAGE
*/
enum class PermissionEnum(val value: kotlin.String) {

    /**
     * 首页
     */
    @JsonProperty("HOME") HOME("HOME"),
    
    /**
     * 用户管理
     */
    @JsonProperty("USER_MANAGE") USER_MANAGE("USER_MANAGE"),
    
    /**
     * 所有患者
     */
    @JsonProperty("ALL_PATIENT") ALL_PATIENT("ALL_PATIENT"),
    
    /**
     * 我的患者
     */
    @JsonProperty("MY_PATIENT") MY_PATIENT("MY_PATIENT"),
    
    /**
     * 角色管理
     */
    @JsonProperty("ROLE_MANAGE") ROLE_MANAGE("ROLE_MANAGE"),
    
    /**
     * 健康科普
     */
    @JsonProperty("HEALTHY_ARTICLE") HEALTHY_ARTICLE("HEALTHY_ARTICLE"),
    
    /**
     * 操作日志
     */
    @JsonProperty("OPERATION_LOG") OPERATION_LOG("OPERATION_LOG"),
    
    /**
     * 服务管理
     */
    @JsonProperty("SERVICE_MANAGE") SERVICE_MANAGE("SERVICE_MANAGE")
    
}

