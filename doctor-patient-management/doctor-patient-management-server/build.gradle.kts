plugins {
    // sqlex 插件
    id("me.danwi.sqlex")
}

dependencies {
    implementation(project(":question-answering-system:question-answering-system-api"))
    implementation(project(":wechat-service:wechat-service-api"))
    implementation(project(":health-scheme:health-scheme-api"))
    implementation(project(":health-indicator:health-indicator-api"))
    implementation(project(":user-permission-centre:user-permission-centre-api"))
    implementation(project(":operation-log:operation-log-sdk"))
    implementation(project(":user-permission-centre:security"))
    implementation(project(":doctor-patient-management:doctor-patient-management-api"))
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
    implementation("p6spy:p6spy")
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
    implementation("com.google.zxing:core:3.3.3")
    
    // 开发工具
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
