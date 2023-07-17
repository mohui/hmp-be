package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * roleDetailResult
 * @param id  
 * @param name  角色名称
 * @param code  角色code
 * @param isUsed  是否可用
 * @param permission  菜单权限
 */
data class RoleDetailResult(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:JsonProperty("code", required = true) val code: kotlin.String,
    
    @field:JsonProperty("isUsed", required = true) val isUsed: kotlin.Boolean,
    
    @field:Valid
    @field:JsonProperty("permission", required = true) val permission: kotlin.collections.List<PermissionEnum>
) {

}

