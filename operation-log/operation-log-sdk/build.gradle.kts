plugins {
}

dependencies {
    api(project(":operation-log:operation-log-api"))

    implementation(project(":user-permission-centre:security"))

    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.aspectj:aspectjweaver")

    // 开发工具
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
