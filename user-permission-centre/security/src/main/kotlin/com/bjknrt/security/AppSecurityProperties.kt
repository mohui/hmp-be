package com.bjknrt.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import java.time.Duration

@RefreshScope
@ConfigurationProperties("app.security")
data class AppSecurityProperties(
    var jwt: Jwt = Jwt(),
    var rpcJwt: Jwt = Jwt(),
    var cookie: Cookie = Cookie(),
    /** 不会被过滤器拦截白名单 */
    var whiteList: MutableSet<String> = mutableSetOf(
        "/actuator/health/*",
        "/auth/login",
        "/auth/register",
        "/auth/loginNoVerify"
    )
) {

    data class Jwt(
        /**
         * 字符长度必须大于等于32;  32:HS256 48:HS384 64:HS512
         */
        var secret: String = "bjknrt-default-secret-must-update",
        /**
         * jwt 有效时常 默认 3600s
         */
        var maxAge: Duration = Duration.ofSeconds(3600),
        /**
         * 允许的始终偏差（服务器之间有时钟偏差）
         */
        val allowedClockSkewSeconds: Duration = Duration.ofMinutes(5)
    )

    data class Cookie(
        var secure: Boolean = false,
        var isHttpOnly: Boolean = true,
        var maxAge: Duration = Duration.ofSeconds(3600),
        var domain: String? = null
    )
}