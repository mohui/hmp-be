package com.bjknrt.wechat.service.boot

import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl
import cn.binarywang.wx.miniapp.config.WxMaConfig
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WechatConfig {
    @Value("\${wechat.id}")
    lateinit var id: String

    @Value("\${wechat.secret}")
    lateinit var secret: String

    /**
     * 用药提醒 消息模板id
     */
    @Value("\${wechat.message.drug}")
    lateinit var drugMessageId: String

    @Bean
    fun weChatConfig(): WxMaConfig {
        val config = WxMaDefaultConfigImpl()
        config.appid = id
        config.secret = secret
        return config
    }

    @Bean
    fun service(): WxMaService {
        val service = WxMaServiceImpl()
        service.wxMaConfig = weChatConfig()
        return service
    }
}