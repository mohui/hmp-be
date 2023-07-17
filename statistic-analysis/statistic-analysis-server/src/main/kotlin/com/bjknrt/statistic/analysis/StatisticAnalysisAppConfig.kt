package com.bjknrt.statistic.analysis

import me.danwi.sqlex.core.DaoFactory
import me.danwi.sqlex.spring.ImportSqlEx
import me.danwi.sqlex.spring.SpringDaoFactory
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@ConfigurationPropertiesScan
@Configuration
@ImportSqlEx(Repository::class, factoryName = "hmpStatisticAnalysisServerFactory") // sqlex
@EnableConfigurationProperties(ForeignDataSourceConfigProperties::class)
class StatisticAnalysisAppConfig(val dataSource: DataSource, val foreignDataSourceConfigProperties: ForeignDataSourceConfigProperties) {

    /**
     * Sqlex数据源工厂
     */
    @Bean
    fun hmpStatisticAnalysisServerFactory(): DaoFactory {
        val factory = SpringDaoFactory(dataSource, Repository::class.java)

        foreignDataSourceConfigProperties.foreign.forEach {
            factory.setDatabaseName(it.key, it.value)
        }

        factory.migrate()
        factory.check()
        return factory
    }
}