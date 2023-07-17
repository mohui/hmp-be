package com.bjknrt.user.permission.centre.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * PageQueryUserParam
 * @param pageNo  页码
 * @param pageSize  每页条数
 * @param roleIds  角色Id
 * @param orgIds  机构Id
 * @param regionIds  区域id
 * @param name  姓名
 * @param identityTagList  身份标签
 * @param createdBy  创建人
 * @param createdAtStart  
 * @param createdAtEnd  
 */
data class PageQueryUserParam(
    
    @field:JsonProperty("pageNo", required = true) val pageNo: kotlin.Long,
    
    @field:JsonProperty("pageSize", required = true) val pageSize: kotlin.Long,

    @field:Valid
    @field:JsonProperty("roleIds") val roleIds: kotlin.collections.Set<java.math.BigInteger>? = null,

    @field:Valid
    @field:JsonProperty("orgIds") val orgIds: kotlin.collections.Set<java.math.BigInteger>? = null,

    @field:Valid
    @field:JsonProperty("regionIds") val regionIds: kotlin.collections.Set<java.math.BigInteger>? = null,

    @field:JsonProperty("name") val name: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("identityTagList") val identityTagList: kotlin.collections.Set<IdentityTag>? = null,

    @field:JsonProperty("createdBy") val createdBy: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("createdAtStart") val createdAtStart: java.time.LocalDate? = null,

    @field:Valid
    @field:JsonProperty("createdAtEnd") val createdAtEnd: java.time.LocalDate? = null
) {

}

