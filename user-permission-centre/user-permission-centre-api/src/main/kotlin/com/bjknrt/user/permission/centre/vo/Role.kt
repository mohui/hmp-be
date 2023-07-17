package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param id  
 * @param name 角色名称 
 * @param code 角色编码 
 * @param permissions  
 */
data class Role(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:JsonProperty("code", required = true) val code: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("permissions", required = true) val permissions: kotlin.collections.List<PermissionEnum>
) {

}

