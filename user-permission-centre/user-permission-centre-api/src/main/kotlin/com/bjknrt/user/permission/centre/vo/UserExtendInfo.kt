package com.bjknrt.user.permission.centre.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * UserExtendInfo
 * @param roleIdList  角色id
 * @param identityTagList  身份标签
 * @param orgId  
 * @param regionId  
 * @param deptName  科室信息
 * @param doctorLevel  
 */
data class UserExtendInfo(

    @field:Valid
    @field:JsonProperty("roleIdList") val roleIdList: kotlin.collections.Set<java.math.BigInteger>? = null,

    @field:Valid
    @field:JsonProperty("identityTagList") val identityTagList: kotlin.collections.Set<IdentityTag>? = null,

    @field:Valid
    @field:JsonProperty("orgId") val orgId: java.math.BigInteger? = null,

    @field:Valid
    @field:JsonProperty("regionId") val regionId: java.math.BigInteger? = null,

    @field:JsonProperty("deptName") val deptName: kotlin.String? = null,

    @field:Valid
    @field:JsonProperty("doctorLevel") val doctorLevel: DoctorLevel? = null
) {

}

