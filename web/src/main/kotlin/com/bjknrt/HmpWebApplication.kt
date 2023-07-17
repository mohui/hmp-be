package com.bjknrt

import com.bjknrt.operation.logsdk.annotation.EnableOperationLog
import feign.Logger
import me.danwi.kato.server.EnableKatoServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean

@EnableKatoServer
@EnableDiscoveryClient
@EnableOperationLog
@SpringBootApplication(scanBasePackages = ["com.bjknrt"])
class HmpWebApplication{
    /**
     * Feign 日志级别
     **/
    @Bean
    fun feignLogLevel(): Logger.Level {
        return Logger.Level.FULL
    }
}
fun main(args: Array<String>) {
    runApplication<HmpWebApplication>(*args)
}