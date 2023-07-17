package com.bjknrt.user.permission.centre.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * RoleCodeAndPermissionType
 * @param roleCodes  角色code列表
 * @param types  权限类型列表
 */
data class RoleCodeAndPermissionType(

    @field:JsonProperty("roleCodes") val roleCodes: kotlin.collections.List<kotlin.String>? = null,

    @field:Valid
    @field:JsonProperty("types") val types: kotlin.collections.List<PermissionType>? = null
) {

}

