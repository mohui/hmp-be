package com.bjknrt.user.permission.centre.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

/**
 * RegisterParam
 * @param name 用户名 
 * @param loginName 登录名 
 * @param loginPwd 密码 
 * @param phone 手机号 
 * @param gender  
 * @param email 邮箱 
 * @param profilePic 头像 
 * @param birthday  
 * @param idCard 省份证号 
 * @param extends 额外信息 
 * @param address 地址 
 */
data class RegisterParam(
    
    @get:Pattern(regexp="^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,12}$")
    @get:Size(min=1,max=12)
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @get:Pattern(regexp="^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,18}$")
    @get:Size(min=1,max=18)
    @field:JsonProperty("loginName", required = true) val loginName: kotlin.String,
    
    @get:Size(min=6,max=18)
    @field:JsonProperty("loginPwd", required = true) val loginPwd: kotlin.String,
    
    @get:Pattern(regexp="^1[34578]\\d{9}$")
    @get:Size(min=11,max=11)
    @field:JsonProperty("phone", required = true) val phone: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,

    @field:JsonProperty("email") val email: kotlin.String? = null,

    @field:JsonProperty("profilePic") val profilePic: kotlin.String? = null,

    @field:JsonProperty("birthday") val birthday: java.time.LocalDateTime? = null,

    @field:JsonProperty("idCard") val idCard: kotlin.String? = null,

    @field:JsonProperty("extends") val extends: kotlin.String? = null,

    @field:JsonProperty("address") val address: kotlin.String? = null
) {

}

