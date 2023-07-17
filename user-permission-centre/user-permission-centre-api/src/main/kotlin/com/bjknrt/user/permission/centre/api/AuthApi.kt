package com.bjknrt.user.permission.centre.api

import com.bjknrt.framework.api.vo.Id
import com.bjknrt.user.permission.centre.vo.*
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@Validated
@KatoClient(name = "\${app.hmp-user-permission-centre.kato-server-name:\${spring.application.name}}", contextId = "AuthApi")
interface AuthApi {


    /**
     * 登录
     * 
     *
     * @param loginParam
     * @return kotlin.String
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/auth/login"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun login(@Valid loginParam: LoginParam): kotlin.String


    /**
     * 内网登录
     * 
     *
     * @param loginNoVerifyParam
     * @return LoginNoVerifyResponse
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/auth/loginNoVerify"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun loginNoVerify(@Valid loginNoVerifyParam: LoginNoVerifyParam): LoginNoVerifyResponse


    /**
     * 登出
     * 
     *
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/auth/logout"],
        produces = ["application/json"]
    )
    fun logout(): Unit


    /**
     * 注册用户
     * 
     *
     * @param registerParam
     * @return Id
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/auth/register"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun register(@Valid registerParam: RegisterParam): Id


    /**
     * 重置密码
     * 
     *
     * @param body
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/auth/reset"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun reset(@Valid body: java.math.BigInteger): Unit


    /**
     * 根据旧密码修改新密码
     * 
     *
     * @param updatePwdParam
     * @return Unit
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/auth/updatePwd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updatePwd(@Valid updatePwdParam: UpdatePwdParam): Unit


    /**
     * 校验密码
     * 
     *
     * @param body
     * @return kotlin.Boolean
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/auth/verifyPwd"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun verifyPwd(@Valid body: kotlin.String): kotlin.Boolean
}
