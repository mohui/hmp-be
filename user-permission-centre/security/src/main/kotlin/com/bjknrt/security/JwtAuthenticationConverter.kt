package com.bjknrt.security

import io.jsonwebtoken.CompressionCodecs
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.jackson.io.JacksonDeserializer
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.util.StringUtils
import java.time.temporal.ChronoUnit
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationConverter(
    private val appSecurityProperties: AppSecurityProperties,
    private val jwtExtendUserDetailsObtainHandler: JwtExtendUserDetailsObtainHandler = emptyJwtExtendUserDetailsObtainHandler
) {

    private val key = Keys.hmacShaKeyFor(appSecurityProperties.jwt.secret.toByteArray())

    // TODO 自定义序列化，多不需要字段不报错
    private val deserializer = JacksonDeserializer<Map<String, *>>(
        mapOf(
            USER to JwtUserDetails::class.java,
        )
    )

    companion object {
        const val JWT_TOKEN_KEY: String = "TOKEN"
        const val JWT_COOKIE_KEY: String = "jck"

        const val USER: String = "u"
    }

    fun jwtStrToJwtUserDetails(value: String): JwtUserDetails {
        val claimsJwt = Jwts.parserBuilder()
            .setSigningKey(key)
            .setAllowedClockSkewSeconds(appSecurityProperties.jwt.allowedClockSkewSeconds.seconds)
            .deserializeJsonWith(deserializer)
            .build()
            .parseClaimsJws(value).body
        return claimsJwt.get(USER, JwtUserDetails::class.java)
    }

    fun jwtUserDetailsToJwtStr(jwtUserDetails: JwtUserDetails): String {
        return Jwts.builder()
            .compressWith(CompressionCodecs.GZIP)
            .signWith(key)
            .setId(jwtUserDetails.id.toString())
            .setIssuedAt(jwtUserDetails.issuedAt)
            .setNotBefore(jwtUserDetails.notBefore)
            .setExpiration(jwtUserDetails.expiration)
            .claim(USER, jwtUserDetails)
            .compact()
    }

    private fun setExtendUserInfo(userDetails: JwtUserDetails): JwtUserDetails {
        return userDetails.apply {
            extendUserDetailsObtainHandler = jwtExtendUserDetailsObtainHandler
        }
    }

    /**
     * 续约
     */
    fun renew(jwtUserDetails: JwtUserDetails, response: HttpServletResponse? = null, destroy: Boolean = false): String {
        val copy = jwtUserDetails.copy()
        // 删除密码
        copy.loginPwd = null
        // 延长过期时间
        copy.expiration =
            if (destroy)
                Date()
            else
                Date(System.currentTimeMillis() + appSecurityProperties.jwt.maxAge.toMillis())

        // 转换jws
        val jwtUserDetailsToJwtStr = jwtUserDetailsToJwtStr(copy)

        // 设置续约时间
        response?.let {
            val cookie = Cookie(JWT_COOKIE_KEY, jwtUserDetailsToJwtStr)
            cookie.path = "/"
            cookie.secure = appSecurityProperties.cookie.secure
            cookie.maxAge = if (destroy) 0 else appSecurityProperties.cookie.maxAge.get(ChronoUnit.SECONDS).toInt()
            cookie.isHttpOnly = appSecurityProperties.cookie.isHttpOnly
            if (StringUtils.hasLength(appSecurityProperties.cookie.domain)) {
                cookie.domain = appSecurityProperties.cookie.domain
            }
            // Cookie续约
            it.addCookie(cookie)
            // Header-TOKEN续约
            it.setHeader(JWT_TOKEN_KEY, jwtUserDetailsToJwtStr)
        }

        return jwtUserDetailsToJwtStr
    }


    fun convertAndRenew(request: HttpServletRequest, response: HttpServletResponse): JwtAuthenticationToken? {
        return convert(request)?.apply {
            renew(principal, response)
        }
    }

    /**
     * 获取JWT
     */
    fun convert(request: HttpServletRequest?): JwtAuthenticationToken? {
        return request?.let {
            try {
                // 先从Header获取
                it.getHeader(JWT_TOKEN_KEY)?.let { token ->
                    return JwtAuthenticationToken(setExtendUserInfo(jwtStrToJwtUserDetails(token)), token)
                }
                // 没有则从Cookie中获取
                it.cookies?.find { cookie -> cookie.name == JWT_COOKIE_KEY }
                    ?.let { cookie ->
                        cookie.value?.let { v ->
                            return JwtAuthenticationToken(setExtendUserInfo(jwtStrToJwtUserDetails(v)), v)
                        }
                    }
            } catch (e: Exception) {
                throw BadCredentialsException("failed to verify JWT", e)
            }
        }
    }
}