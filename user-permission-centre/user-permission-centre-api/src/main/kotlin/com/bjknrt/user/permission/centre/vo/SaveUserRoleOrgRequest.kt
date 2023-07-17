package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param name 用户名 
 * @param loginName 登录名 
 * @param gender  
 * @param phone  手机号
 * @param id  
 * @param address  地址
 * @param extendInfo  
 */
data class SaveUserRoleOrgRequest(
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:JsonProperty("loginName", required = true) val loginName: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("gender", required = true) val gender: Gender,
    
    @field:JsonProperty("phone", required = true) val phone: kotlin.String,

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null,

    @field:JsonProperty("address") val address: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("extendInfo") val extendInfo: UserExtendInfo? = null
) {

}

