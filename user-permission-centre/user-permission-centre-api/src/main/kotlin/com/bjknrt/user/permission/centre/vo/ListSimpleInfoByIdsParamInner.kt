package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

/**
 * 
 * @param id  
 * @param name 用户名 
 * @param loginName 登录名 
 * @param gender  
 * @param email 邮箱 
 * @param profilePic 头像 
 * @param birthday  
 * @param extends 额外信息 
 * @param address 地址 
 */
data class ListSimpleInfoByIdsParamInner(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @get:Pattern(regexp="^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,12}$")
    @get:Size(min=1,max=12)
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @get:Pattern(regexp="^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,18}$")
    @get:Size(min=1,max=18)
    @field:JsonProperty("loginName", required = true) val loginName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,

    @field:JsonProperty("email") val email: kotlin.String? = null,

    @field:JsonProperty("profilePic") val profilePic: kotlin.String? = null,

    @field:JsonProperty("birthday") val birthday: java.time.LocalDateTime? = null,

    @field:JsonProperty("extends") val extends: kotlin.String? = null,

    @field:JsonProperty("address") val address: kotlin.String? = null
) {

}

