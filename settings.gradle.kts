pluginManagement {
    val isLanCiServer = (System.getenv("CI_RUNNER_TAGS") ?: "").contains("LAN")
    println("isLanCiServer:${isLanCiServer}")
    val ciUsername = System.getenv(extra["CI_UN_ENV_KEY"].toString()) ?: ""
    val ciPassword = System.getenv(extra["CI_PD_ENV_KEY"].toString()) ?: ""

    val sqlexVer: String by settings
    val kotlinPluginVer: String by settings
    val jibVer: String by settings
    val springBootVer: String by settings

    // 统一插件版本管理
    plugins {
        id("com.google.cloud.tools.jib") version jibVer
        id("org.springframework.boot") version springBootVer

        kotlin("jvm") version kotlinPluginVer
        kotlin("kapt") version kotlinPluginVer
        kotlin("plugin.spring") version kotlinPluginVer
        kotlin("plugin.jpa") version kotlinPluginVer

        id("me.danwi.sqlex") version sqlexVer
    }

    repositories {
        fun getMavenArtifactRepo(
            url: String,
            repoName: String = "DEFAULT_REPO_NAME",
            _username: String = "bjknrt",
            _password: String = "bjknrt"
        ): (MavenArtifactRepository).() -> Unit {
            return {
                name = repoName
                isAllowInsecureProtocol = true
                setUrl(url)
                credentials {
                    username = _username
                    password = _password
                }
            }
        }

        val url = if (isLanCiServer) {
            "http://192.168.3.201:8081/repository/maven-public/"
        } else {
//            "http://192.168.3.201:8081/repository/maven-public/"
            "https://repo.gate.bjknrt.com/repository/maven-public/"
        }
        println("maven:${url}")
        maven(getMavenArtifactRepo(url))
    }
}

rootProject.name = "hmp"
include("cloud-dependencies")
include("framework-webapi")
include("web")
include("user-permission-centre", "user-permission-centre:security", "user-permission-centre:user-permission-centre-api", "user-permission-centre:user-permission-centre-server")
include("operation-log", "operation-log:operation-log-api", "operation-log:operation-log-sdk", "operation-log:operation-log-server")
include("message-board", "message-board:message-board-api", "message-board:message-board-server")
include("article-service", "article-service:article-service-api", "article-service:article-service-server")
include("medication-guide", "medication-guide:medication-guide-api", "medication-guide:medication-guide-server")
include("statistic-analysis", "statistic-analysis:statistic-analysis-api", "statistic-analysis:statistic-analysis-server")
include("health-indicator", "health-indicator:health-indicator-api", "health-indicator:health-indicator-server")
include("doctor-patient-management", "doctor-patient-management:doctor-patient-management-api", "doctor-patient-management:doctor-patient-management-server")
include("wechat-service", "wechat-service:wechat-service-api", "wechat-service:wechat-service-server")
include("health-scheme", "health-scheme:health-scheme-api", "health-scheme:health-scheme-server")
include("medication-remind", "medication-remind:medication-remind-api", "medication-remind:medication-remind-server")
include("question-answering-system", "question-answering-system:question-answering-system-api", "question-answering-system:question-answering-system-server")
