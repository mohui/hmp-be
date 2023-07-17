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
 * @param phone 手机号 
 * @param gender  
 * @param createdAt  
 * @param updatedAt  
 * @param email 邮箱 
 * @param profilePic 头像 
 * @param birthday  
 * @param idCard 省份证号 
 * @param extends 额外信息 
 * @param address 地址 
 * @param createdBy  
 * @param createdName  创建人姓名
 * @param updatedBy  
 * @param updatedName  修改人姓名
 */
data class ListByIdsParamInner(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @get:Pattern(regexp="^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,12}$")
    @get:Size(min=1,max=12)
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @get:Pattern(regexp="^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,18}$")
    @get:Size(min=1,max=18)
    @field:JsonProperty("loginName", required = true) val loginName: kotlin.String,
    
    @get:Pattern(regexp="^1[34578]\\d{9}$")
    @get:Size(min=11,max=11)
    @field:JsonProperty("phone", required = true) val phone: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,
    
    @field:JsonProperty("updatedAt", required = true) val updatedAt: java.time.LocalDateTime,

    @field:JsonProperty("email") val email: kotlin.String? = null,

    @field:JsonProperty("profilePic") val profilePic: kotlin.String? = null,

    @field:JsonProperty("birthday") val birthday: java.time.LocalDateTime? = null,

    @field:JsonProperty("idCard") val idCard: kotlin.String? = null,

    @field:JsonProperty("extends") val extends: kotlin.String? = null,

    @field:JsonProperty("address") val address: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("createdBy") val createdBy: Id? = null,

    @field:JsonProperty("createdName") val createdName: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("updatedBy") val updatedBy: Id? = null,

    @field:JsonProperty("updatedName") val updatedName: kotlin.String? = null
) {

}

