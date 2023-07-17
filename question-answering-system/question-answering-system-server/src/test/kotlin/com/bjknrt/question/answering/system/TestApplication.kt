package com.bjknrt.question.answering.system

import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@ImportKatoClients(
    basePackages =
    [
        "com.bjknrt.health.scheme.api",
        "com.bjknrt.user.permission.centre.api",
        "com.bjknrt.health.indicator.api",
        "com.bjknrt.doctor.patient.management.api"
    ]
)
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.question.answering.system", "com.bjknrt.framework"])
class TestApplication
