dependencies {
    compileOnly("me.danwi.kato:client")
    implementation(project(":user-permission-centre:user-permission-centre-api"))

    implementation("org.slf4j:slf4j-api")
    compileOnly("javax.servlet:javax.servlet-api")

    compileOnly("io.github.openfeign:feign-core")

    compileOnly("org.springframework.boot:spring-boot")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    compileOnly("org.springframework.cloud:spring-cloud-context")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")

    // spring web
    compileOnly("org.springframework:spring-web")
    // security
    api("org.springframework.security:spring-security-web")
    api("org.springframework.security:spring-security-config")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api")
    implementation("io.jsonwebtoken:jjwt-impl")
    implementation("io.jsonwebtoken:jjwt-jackson")
}