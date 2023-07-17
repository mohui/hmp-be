package com.bjknrt.framework.test

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.test.db")
data class TestDbConfigProperty(
    var username: String = "root",
    var password: String = "1234qwer",
    var driverClassName: String = "com.mysql.cj.jdbc.Driver",
    var url: String = "jdbc:mysql://192.168.3.200:3306/",
)