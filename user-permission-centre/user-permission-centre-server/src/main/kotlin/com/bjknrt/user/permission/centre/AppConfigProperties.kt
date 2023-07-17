package com.bjknrt.user.permission.centre

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope

@RefreshScope
@ConfigurationProperties("app.user")
class AppConfigProperties(
    /**
     * 默认密码
     */
    var defaultPassword: String = "666666"
)