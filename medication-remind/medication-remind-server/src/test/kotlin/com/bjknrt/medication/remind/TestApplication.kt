package com.bjknrt.medication.remind

import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient


@EnableDiscoveryClient
@ImportKatoClients(
    basePackages =
    [
        "com.bjknrt.user.permission.centre.api",
        "com.bjknrt.wechat.service.api",
        "com.bjknrt.article.service.api"
    ]
)
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.medication.remind", "com.bjknrt.framework"])
class TestApplication
