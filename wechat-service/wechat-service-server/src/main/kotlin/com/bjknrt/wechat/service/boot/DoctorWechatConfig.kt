package com.bjknrt.wechat.service.boot

import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl
import cn.binarywang.wx.miniapp.config.WxMaConfig
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 医生端微信小程序配置
 */
@Configuration
class DoctorWechatConfig {
    /**
     * 微信小程序id
     */
    @Value("\${doctor.id}")
    lateinit var id: String

    /**
     * 微信小程序secret
     */
    @Value("\${doctor.secret}")
    lateinit var secret: String

    /**
     * 跳转小程序类型
     *
     * developer: 开发版；trial: 体验版；formal: 正式版
     */
    @Value("\${doctor.state}")
    lateinit var state: String

    /**
     * 用药提醒 消息模板id
     */
    @Value("\${doctor.message.drug}")
    lateinit var drugMessageId: String

    /**
     * 患者解绑 消息模板id
     */
    @Value("\${doctor.message.unbind}")
    lateinit var unbindMessageId: String

    @Bean
    fun doctorConfig(): WxMaConfig {
        val config = WxMaDefaultConfigImpl()
        config.appid = id
        config.secret = secret
        return config
    }

    @Bean
    fun doctorService(): WxMaService {
        val service = WxMaServiceImpl()
        service.wxMaConfig = doctorConfig()
        return service
    }
}