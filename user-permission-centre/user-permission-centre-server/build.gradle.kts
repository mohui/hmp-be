plugins {
    // sqlex 插件
    id("me.danwi.sqlex")
}

dependencies {
    implementation(project(":user-permission-centre:security"))
    implementation(project(":user-permission-centre:user-permission-centre-api"))
    implementation(project(":operation-log:operation-log-sdk"))
    implementation(project(":doctor-patient-management:doctor-patient-management-api"))
    implementation(project(":health-scheme:health-scheme-api"))
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

    // sqlex 由于插件原因，无法使用依赖管理
    implementation("me.danwi.sqlex:core")
    implementation("me.danwi.sqlex:core-kotlin")


    // hutool 工具 自己需要什么加什么 参考 https://www.hutool.cn/docs/#/
    
    // 开发工具
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    // 开发 代理SQL日志
    implementation("p6spy:p6spy")


    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}
