package com.bjknrt.health.scheme

import com.bjknrt.framework.test.DbTestConfig
import org.jobrunr.jobs.mappers.JobMapper
import org.jobrunr.storage.InMemoryStorageProvider
import org.jobrunr.storage.StorageProvider
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.support.JdbcTransactionManager
import javax.sql.DataSource

@Import(value = [DbTestConfig::class])
@TestConfiguration
class ConfigTest {

    @Bean
    fun transactionManager(
        dataSource: DataSource,
        transactionManagerCustomizers: ObjectProvider<TransactionManagerCustomizers>
    ): DataSourceTransactionManager {
        val transactionManager = JdbcTransactionManager(dataSource)
        transactionManagerCustomizers.ifAvailable { customizers: TransactionManagerCustomizers ->
            customizers.customize(
                transactionManager
            )
        }
        return transactionManager
    }

    @Primary
    @Bean
    fun inMemoryStorageProvider(jobMapper: JobMapper): StorageProvider {
        return InMemoryStorageProvider().apply {
            setJobMapper(jobMapper)
        }
    }

}
