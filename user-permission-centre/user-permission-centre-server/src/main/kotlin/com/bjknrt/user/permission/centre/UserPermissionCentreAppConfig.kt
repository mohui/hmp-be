package com.bjknrt.user.permission.centre

import me.danwi.sqlex.core.DaoFactory
import me.danwi.sqlex.core.exception.SqlExCheckException
import me.danwi.sqlex.core.exception.SqlExException
import me.danwi.sqlex.spring.ImportSqlEx
import me.danwi.sqlex.spring.SpringDaoFactory
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@ConfigurationPropertiesScan
@ImportSqlEx(Repository::class, factoryName = "hmpUserPermissionCentreServerFactory") // sqlex
class UserPermissionCentreAppConfig(val dataSource: DataSource) {


    /**
     * Sqlex数据源工厂
     */
    @Bean
    fun hmpUserPermissionCentreServerFactory(): DaoFactory {
        val factory = SpringDaoFactory(dataSource, Repository::class.java)
        factory.migrate()
        try {
            factory.check()
        } catch (e: SqlExCheckException) {
            throw SqlExException("数据库结构检查失败：${e.missed}")
        }
        return factory
    }
}