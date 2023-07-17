plugins {
    // sqlex 插件
    id("me.danwi.sqlex")
}

dependencies {
    implementation(project(":medication-remind:medication-remind-api"))
    implementation(project(":wechat-service:wechat-service-api"))
    implementation(project(":article-service:article-service-api"))
    implementation(project(":user-permission-centre:security"))

    // 监控
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // web
    implementation("org.springframework.boot:spring-boot-starter-web")
    // 远程调用
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    // 客户端负载均衡
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    // 配置中心
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
    // 注册中心
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")

    // JDBC
    runtimeOnly("mysql:mysql-connector-java")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    // sqlex
    implementation("me.danwi.sqlex:core")
    implementation("me.danwi.sqlex:core-kotlin")

    // 定时job+异步job
    implementation("org.jobrunr:jobrunr-spring-boot-2-starter:6.2.3"){
        exclude("ch.qos.logback","logback-classic")
        exclude("ch.qos.logback","logback-core")
        exclude("org.slf4j","slf4j-api")
    }

    // redis
    implementation("org.apache.commons:commons-pool2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // hutool 工具 自己需要什么加什么 参考 https://www.hutool.cn/docs/#/

    // 开发工具
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    // 开发 代理SQL日志
    implementation("p6spy:p6spy")

    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

}
