package com.bjknrt.wechat.service.api

import com.bjknrt.wechat.service.vo.WechatSubscribePostRequest
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.wechat-service.kato-server-name:\${spring.application.name}}", contextId = "WechatApi")
@Validated
interface WechatApi {


    /**
     * 国卫微信通知 (v1.10)
     *
     *
     * @param wechatSubscribePostRequest
     * @return kotlin.Any?
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/wechat/subscribe"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun wechatSubscribePost(@Valid wechatSubscribePostRequest: WechatSubscribePostRequest): kotlin.Any?
}
