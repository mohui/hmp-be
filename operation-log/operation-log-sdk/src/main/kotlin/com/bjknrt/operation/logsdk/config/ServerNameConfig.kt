package com.bjknrt.operation.logsdk.config

import me.danwi.kato.client.ImportKatoClients
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ServerNameProperties::class)
@ImportKatoClients(basePackages = ["com.bjknrt.operation.logsdk"])
class ServerNameConfig