plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    id("com.google.cloud.tools.jib")

    id("org.springframework.boot")
    // sqlex 插件
    id("me.danwi.sqlex")
}
dependencies {
    implementation(project(":framework-webapi"))
    kapt(project(":framework-webapi"))
    implementation(project(":medication-remind:medication-remind-server"))
    implementation(project(":operation-log:operation-log-server"))
    implementation(project(":operation-log:operation-log-sdk"))
    implementation(project(":question-answering-system:question-answering-system-server"))
    implementation(project(":doctor-patient-management:doctor-patient-management-server"))
    implementation(project(":health-indicator:health-indicator-server"))
    implementation(project(":health-scheme:health-scheme-server"))
    implementation(project(":article-service:article-service-server"))
    implementation(project(":user-permission-centre:user-permission-centre-server"))
    implementation(project(":message-board:message-board-server"))
    implementation(project(":wechat-service:wechat-service-server"))
    implementation(project(":statistic-analysis:statistic-analysis-server"))
    implementation(project(":medication-guide:medication-guide-server"))

    implementation("org.springframework.boot:spring-boot-starter-logging")
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

    // 定时job+异步job
    implementation("org.jobrunr:jobrunr-spring-boot-2-starter:6.2.3"){
        exclude("ch.qos.logback","logback-classic")
        exclude("ch.qos.logback","logback-core")
        exclude("org.slf4j","slf4j-api")
    }

    // redis
    implementation("org.apache.commons:commons-pool2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("p6spy:p6spy")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // 开发工具
    developmentOnly(platform(project(":cloud-dependencies")))
}

apply<JavaPlugin>()
apply<JavaLibraryPlugin>()
apply<MavenPublishPlugin>()
apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "org.jetbrains.kotlin.kapt")
apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
apply(plugin = "org.jetbrains.kotlin.plugin.spring")

val jvmVersion = "1.8"
val moduleName = ext["moduleName"] as String
val isSnapshot = ext["isSnapshotVersion"] as Boolean
val currentGitBranch = ext["currentGitBranch"] as String
val imageTag = ext["imageTag"] as String
val isLanCiServer = ext["isLanCiServer"] as Boolean
val ciUsername = ext["ciUsername"] as String
val ciPassword = ext["ciPassword"] as String

java.sourceCompatibility = JavaVersion.VERSION_1_8
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = jvmVersion
    targetCompatibility = jvmVersion
    inputs.files(tasks.named("processResources"))
    options.compilerArgs.add("-parameters")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = jvmVersion
        javaParameters = true
    }
    inputs.files(tasks.named("processResources"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configure<org.jetbrains.kotlin.gradle.plugin.KaptExtension> {
    keepJavacAnnotationProcessors = true
}

configure<JavaPluginExtension> {
    withSourcesJar()
}