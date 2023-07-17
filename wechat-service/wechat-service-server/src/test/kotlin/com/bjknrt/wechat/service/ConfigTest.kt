package com.bjknrt.wechat.service

import com.bjknrt.framework.test.DbTestConfig
import org.springframework.context.annotation.Import

@Import(value = [DbTestConfig::class])
class ConfigTest