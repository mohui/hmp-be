package com.bjknrt.user.permission.centre.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * LoginNoVerifyParam
 * @param name 用户名 
 * @param phone 手机号 
 * @param identityTagList  身份标签
 */
data class LoginNoVerifyParam(
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:JsonProperty("phone", required = true) val phone: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("identityTagList", required = true) val identityTagList: kotlin.collections.Set<IdentityTag>
) {

}

