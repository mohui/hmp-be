package com.bjknrt.operation.log

import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@ImportKatoClients(basePackages = ["com.bjknrt.user.permission.centre.api"])
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.operation.log", "com.bjknrt.framework"])
class TestApplication
