package com.bjknrt.doctor.patient.management

import com.bjknrt.framework.test.DbTestConfig
import com.zaxxer.hikari.HikariDataSource
import org.jobrunr.jobs.mappers.JobMapper
import org.jobrunr.storage.InMemoryStorageProvider
import org.jobrunr.storage.StorageProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import javax.sql.DataSource

@Import(value = [DbTestConfig::class])
class ConfigTest {
    @Bean
    fun foreignDataSourceConfigProperties(
        dataSource: DataSource
    ): ForeignDataSourceConfigProperties {
        val hikariDataSource = dataSource as HikariDataSource
        val dbName = hikariDataSource.jdbcUrl.substringAfterLast("/")
        return ForeignDataSourceConfigProperties(
            foreign = mapOf(
                "upcs" to dbName,
                "hs" to dbName
            )
        )
    }

    @Bean
    fun inMemoryStorageProvider(jobMapper: JobMapper): StorageProvider {
        return InMemoryStorageProvider().apply {
            setJobMapper(jobMapper)
        }
    }
}
