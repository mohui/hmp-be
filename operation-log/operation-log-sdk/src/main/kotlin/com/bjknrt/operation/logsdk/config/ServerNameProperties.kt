package com.bjknrt.operation.logsdk.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author hgm
 */
@ConfigurationProperties("spring.application")
object ServerNameProperties {
    lateinit var name: String
}