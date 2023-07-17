package com.bjknrt

import io.lettuce.core.RedisClient
import org.jobrunr.jobs.mappers.JobMapper
import org.jobrunr.spring.autoconfigure.JobRunrProperties
import org.jobrunr.storage.InMemoryStorageProvider
import org.jobrunr.storage.StorageProvider
import org.jobrunr.storage.nosql.redis.LettuceRedisStorageProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class JobRunnrConfig {

    //    @Bean
    fun inMemoryStorageProvider(jobMapper: JobMapper): StorageProvider {
        return InMemoryStorageProvider().apply {
            setJobMapper(jobMapper)
        }
    }

    @Bean(name = ["storageProvider"], destroyMethod = "close")
    @ConditionalOnMissingBean
    fun redisStorageProvider(
        lettuceConnectionFactory: LettuceConnectionFactory,
        jobMapper: JobMapper,
        properties: JobRunrProperties
    ): StorageProvider {
        val lettuceRedisStorageProvider = LettuceRedisStorageProvider(
            lettuceConnectionFactory.requiredNativeClient as RedisClient,
            properties.database.tablePrefix
        )
        lettuceRedisStorageProvider.setJobMapper(jobMapper)
        return lettuceRedisStorageProvider
    }

}