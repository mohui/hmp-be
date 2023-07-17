package com.bjknrt.health.scheme

import com.bjknrt.operation.logsdk.annotation.EnableOperationLog
import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableAsync

@EnableDiscoveryClient
@ImportKatoClients(
    basePackages =
    [
        "com.bjknrt.operation.log.api",
        "com.bjknrt.user.permission.centre.api",
        "com.bjknrt.question.answering.system.api",
        "com.bjknrt.health.indicator.api",
        "com.bjknrt.medication.remind.api",
        "com.bjknrt.doctor.patient.management.api"
    ]
)
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.health.scheme", "com.bjknrt.framework"])
@EnableOperationLog
@EnableAsync
class TestApplication
