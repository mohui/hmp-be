package com.bjknrt.user.permission.centre.api

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.framework.api.vo.Page
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.security.JwtAuthenticationConverter
import com.bjknrt.user.permission.centre.AbstractContainerBaseTest
import com.bjknrt.user.permission.centre.vo.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional

class AuthTest : AbstractContainerBaseTest() {


    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var api: AuthApi

    @Autowired
    lateinit var userApi: UserApi

    val cookieName = "Set-Cookie"

    @Test
    fun loginCookieTest() {
        val httpHeaders = HttpHeaders()

        // 未授权
        val postForEntity = rest.postForEntity<Any>("/user/page", HttpEntity(null, httpHeaders), null)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, postForEntity.statusCode)

        // register
        val registerParam = RegisterParam(
            name = "nickName",
            loginName = "loginTestLoginName",
            loginPwd = "loginTestLoginPwd",
            gender = Gender.MAN,
            phone = "13336363526"
        )
        val registerId = register(registerParam)

        // login
        val loginParamHttpEntity = HttpEntity(LoginParam(registerParam.loginName, registerParam.loginPwd))
        login(loginParamHttpEntity, httpHeaders)

        // 已授权
        val authedPageRes =
            rest.postForEntity("/user/page", HttpEntity(Page(1, 1), httpHeaders), PagedResult::class.java)
        Assertions.assertEquals(HttpStatus.OK, authedPageRes.statusCode)
        updateCookie(authedPageRes.headers[cookieName], httpHeaders)

        // 重置密码
        val resetRes = rest.postForEntity("/auth/reset", HttpEntity(registerId, httpHeaders), Any::class.java)
        Assertions.assertEquals(HttpStatus.OK, resetRes.statusCode)
        updateCookie(resetRes.headers[cookieName], httpHeaders)
        // 重新登录
        val loginParamHttpEntity1 = HttpEntity(LoginParam(registerParam.loginName, "666666"))
        login(loginParamHttpEntity1, httpHeaders)

        // 已授权
        val resetAuthRes =
            rest.postForEntity("/user/page", HttpEntity(Page(1, 1), httpHeaders), PagedResult::class.java)
        Assertions.assertEquals(HttpStatus.OK, resetAuthRes.statusCode)
        updateCookie(resetAuthRes.headers[cookieName], httpHeaders)

        // 登出
        val logOut = rest.postForEntity("/auth/logout", HttpEntity(null, httpHeaders), Any::class.java)
        Assertions.assertEquals(HttpStatus.OK, logOut.statusCode)
        updateCookie(logOut.headers[cookieName], httpHeaders)

        // 未授权
        val logOutAuthRes = rest.postForEntity<Any>("/user/page", HttpEntity(null, null), null)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, logOutAuthRes.statusCode)
    }

    private fun login(
        loginParamHttpEntity: HttpEntity<LoginParam>,
        httpHeaders: HttpHeaders
    ) {
        val resetLoginRes = rest.postForEntity("/auth/login", loginParamHttpEntity, String::class.java)
        Assertions.assertEquals(HttpStatus.OK, resetLoginRes.statusCode)
        val jwtTokenReset = resetLoginRes.body
        Assertions.assertNotNull(jwtTokenReset)
        updateCookie(resetLoginRes.headers[cookieName], httpHeaders)
    }

    private fun register(registerParam: RegisterParam): Id? {
        val registerRes = rest.postForEntity("/auth/register", HttpEntity(registerParam), Id::class.java)
        Assertions.assertEquals(HttpStatus.OK, registerRes.statusCode)
        Assertions.assertTrue(registerRes.hasBody())
        val registerId = registerRes.body
        return registerId
    }

    private fun updateCookie(cookies: List<String>?, httpHeaders: HttpHeaders) {
        Assertions.assertNotNull(cookies)
        httpHeaders["cookie"] = cookies?.joinToString(";")
    }

    @Test
    fun loginTokenTest() {
        val httpHeaders = HttpHeaders()

        // 未授权
        val postForEntity = rest.postForEntity<Any>("/user/page", HttpEntity(null, httpHeaders), null)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, postForEntity.statusCode)

        // register
        val registerParam = RegisterParam(
            name = "nickName",
            loginName = "loginTokenTestName",
            loginPwd = "loginTokenTestPwd",
            phone = "13336363529",
            gender = Gender.MAN
        )
        val registerRes = rest.postForEntity("/auth/register", HttpEntity(registerParam), Id::class.java)
        Assertions.assertEquals(HttpStatus.OK, registerRes.statusCode)
        Assertions.assertTrue(registerRes.hasBody())

        // login
        val loginRes = rest.postForEntity(
            "/auth/login",
            HttpEntity(LoginParam(registerParam.loginName, registerParam.loginPwd)),
            String::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, loginRes.statusCode)
        val jwtToken = loginRes.body
        Assertions.assertNotNull(jwtToken)
        httpHeaders[JwtAuthenticationConverter.JWT_TOKEN_KEY] =
            loginRes.headers[JwtAuthenticationConverter.JWT_TOKEN_KEY]

        // 已授权
        val authedPageRes =
            rest.postForEntity("/user/page", HttpEntity(Page(1, 1), httpHeaders), PagedResult::class.java)
        Assertions.assertEquals(HttpStatus.OK, authedPageRes.statusCode)
    }

    @Transactional
    @Test
    fun loginNoVerifyTest() {
        val phone = "13188133123"
        // 新增用户
        val userRoleOrg = userApi.saveUserRoleOrg(
            SaveUserRoleOrgRequest(
                name = "nickName",
                loginName = phone,
                gender = Gender.MAN,
                phone = phone,
            )
        )
        Assertions.assertNotNull(userRoleOrg)
        // loginNoVerifyTest
        val loginNoVerify =
            api.loginNoVerify(
                LoginNoVerifyParam(
                    name = "loginNoVerifyTest",
                    phone = phone,
                    identityTagList = setOf(IdentityTag.REGION_ADMIN)
                )
            )
        val loginNoVerifyUser = userApi.listByIds(listOf(loginNoVerify.id))
        Assertions.assertEquals(phone, loginNoVerifyUser[0].loginName)
    }

    @Test
    fun updatePwdTest() {
        val httpHeaders = HttpHeaders()

        // 注册
        val registerParam = RegisterParam(
            name = "nickName",
            loginName = "updatePwdTestName",
            loginPwd = "updatePwd",
            phone = "13336363956",
            gender = Gender.MAN
        )
        register(registerParam)

        // 登录
        val loginParamHttpEntity = HttpEntity(LoginParam(registerParam.loginName, registerParam.loginPwd))
        login(loginParamHttpEntity, httpHeaders)

        // 修改密码
        val updatePwdParam = UpdatePwdParam(registerParam.loginPwd, "NEW" + registerParam.loginPwd)
        val updatePwdParamHttpEntity = HttpEntity(updatePwdParam, httpHeaders)
        val updatePwdRes = rest.postForEntity("/auth/updatePwd", updatePwdParamHttpEntity, Any::class.java)
        Assertions.assertEquals(HttpStatus.OK, updatePwdRes.statusCode)

        // 新密码登录
        val newLoginParamHttpEntity = HttpEntity(LoginParam(registerParam.loginName, updatePwdParam.newPwd))
        login(newLoginParamHttpEntity, httpHeaders)
    }
}
