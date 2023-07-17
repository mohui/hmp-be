plugins {
    `java-platform`
    `maven-publish`
}

dependencies {
    val dgpVer = property("dgpVer")
    val katoVer = property("katoVer")
    val p6spyVer = property("spyVer")
    val mysqlVer = property("mysqlVer")
    val jwtVersion = property("jjwtVer")

    api(platform("cn.hutool:hutool-bom:${property("hutoolVer")}"))
    api(platform("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}"))
    api(platform("org.springframework.boot:spring-boot-dependencies:${property("springBootVer")}"))
    api(platform("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}"))
    api(platform("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${property("springCloudAlibabaVersion")}"))

    constraints {
        api("mysql:mysql-connector-java:$mysqlVer")
        api("p6spy:p6spy:$p6spyVer")
        api("me.danwi.kato:server:$katoVer")
        api("me.danwi.kato:client:$katoVer")
        api("com.bjknrt.dgp:dgp-spring-boot-starter:${dgpVer}")

        api("io.jsonwebtoken:jjwt-dependency:${jwtVersion}")
        api("io.jsonwebtoken:jjwt-impl:${jwtVersion}")
        api("io.jsonwebtoken:jjwt-jackson:${jwtVersion}")
    }
}