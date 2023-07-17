package com.bjknrt.medication.remind

import com.bjknrt.framework.test.DbTestConfig
import org.jobrunr.jobs.mappers.JobMapper
import org.jobrunr.storage.InMemoryStorageProvider
import org.jobrunr.storage.StorageProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@Import(value = [DbTestConfig::class])
class ConfigTest {
    @Bean
    fun inMemoryStorageProvider(jobMapper: JobMapper): StorageProvider {
        return InMemoryStorageProvider().apply {
            setJobMapper(jobMapper)
        }
    }
}