package com.bjknrt.wechat.service.api

import com.bjknrt.wechat.service.AbstractContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired

class NotifyTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: NotifyApi
}
