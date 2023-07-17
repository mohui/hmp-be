package com.bjknrt.wechat.service.boot

import cn.hutool.core.thread.BlockPolicy
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


@EnableAsync
@Configuration
@RefreshScope
@ConfigurationProperties("app.executor.notify")
data class ExecutorConfig(
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
    var queueCapacity: Int = 100000,

    /**
     * 线程名称前缀
     */
     var namePrefix: String ="wechat-notify-"

) {

    companion object {
        const val WE_CHAT_NOTIFY_EXECUTOR = "weChatNotifyExecutor"
    }

    @Bean(WE_CHAT_NOTIFY_EXECUTOR)
    fun asyncServiceExecutor(): ThreadPoolTaskExecutor? {
        val executor = ThreadPoolTaskExecutor()
        //配置核心线程数
        executor.corePoolSize = corePoolSize
        //配置最大线程数
        executor.maxPoolSize = maxPoolSize
        //配置队列大小
        executor.setQueueCapacity(queueCapacity)
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(namePrefix)
        // BlockPolicy：当任务队列过长时处于阻塞状态，直到添加到队列中
        executor.setRejectedExecutionHandler(BlockPolicy())
        //执行初始化
        executor.initialize()
        return executor
    }
}
