package com.bjknrt.doctor.patient.management

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope

@RefreshScope
@ConfigurationProperties("spring.sqlex")
class ForeignDataSourceConfigProperties(
    var foreign: Map<String, String> = mapOf()
)
