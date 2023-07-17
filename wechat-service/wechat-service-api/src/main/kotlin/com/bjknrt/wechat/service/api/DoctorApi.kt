package com.bjknrt.wechat.service.api

import com.bjknrt.wechat.service.vo.DoctorLoginPostRequest
import com.bjknrt.wechat.service.vo.UnbindNotify
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.wechat-service.kato-server-name:\${spring.application.name}}", contextId = "DoctorApi2")
@Validated
interface DoctorApi {


    /**
     * 医生登录 (v1.0)
     *
     *
     * @param doctorLoginPostRequest
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctor/login"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun doctorLoginPost(@Valid doctorLoginPostRequest: DoctorLoginPostRequest): kotlin.Any?


    /**
     * 患者解绑通知 (FE v1.10)
     *
     *
     * @param unbindNotify
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/doctor/notify/unbind"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun doctorNotifyUnbindPost(@Valid unbindNotify: UnbindNotify): kotlin.Any?
}
