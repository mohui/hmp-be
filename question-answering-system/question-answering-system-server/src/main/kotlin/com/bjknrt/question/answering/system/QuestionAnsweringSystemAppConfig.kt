package com.bjknrt.question.answering.system

import me.danwi.sqlex.core.DaoFactory
import me.danwi.sqlex.spring.ImportSqlEx
import me.danwi.sqlex.spring.SpringDaoFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
@ImportSqlEx(Repository::class, factoryName = "hmpQuestionAnsweringSystemServerFactory") // sqlex
class QuestionAnsweringSystemAppConfig(val dataSource: DataSource) {

    /**
     * Sqlex数据源工厂
     */
    @Bean
    fun hmpQuestionAnsweringSystemServerFactory(): DaoFactory {
        val factory = SpringDaoFactory(dataSource, Repository::class.java)
        factory.migrate()
        factory.check()
        return factory
    }
}