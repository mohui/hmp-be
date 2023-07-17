package com.bjknrt.wechat.service.api

import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@ImportKatoClients(basePackages = ["com.bjknrt.doctor.patient.management.api", "com.bjknrt.user.permission.centre.api"])
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.wechat.service", "com.bjknrt.framework"])
class TestApplication

