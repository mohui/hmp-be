package com.bjknrt.wechat.service.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.wechat.service.api.WechatApi
import com.bjknrt.wechat.service.dao.KnWechatSubscribeLog
import com.bjknrt.wechat.service.dao.KnWechatSubscribeLogTable
import com.bjknrt.wechat.service.vo.WechatSubscribePostRequest
import me.danwi.kato.common.exception.KatoAuthenticationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RestController

@RestController("com.bjknrt.wechat.service.api.WechatController")
class WechatController(
    @Value("\${fake_token}") val fakeToken: String,
    val wechatSubscribeTable: KnWechatSubscribeLogTable,
) : AppBaseController(), WechatApi {
    override fun wechatSubscribePost(wechatSubscribePostRequest: WechatSubscribePostRequest): Any? {
        // 获取 token header
        val token = getRequest().getHeader("token")
        // 判断 token 是否合法
        if (token != fakeToken) throw KatoAuthenticationException()
        // 写入数据库
        wechatSubscribeTable.saveOrUpdate(
            KnWechatSubscribeLog.builder()
                .setKnId(AppIdUtil.nextId())
                .setKnUnionid(wechatSubscribePostRequest.from)
                .setKnEvent(wechatSubscribePostRequest.event.value)
                .setKnEventAt(wechatSubscribePostRequest.eventAt)
                .build()
        )

        return null
    }
}
