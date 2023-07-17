package com.bjknrt.security

import org.springframework.security.authentication.AbstractAuthenticationToken

/**
 * 微服务内部RPC调用使用
 */
data class RpcAuthenticationToken(
    private val credentials: String,
    var principal: JwtAuthenticationToken? = null
) : AbstractAuthenticationToken(null) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): String {
        return credentials
    }

    override fun getPrincipal(): Any? {
        return principal
    }

    override fun setAuthenticated(authenticated: Boolean) {
        super.setAuthenticated(authenticated)
        principal?.isAuthenticated = authenticated
    }
}