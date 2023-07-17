import org.jetbrains.kotlin.gradle.plugin.KaptExtension

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("me.danwi.sqlex")
}

dependencies {
    // 依赖管理
    api(platform(project(":cloud-dependencies")))
    annotationProcessor(platform(project(":cloud-dependencies"))) // platform传递版本管理，apt需要单独引用
    kapt(platform(project(":cloud-dependencies"))) // platform传递版本管理，apt需要单独引用

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    compileOnly("org.springframework:spring-tx")
    implementation("com.zaxxer:HikariCP")
    // web
    compileOnly("org.springframework:spring-webmvc")
    api("javax.servlet:javax.servlet-api")
    // validation
    api("org.springframework.boot:spring-boot-starter-validation")

    api("org.slf4j:slf4j-api")

    api("cn.hutool:hutool-core")
    api("cn.hutool:hutool-extra")

    // kato
    api("me.danwi.kato:server")
    api("me.danwi.kato:client")

    compileOnly("me.danwi.sqlex:core")

    // web
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}


val jvmVer = "1.8"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    all {
        resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.MINUTES)
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.HOURS)
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = jvmVer
    targetCompatibility = jvmVer
    inputs.files(tasks.named("processResources"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = jvmVer
    }
    inputs.files(tasks.named("processResources"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configure<KaptExtension> {
    showProcessorStats = false
    keepJavacAnnotationProcessors = true
}

configure<JavaPluginExtension> {
    withSourcesJar()
}
