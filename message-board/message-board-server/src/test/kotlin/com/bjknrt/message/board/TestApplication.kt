package com.bjknrt.message.board

import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@ImportKatoClients(basePackages = ["com.bjknrt.user.permission.centre.api", "com.bjknrt.doctor.patient.management.api"])
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.message.board", "com.bjknrt.framework"])
class TestApplication
