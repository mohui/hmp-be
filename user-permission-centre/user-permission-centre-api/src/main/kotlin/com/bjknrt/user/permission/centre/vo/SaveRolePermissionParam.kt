package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Size
import javax.validation.Valid

/**
 * SaveRolePermissionParam
 * @param name 角色名称 
 * @param permissions Permission 权限列表
 * @param id  
 */
data class SaveRolePermissionParam(
    
    @get:Size(min=1)
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("permissions", required = true) val permissions: kotlin.collections.List<PermissionEnum>,

    @field:Valid
    @field:JsonProperty("id") val id: Id? = null
) {

}

