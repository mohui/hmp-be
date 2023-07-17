plugins {
    // sqlex 插件
    id("me.danwi.sqlex")
}

dependencies {
    implementation(project(":doctor-patient-management:doctor-patient-management-api"))
    implementation(project(":message-board:message-board-api"))
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

    // hutool 工具 自己需要什么加什么 参考 https://www.hutool.cn/docs/#/
    
    // 开发工具
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
