package com.bjknrt.user.permission.centre.vo

import java.util.Objects
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
 * LoginParam
 * @param loginName 登录名 
 * @param loginPwd 密码 
 */
data class LoginParam(
    
    @get:Pattern(regexp="^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,18}$")
    @get:Size(min=1,max=18)
    @field:JsonProperty("loginName", required = true) val loginName: kotlin.String,
    
    @get:Size(min=6,max=18)
    @field:JsonProperty("loginPwd", required = true) val loginPwd: kotlin.String
) {

}

