package com.bjknrt.health.indicator.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("indicator.task.executor.pool")
class ExecutorProperties(
    /**
     * 核心线程数
     */
    var corePoolSize: Int = 10,

    /**
     * 最大线程数
     */
    var maxPoolSize: Int = 20,

    /**
     * 队列容量
     */
    var queueCapacity: Int = 50,

    /**
     * 线程名称前缀
     */
    var namePrefix: String = "indicator-async-thread-"
)