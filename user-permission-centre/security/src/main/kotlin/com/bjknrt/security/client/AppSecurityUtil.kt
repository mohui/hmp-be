package com.bjknrt.security.client

import com.bjknrt.security.JwtAuthenticationToken
import com.bjknrt.security.JwtUserDetails
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.math.BigInteger

object AppSecurityUtil {

    /**
     * @return [Authentication]
     */
    fun authentication(): Authentication? {
        return SecurityContextHolder.getContext().authentication
    }

    /**
     * @return 用户登陆后请求，返回[JwtUserDetails];非用户登陆后调用，返回null
     */
    fun jwtUser(): JwtUserDetails? {
        val authentication = authentication()
        return if (authentication is JwtAuthenticationToken) {
            authentication.principal
        } else {
            null
        }
    }

    /**
     *  @return 当前有登陆用户，返回登陆用户id，如果没有登陆用户，返回null
     */
    fun currentUserId(): BigInteger? {
        return jwtUser()?.userId
    }

    /**
     * @param default 指定默认值，不指定默认[BigInteger.ZERO]
     * @return 当前登陆用户ID 或 默认值
     */
    fun currentUserIdWithDefault(default: BigInteger = BigInteger.ZERO): BigInteger {
        return currentUserId() ?: default
    }

    /**
     * 判断是否登录并且是超级管理员时，执行回调函数
     * @param block 回调函数
     * @return 未登录时，不进行回调，返回值为null
     *
     * 登录成功，是超级管理员，执行回调函数参数是null，返回值是回调函数的出参
     *
     * 登录成功，不是超级管理员，执行回调函数参数是orgIdSet，返回值是回调函数的出参
     *
     */
    fun <R> executeWithOrgIds(block: (isSuperAdmin: Boolean, orgIdSet: Set<BigInteger>?) -> R): R? {
        // 登录执行函数
        jwtUser()?.let { user ->
            // 参数为是否是超级管理员，机构Id集合
            return block(user.isSuperAdmin(), user.orgIdSet)
        }
        // 未登录，直接返回
        return null
    }

    /**
     *  当用户登录的时候执行的回调
     *  @param block 回调函数
     *  @return 登录时执行回调函数，返回回调函数的返回值
     *
     *  未登录时，返回null
     */
    fun <R> executeWithLogin(block: (user: JwtUserDetails) -> R): R? {
        jwtUser()?.let {
            return block(it)
        }
        return null
    }

}
