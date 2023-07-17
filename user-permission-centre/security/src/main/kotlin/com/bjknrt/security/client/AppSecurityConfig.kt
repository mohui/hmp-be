package com.bjknrt.security.client

import com.bjknrt.security.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.AnyRequestMatcher
import java.security.SecureRandom
import java.util.*

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AppSecurityProperties::class)
@Import(RpcJwtTransfer::class, JwtExtendUserDetailsObtainHandlerDefaultImpl::class)
@EnableWebSecurity
class AppSecurityConfig {

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder(4, SecureRandom())
    }

    @Bean
    fun jwtAuthenticationConverter(
        appSecurityProperties: AppSecurityProperties,
        jwtExtendUserDetailsObtainHandler: Optional<JwtExtendUserDetailsObtainHandler>
    ): JwtAuthenticationConverter {
        return JwtAuthenticationConverter(appSecurityProperties, jwtExtendUserDetailsObtainHandler.orElse(emptyJwtExtendUserDetailsObtainHandler))
    }

    @Bean
    fun rpcAuthenticationConverter(appSecurityProperties: AppSecurityProperties): RpcAuthenticationConverter {
        return RpcAuthenticationConverter(appSecurityProperties)
    }

    @Bean
    fun jwtAuthenticationFilter(
        jwtAuthenticationConverter: JwtAuthenticationConverter,
        appSecurityProperties: AppSecurityProperties,
        rpcAuthenticationConverter: RpcAuthenticationConverter
    ): JwtAuthenticationFilter {
        val antPathRequestMatcherList = appSecurityProperties.whiteList.map { AntPathRequestMatcher(it) }
        return JwtAuthenticationFilter(
            getUnAuthorizedEntryPoint(),
            jwtAuthenticationConverter,
            rpcAuthenticationConverter,
            antPathRequestMatcherList
        )
    }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        appSecurityProperties: AppSecurityProperties,
        jwtAuthenticationConverter: JwtAuthenticationConverter,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): SecurityFilterChain {

        http.authorizeRequests().anyRequest().permitAll()
            .and()
            .cors().disable() // jwt 禁用跨域
            .csrf().disable() // TODO 跨站请求伪造
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 禁用session
            }
            .httpBasic().disable() // 禁用
            .formLogin().disable() // 禁用原生用户名密码登陆
            .logout().disable() // 禁用
            .exceptionHandling {
                // 拒绝访问资源 403
                it.defaultAccessDeniedHandlerFor(AccessDeniedHandlerImpl(), AnyRequestMatcher.INSTANCE)
                // 未登录 401
                it.defaultAuthenticationEntryPointFor(getUnAuthorizedEntryPoint(), AnyRequestMatcher.INSTANCE)
            }
            .addFilterAfter(jwtAuthenticationFilter, RememberMeAuthenticationFilter::class.java) // JWTFilter
        return http.build()
    }

    /**
     * 未登录，登陆失败 401
     */
    private fun getUnAuthorizedEntryPoint() = AuthenticationEntryPoint { _, response, authException ->
        response?.status = HttpStatus.UNAUTHORIZED.value()
        response?.writer?.write(authException?.message ?: HttpStatus.UNAUTHORIZED.reasonPhrase)
        response?.writer?.flush()
    }

    /**
     * 禁用默认InMemory管理 使用
     */
    @Bean
    fun emptyUserDetails(): UserDetailsService {
        return UserDetailsService { User("empty", "empty", false, false, false, false, listOf()) }
    }

}