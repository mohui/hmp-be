package com.bjknrt.medication.guide

import me.danwi.kato.client.ImportKatoClients
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@ImportKatoClients(basePackages = ["com.bjknrt.user.permission.centre.api", "com.bjknrt.doctor.patient.management.api"])
@EnableKatoServer
@SpringBootApplication(scanBasePackages = ["com.bjknrt.medication.guide","com.bjknrt.framework"])
class TestApplication

fun main(args: Array<String>) {
    runApplication<TestApplication>(*args)
}