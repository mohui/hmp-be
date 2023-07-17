package com.bjknrt.security.test

import com.bjknrt.security.*
import org.springframework.security.core.context.SecurityContextHolder
import java.math.BigInteger
import java.util.*

/**
 * 测试工具
 */
class AppSecurityTestUtil {

    companion object {
        // 设置当前用户信息
        fun setCurrentUserInfo(
            userId: BigInteger,
            loginName: String = "loginName",
            nickName: String = "nickName",
            roleCodeSet: Set<String> = setOf(),
            identityTagSet: Set<String> = setOf(),
            orgIdSet: Set<BigInteger> = setOf(),
            regionIdSet: Set<BigInteger> = setOf(),
            permissionCodeSet: Set<String> = setOf(),
            enabled: Boolean = true,
            issuedAt: Date = Date(),
            notBefore: Date = Date(),
            expiration: Date = Date(),
            id: BigInteger = BigInteger.valueOf(Math.random().toLong()),
            extendUserDetailsObtainHandler: JwtExtendUserDetailsObtainHandler = object : JwtExtendUserDetailsObtainHandler {
                override fun getExtendInfo(userId: BigInteger): ExtendUserDetails {
                    return DefaultInfo(
                        roleCodeSet = roleCodeSet,
                        identityTagSet = identityTagSet,
                        orgIdSet = orgIdSet,
                        regionIdSet = regionIdSet,
                        permissionCodeSet = permissionCodeSet
                    )
                }
            },
        ) {
            SecurityContextHolder.setContext(
                SecurityContextHolder.createEmptyContext().apply {
                    authentication = JwtAuthenticationToken(
                        JwtUserDetails(
                            id = id,
                            issuedAt = issuedAt,
                            notBefore = notBefore,
                            expiration = expiration,
                            userId = userId,
                            nickName = nickName,
                            enabled = enabled,
                            loginName = loginName,
                        ).apply {
                            this.extendUserDetailsObtainHandler = extendUserDetailsObtainHandler
                        },
                        ""
                    ).apply {
                        this.isAuthenticated = true
                    }
                }
            )
        }
    }
}