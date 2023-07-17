package com.bjknrt.health.scheme

import com.bjknrt.extension.LOGGER
import me.danwi.sqlex.core.DaoFactory
import me.danwi.sqlex.core.exception.SqlExCheckException
import me.danwi.sqlex.spring.ImportSqlEx
import me.danwi.sqlex.spring.SpringDaoFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@ImportSqlEx(Repository::class, factoryName = "hmpHealthSchemeServerFactory") // sqlex
class HealthSchemeAppConfig(val dataSource: DataSource) {

    /**
     * Sqlex数据源工厂
     */
    @Bean
    fun hmpHealthSchemeServerFactory(): DaoFactory {
        val factory = SpringDaoFactory(dataSource, Repository::class.java)
        factory.migrate()
        try {
            factory.check()
        } catch (e: SqlExCheckException) {
            LOGGER.error("数据库结构检查失败：${e.missed}")
            throw e
        }
        return factory
    }
}