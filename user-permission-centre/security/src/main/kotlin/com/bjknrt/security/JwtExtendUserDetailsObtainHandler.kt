package com.bjknrt.security

import java.math.BigInteger

/**
 * 用户扩展信息获取处理器
 */
interface JwtExtendUserDetailsObtainHandler {

    fun getExtendInfo(userId: BigInteger): ExtendUserDetails

}

interface ExtendUserDetails {
    /**
     * 角色CODE集合
     */
    val roleCodeSet: Set<String>

    /**
     * 身份标签
     */
    val identityTagSet: Set<String>

    /**
     * 机构ID集合：具体机构实体集合
     */
    val orgIdSet: Set<BigInteger>

    /**
     * 行政区域ID集合：以行政区域为组的形式管理具体机构
     */
    val regionIdSet: Set<BigInteger>

    /**
     * 拥有的所有权限CODE集合（所有角色拥有的权限CODE，去重）
     */
    val permissionCodeSet: Set<String>

    /**
     * 当前数据权限id
     */
    val currentOrgId: BigInteger?
}

data class DefaultInfo(
    override val roleCodeSet: Set<String> = setOf(),
    override val identityTagSet: Set<String> = setOf(),
    override val orgIdSet: Set<BigInteger> = setOf(),
    override val regionIdSet: Set<BigInteger> = setOf(),
    override val permissionCodeSet: Set<String> = setOf(),
    override val currentOrgId: BigInteger? = regionIdSet.takeIf { it.isNotEmpty() }?.first() ?: orgIdSet.takeIf { it.isNotEmpty() }?.first()
) : ExtendUserDetails

val emptyJwtExtendUserDetailsObtainHandler = object : JwtExtendUserDetailsObtainHandler {
    override fun getExtendInfo(userId: BigInteger): ExtendUserDetails {
        return DefaultInfo()
    }
}