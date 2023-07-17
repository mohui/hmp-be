package com.bjknrt.doctor.patient.management

import com.bjknrt.operation.logsdk.annotation.EnableOperationLog
import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableOperationLog
@EnableDiscoveryClient
@ImportKatoClients(
    basePackages =
    [
        "com.bjknrt.health.scheme.api",
        "com.bjknrt.operation.log.api",
        "com.bjknrt.user.permission.centre.api",
        "com.bjknrt.question.answering.system.api",
        "com.bjknrt.wechat.service.api",
        "com.bjknrt.health.indicator.api"
    ]
)
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.doctor.patient.management", "com.bjknrt.framework"])
class TestApplication
