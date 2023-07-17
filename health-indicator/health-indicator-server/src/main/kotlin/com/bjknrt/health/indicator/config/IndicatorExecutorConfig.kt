package com.bjknrt.health.indicator.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ThreadPoolExecutor

@EnableConfigurationProperties(ExecutorProperties::class)
@Configuration
class IndicatorExecutorConfig {

    companion object {
        const val INDICATOR_ASYNC_TASK_EXECUTOR = "indicatorAsyncTaskExecutor"
    }

    @RefreshScope
    @Primary
    @Bean(INDICATOR_ASYNC_TASK_EXECUTOR)
    fun taskExecutor(executorProperties: ExecutorProperties): ThreadPoolTaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()
        //配置核心线程数
        taskExecutor.corePoolSize = executorProperties.corePoolSize
        //配置最大线程数
        taskExecutor.maxPoolSize = executorProperties.maxPoolSize
        //配置队列大小
        taskExecutor.queueCapacity = executorProperties.queueCapacity
        //配置线程池中的线程的名称前缀
        taskExecutor.setThreadNamePrefix(executorProperties.namePrefix)
        //配置拒绝策略：当触发拒绝策略时(工作线程数等于最大线程数并且队列已满)，不允许任务丢弃，交由上级线程执行
        taskExecutor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        //执行初始化
        taskExecutor.initialize()
        return taskExecutor
    }
}
