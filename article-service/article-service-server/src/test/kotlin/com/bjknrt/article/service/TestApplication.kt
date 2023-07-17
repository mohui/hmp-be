package com.bjknrt.article.service

import com.bjknrt.operation.logsdk.annotation.EnableOperationLog
import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableOperationLog
@EnableDiscoveryClient
@ImportKatoClients(basePackages = ["com.bjknrt.health.scheme.api", "com.bjknrt.doctor.patient.management.api", "com.bjknrt.operation.log.api", "com.bjknrt.user.permission.centre.api"])
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.article.service", "com.bjknrt.framework"])
class TestApplication
