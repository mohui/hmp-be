package com.bjknrt.message.board

import com.bjknrt.framework.test.DbTestConfig
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.support.JdbcTransactionManager
import javax.sql.DataSource

@TestConfiguration
@Import(DbTestConfig::class)
class ConfigTest {

    @Bean
    fun transactionManager(dataSource: DataSource, transactionManagerCustomizers: ObjectProvider<TransactionManagerCustomizers>): DataSourceTransactionManager {
        val transactionManager = JdbcTransactionManager(dataSource)
        transactionManagerCustomizers.ifAvailable { customizers: TransactionManagerCustomizers -> customizers.customize(transactionManager) }
        return transactionManager
    }

}
