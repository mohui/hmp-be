package com.bjknrt.user.permission.centre.vo

import java.util.Objects
import com.bjknrt.framework.api.vo.Id
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
 * 
 * @param id  
 * @param name 角色名称 
 * @param code 角色编码 
 * @param permissionNum 权限数量 
 * @param userNum 用户数量 
 * @param createdAt  
 * @param createdName  创建人
 * @param isUsed  状态:true可用,false:不可用
 */
data class RoleRowInner(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:JsonProperty("code", required = true) val code: kotlin.String,
    
    @field:JsonProperty("permissionNum", required = true) val permissionNum: kotlin.Int,
    
    @field:JsonProperty("userNum", required = true) val userNum: kotlin.Int,
    
    @field:JsonProperty("createdAt", required = true) val createdAt: java.time.LocalDateTime,
    
    @field:JsonProperty("createdName", required = true) val createdName: kotlin.String,
    
    @field:JsonProperty("isUsed", required = true) val isUsed: kotlin.Boolean
) {

}

