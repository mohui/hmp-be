package com.bjknrt.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.BadCredentialsException
import javax.servlet.http.HttpServletRequest

class RpcAuthenticationConverter(private val appSecurityProperties: AppSecurityProperties) {

    private val key = Keys.hmacShaKeyFor(appSecurityProperties.rpcJwt.secret.toByteArray())

    companion object {
        const val RPC_TOKEN_KEY: String = "RPC_TOKEN"
    }

    fun parseRpcJws(value: String): Boolean {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .setAllowedClockSkewSeconds(appSecurityProperties.jwt.allowedClockSkewSeconds.seconds)
            .build()
            .parseClaimsJws(value)
            .body
            .subject == RPC_TOKEN_KEY
    }

    fun buildRpcJws(): String {
        return Jwts.builder()
            .signWith(key)
            .setSubject(RPC_TOKEN_KEY)
            .compact()
    }

    /**
     * 获取JWT
     */
    fun convert(request: HttpServletRequest?): RpcAuthenticationToken? {
        return request?.let {
            it.getHeader(RPC_TOKEN_KEY)?.let { token ->
                val parsed: Boolean
                try {
                    // 验证
                    parsed = parseRpcJws(token)
                } catch (e: Exception) {
                    throw BadCredentialsException("failed to verify RPC TOKEN", e)
                }
                if (!parsed) {
                    throw BadCredentialsException("RPC TOKEN Not Allow Modify")
                }
                // 构造
                return RpcAuthenticationToken(token)
            }
        }
    }
}