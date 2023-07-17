package com.bjknrt.message.board

import me.danwi.sqlex.core.DaoFactory
import me.danwi.sqlex.spring.ImportSqlEx
import me.danwi.sqlex.spring.SpringDaoFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@ImportSqlEx(Repository::class, factoryName = "hmpMessageBoardServerFactory") // sqlex
class MessageBoardAppConfig(val dataSource: DataSource) {

    /**
     * Sqlex数据源工厂
     */
    @Bean
    fun hmpMessageBoardServerFactory(): DaoFactory {
        val factory = SpringDaoFactory(dataSource, Repository::class.java)
        factory.migrate()
        factory.check()
        return factory
    }
}