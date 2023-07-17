package com.bjknrt.user.permission.centre

import com.bjknrt.operation.logsdk.annotation.EnableOperationLog
import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableOperationLog
@EnableDiscoveryClient
@ImportKatoClients(basePackages = ["com.bjknrt.health.scheme.api", "com.bjknrt.doctor.patient.management.api", "com.bjknrt.operation.log.api"])
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.user.permission.centre","com.bjknrt.framework"])
class TestApplication
