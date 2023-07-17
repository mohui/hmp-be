package com.bjknrt.medication.guide

import com.bjknrt.framework.test.DbTestConfig
import com.zaxxer.hikari.HikariDataSource
import me.danwi.sqlex.core.DaoFactory
import me.danwi.sqlex.spring.SpringDaoFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.support.JdbcTransactionManager
import javax.sql.DataSource

@Import(DbTestConfig::class)
@TestConfiguration
class ConfigTest {

    @Bean
    @Primary
    fun factory1(dataSource: DataSource): DaoFactory {
        val factory = SpringDaoFactory(dataSource, Repository::class.java)
        factory.migrate()
        factory.check()
        return factory
    }

    @Bean
    @Primary
    @DependsOn(value = ["factory1"])
    fun medicationConfigProperty(dataSource: DataSource): MedicationGuideDbConfigProperties {
        val ds = dataSource as HikariDataSource
        return MedicationGuideDbConfigProperties(
            url = ds.jdbcUrl,
            username = ds.username,
            password = ds.password
        )
    }

    @Bean
    fun transactionManager(dataSource: DataSource, transactionManagerCustomizers: ObjectProvider<TransactionManagerCustomizers>): DataSourceTransactionManager {
        val transactionManager = JdbcTransactionManager(dataSource)
        transactionManagerCustomizers.ifAvailable { customizers: TransactionManagerCustomizers -> customizers.customize(transactionManager) }
        return transactionManager
    }


}