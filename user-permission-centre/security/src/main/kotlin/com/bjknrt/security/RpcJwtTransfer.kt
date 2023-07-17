package com.bjknrt.security

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * 微服务之间调用携带JWT
 */
@Component
class RpcJwtTransfer(
    private val rpcAuthenticationConverter: RpcAuthenticationConverter
) : RequestInterceptor {
    override fun apply(template: RequestTemplate?) {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication is JwtAuthenticationToken) {
            template?.header(JwtAuthenticationConverter.JWT_TOKEN_KEY, authentication.credentials)
        } else if (authentication is RpcAuthenticationToken) {
            authentication.principal?.let {
                template?.header(JwtAuthenticationConverter.JWT_TOKEN_KEY, it.credentials)
            }
            template?.header(RpcAuthenticationConverter.RPC_TOKEN_KEY, authentication.credentials)
        } else {
            // 未登录情况（定时调度）
            template?.header(RpcAuthenticationConverter.RPC_TOKEN_KEY, rpcAuthenticationConverter.buildRpcJws())
        }
    }

}