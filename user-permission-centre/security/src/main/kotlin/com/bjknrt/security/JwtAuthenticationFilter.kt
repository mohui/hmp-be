package com.bjknrt.security

import org.springframework.core.log.LogMessage
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val jwtAuthenticationConverter: JwtAuthenticationConverter,
    private val rpcAuthenticationConverter: RpcAuthenticationConverter,
    private val whitelist: List<AntPathRequestMatcher>? = null
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        whitelist?.forEach { matcher ->
            if (matcher.matches(request)) {
                logger.trace("命中：${matcher.pattern} 白名单，不处理:JwtAuthenticationFilter")
                return true
            }
        }
        return false
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        try {
            // 占位变量
            val authenticationToken: AbstractAuthenticationToken

            // 验证 RPC TOKEN
            val rpcAuthenticationToken = rpcAuthenticationConverter.convert(request)
            if (rpcAuthenticationToken == null) {
                // 验证JWT并且续约
                authenticationToken = jwtAuthenticationConverter.convertAndRenew(request, response) ?: throw BadCredentialsException("no find JWT")
            } else {
                authenticationToken = rpcAuthenticationToken
                // 获取JWT
                authenticationToken.principal = jwtAuthenticationConverter.convert(request)
            }

            // 授权
            authenticationToken.isAuthenticated = true
            // SecurityContext
            val context = SecurityContextHolder.createEmptyContext()
            context.authentication = authenticationToken
            SecurityContextHolder.setContext(context)
            if (logger.isDebugEnabled) {
                logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authenticationToken))
            }

            // doFilter
            chain.doFilter(request, response)
        } catch (ex: AuthenticationException) {
            SecurityContextHolder.clearContext()
            logger.debug("Failed to process authentication request", ex)
            authenticationEntryPoint.commence(request, response, ex)
        }
    }
}