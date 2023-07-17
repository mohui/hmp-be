package com.bjknrt.security

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger
import java.util.*

const val SUPER_ADMIN_ROLE_CODE = "SUPER_ADMIN"

data class JwtUserDetails(
    @JsonProperty("id")
    val id: BigInteger, // jwt id
    @JsonProperty("issuedAt")
    val issuedAt: Date,
    @JsonProperty("notBefore")
    val notBefore: Date,
    @JsonProperty("expiration")
    var expiration: Date,
    @JsonProperty("userId")
    val userId: BigInteger,
    @JsonProperty("nickName")
    val nickName: String,
    @JsonProperty("enabled")
    val enabled: Boolean,
    @JsonProperty("loginName")
    val loginName: String,
) {

    @JsonIgnore
    var loginPwd: String? = null

    @JsonIgnore
    var extendUserDetailsObtainHandler: JwtExtendUserDetailsObtainHandler = emptyJwtExtendUserDetailsObtainHandler

    @get:JsonIgnore
    val extendInfo: ExtendUserDetails by lazy { extendUserDetailsObtainHandler.getExtendInfo(userId) }

    @get:JsonIgnore
    val identityTagSet: Set<String> by lazy { extendInfo.identityTagSet }

    @get:JsonIgnore
    val roleCodeSet: Set<String> by lazy { extendInfo.roleCodeSet }

    @get:JsonIgnore
    val orgIdSet: Set<BigInteger> by lazy { extendInfo.orgIdSet }

    @get:JsonIgnore
    val regionIdSet: Set<BigInteger> by lazy { extendInfo.regionIdSet }

    @get:JsonIgnore
    val permissionCodeSet: Set<String> by lazy { extendInfo.permissionCodeSet }

    /**
     * 当前拥有的数据权限id，具体机构和区域id
     */
    @get:JsonIgnore
    val currentOrgId: BigInteger? by lazy { extendInfo.currentOrgId }

    @JsonIgnore
    fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    fun isAccountNonLocked(): Boolean {
        return enabled
    }

    // JWT 解析是会验证过期时间 此处不再重复验证
    @JsonIgnore
    fun isCredentialsNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    fun isSuperAdmin(): Boolean {
        return roleCodeSet.find { it == SUPER_ADMIN_ROLE_CODE } != null
    }

}