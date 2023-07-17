package com.bjknrt.framework.test

import com.bjknrt.extension.LOGGER
import com.bjknrt.framework.util.AppIdUtil
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import javax.annotation.PreDestroy
import javax.sql.DataSource

@EnableConfigurationProperties(TestDbConfigProperty::class)
class DbTestConfig {
    companion object {
        private val alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray()

        private fun createDbName(): String = "test_${AppIdUtil.nextNanoId(8, alphabet)}"

        private var initDbDataSource: HikariDataSource? = null

        private var dbName = createDbName()

        @Volatile
        var count = 0

        private fun createDb(driverName: String, baseUrl: String, un: String, pw: String) {
            initDbDataSource = HikariDataSource().apply {
                driverClassName = driverName
                username = un
                password = pw
                jdbcUrl = baseUrl
                connection.autoCommit = true
                connection.createStatement().use { statement ->
                    while (statement.executeQuery("SHOW DATABASES LIKE '${dbName}'").next()) {
                        // 如果重复
                        dbName = createDbName()
                    }
                    statement.executeUpdate("CREATE DATABASE IF NOT EXISTS $dbName")
                }
            }
            LOGGER.info("创建数据库，连接信息：$baseUrl$dbName")
        }
    }

    @Bean
    fun dataSource(datasourceProperties: TestDbConfigProperty): DataSource {
        synchronized(DbTestConfig::class) {
            ++count
            val driverName = datasourceProperties.driverClassName
            val baseUrl = if (datasourceProperties.url.last() == '/') {
                datasourceProperties.url
            } else {
                datasourceProperties.url + "/"
            }
            val un = datasourceProperties.username
            val pw = datasourceProperties.password
            val url = "${baseUrl}${dbName}"
            if (count == 1) {
                //只创建一次库
                createDb(driverName, baseUrl, un, pw)
            }
            LOGGER.info("使用数据库次数：${count}，连接信息：$url")
            return HikariDataSource().apply {
                driverClassName = driverName
                username = un
                password = pw
                jdbcUrl = url
            }
        }
    }

    @PreDestroy
    fun destroyTestDb() {
        synchronized(DbTestConfig::class) {
            if (count > 1) {
                --count
                LOGGER.info("销毁计数，数据库${dbName},次数：${count}")
            } else {
                initDbDataSource?.apply {
                    connection.createStatement().use { statement ->
                        statement.executeUpdate("DROP DATABASE $dbName")
                    }
                    //关闭数据源
                    this.close()
                }
                LOGGER.info("销毁数据库${dbName},次数：${count}")
            }
        }
    }

}