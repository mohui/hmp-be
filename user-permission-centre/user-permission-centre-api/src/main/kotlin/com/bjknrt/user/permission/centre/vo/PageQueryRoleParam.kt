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
 * PageQueryRoleParam
 * @param pageNo  页码
 * @param pageSize  每页条数
 * @param name  角色名称
 * @param createdName  创建人
 * @param createdStartAt  
 * @param createdEndAt  
 * @param status  状态
 */
data class PageQueryRoleParam(
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,

    @field:JsonProperty("name") val name: kotlin.String? = null,

    @field:JsonProperty("createdName") val createdName: kotlin.String? = null,

    @field:JsonProperty("createdStartAt") val createdStartAt: java.time.LocalDateTime? = null,

    @field:JsonProperty("createdEndAt") val createdEndAt: java.time.LocalDateTime? = null,

    @field:JsonProperty("status") val status: kotlin.Boolean? = null
) {

}

