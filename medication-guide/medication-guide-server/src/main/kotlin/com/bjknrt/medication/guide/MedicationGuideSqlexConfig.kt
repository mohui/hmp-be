package com.bjknrt.medication.guide

import com.zaxxer.hikari.HikariDataSource
import me.danwi.sqlex.core.DaoFactory
import me.danwi.sqlex.core.exception.SqlExCheckException
import me.danwi.sqlex.core.exception.SqlExException
import me.danwi.sqlex.spring.ImportSqlEx
import me.danwi.sqlex.spring.SpringDaoFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy
import javax.sql.DataSource

@Configuration
@ImportSqlEx(Repository::class, factoryName = "hmpMedicationGuideServerFactory") // sqlex
@EnableConfigurationProperties(MedicationGuideDbConfigProperties::class)
class MedicationGuideSqlexConfig {

    var dataSource: HikariDataSource? = null

    /**
     * Sqlex数据源工厂
     */
    @Bean
    fun hmpMedicationGuideServerFactory(medicationGuideDbConfigProperties: MedicationGuideDbConfigProperties): DaoFactory {
        dataSource = hmpMedicationGuideDataSource(medicationGuideDbConfigProperties) as HikariDataSource
        val factory = SpringDaoFactory(
            dataSource,
            Repository::class.java
        )
        try {
            factory.check()
        } catch (e: SqlExCheckException) {
            throw SqlExException("数据库结构检查失败：${e.missed}")
        }
        return factory
    }

    private fun hmpMedicationGuideDataSource(medicationGuideDbConfigProperties: MedicationGuideDbConfigProperties): DataSource {
        val driverName = medicationGuideDbConfigProperties.driverClassName
        val url = medicationGuideDbConfigProperties.url
        val un = medicationGuideDbConfigProperties.username
        val pw = medicationGuideDbConfigProperties.password
        return HikariDataSource().apply {
            driverClassName = driverName
            username = un
            password = pw
            jdbcUrl = url
            connection.autoCommit = true
        }
    }

    @PreDestroy
    fun closeDataSource() {
        dataSource?.apply {
            //关闭数据源
            this.close()
        }
    }
}

@ConfigurationProperties(prefix = "app.medication-guide.db")
class MedicationGuideDbConfigProperties(
    var username: String = "app_dev",
    var password: String = "app_dev",
    var driverClassName: String = "com.mysql.cj.jdbc.Driver",
    var url: String = "jdbc:mysql://192.168.3.100:3306/lake"
)