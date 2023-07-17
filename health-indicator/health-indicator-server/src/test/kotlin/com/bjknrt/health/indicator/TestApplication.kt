package com.bjknrt.health.indicator

import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@ImportKatoClients(basePackages = ["com.bjknrt.health.scheme.api", "com.bjknrt.doctor.patient.management.api", "com.bjknrt.user.permission.centre.api", "com.bjknrt.wechat.service.api"])
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.health.indicator", "com.bjknrt.framework"])
class TestApplication
