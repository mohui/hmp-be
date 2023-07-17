package com.bjknrt.wechat.service.api

import com.bjknrt.wechat.service.vo.NotifyListPostResponseInner
import me.danwi.kato.client.KatoClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid

@KatoClient(name = "\${app.wechat-service.kato-server-name:\${spring.application.name}}", contextId = "NotifyApi")
@Validated
interface NotifyApi {


    /**
     * 微信消息 - 订阅消息列表 (v1.0) (废弃)
     *
     *
     * @param body
     * @return List<NotifyListPostResponseInner>
     */
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/notify/list"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun notifyListPost(@Valid body: kotlin.String): List<NotifyListPostResponseInner>
}
