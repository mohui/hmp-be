package com.bjknrt.user.permission.centre.vo

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

/**
 * UserPermissions
 * @param roleCodeSet  角色编码
 * @param orgIdSet  机构Id
 * @param regionIdSet  区域Id
 * @param permissionCodeSet  权限编码
 * @param identityTagSet  身份标签
 */
data class UserPermissions(
    
    @field:JsonProperty("roleCodeSet", required = true) val roleCodeSet: kotlin.collections.Set<kotlin.String>,
    
    @field:Valid
    @field:JsonProperty("orgIdSet", required = true) val orgIdSet: kotlin.collections.Set<java.math.BigInteger>,
    
    @field:Valid
    @field:JsonProperty("regionIdSet", required = true) val regionIdSet: kotlin.collections.Set<java.math.BigInteger>,
    
    @field:JsonProperty("permissionCodeSet", required = true) val permissionCodeSet: kotlin.collections.Set<kotlin.String>,
    
    @field:JsonProperty("identityTagSet", required = true) val identityTagSet: kotlin.collections.Set<kotlin.String>
) {

}

