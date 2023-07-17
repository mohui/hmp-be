package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param id  
 * @param name 权限名称 
 * @param code 权限编码 
 * @param type  
 */
data class Permission(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("code", required = true) val code: PermissionEnum,
    
    @field:Valid
    @field:JsonProperty("type", required = true) val type: PermissionType = PermissionType.PAGE
) {

}

