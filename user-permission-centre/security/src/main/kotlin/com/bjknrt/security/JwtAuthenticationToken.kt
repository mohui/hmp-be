package com.bjknrt.security

import org.springframework.security.authentication.AbstractAuthenticationToken

data class JwtAuthenticationToken(
    private val principal: JwtUserDetails,
    private val credentials: String
) : AbstractAuthenticationToken(null) {

    init {
        isAuthenticated = false
    }

    override fun getCredentials(): String {
        return credentials
    }

    override fun getPrincipal(): JwtUserDetails {
        return principal
    }
}