val isLanCiServer = (System.getenv("CI_RUNNER_TAGS") ?: "").contains("LAN")
val currentGitBranch = System.getenv("CI_COMMIT_REF_NAME") ?: "temp"
val imageNameFromEnv: String? = System.getenv("IMAGE_NAME")
val imageTag = System.getenv("IMAGE_TAG") ?: ""
val ciUsername = System.getenv(extra["CI_UN_ENV_KEY"].toString()) ?: "a"
val ciPassword = System.getenv(extra["CI_PD_ENV_KEY"].toString()) ?: "a"
println("currentGitBranch:${currentGitBranch}")
println("imageTag:${imageTag}")

plugins {
    id("com.google.cloud.tools.jib") apply false
}

allprojects {
    val rootProjectName = project.rootProject.name
    group = "" + group + (if (project.parent == null) "" else ".${rootProjectName}")
    version = "" + version + (if (System.getenv("IS_RELEASE") == null) "-SNAPSHOT" else "")
    val moduleName = project.name
    val isSnapshot = version.toString().endsWith("SNAPSHOT")
    ext.set("moduleName", moduleName)
    ext.set("isLanCiServer", isLanCiServer)
    ext.set("currentGitBranch", currentGitBranch)
    ext.set("imageTag", imageTag)
    ext.set("isSnapshotVersion", isSnapshot)
    ext.set("ciUsername", ciUsername)
    ext.set("ciPassword", ciPassword)

    tasks.withType<Copy> {
        filter<org.apache.tools.ant.filters.ReplaceTokens>(
            "tokens" to mapOf(
                "moduleName" to if (rootProjectName == moduleName) moduleName else "${rootProjectName}-${moduleName}"
            )
        )
    }

    configurations.all {
        // check for updates every build
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }

    // 仓库
    repositories {
        repositories {
            // mavenLocal()
            maven(getMavenArtifactRepo(getMavenArtifactUri(isLanCiServer) + "maven-public/"))
        }
    }

    apply<MavenPublishPlugin>()
    // 发布仓库
    configure<PublishingExtension> {
        repositories {
            maven {
                val repoUrl = "${getMavenArtifactUri(isLanCiServer)}${if (isSnapshot) "maven-snapshots" else "maven-releases"}/"
                // 读取环境变量的值 ./gradlew publishAllPublicationsToBjknrtRepository
                if (ciUsername != "" && ciPassword != "") {
                    maven(
                        getMavenArtifactRepo(
                            repoUrl, "bjknrt", ciUsername, ciPassword
                        )
                    )
                }
            }
        }
    }
    pluginManager.withPlugin("java-library") {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("java") {
                    from(components["java"])
                }
            }
        }
    }
    pluginManager.withPlugin("java-platform") {
        configure<JavaPlatformExtension> {
            allowDependencies()
        }
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("java") {
                    from(components["javaPlatform"])
                }
            }
        }
    }
    pluginManager.withPlugin("com.google.cloud.tools.jib") {
        configure<com.google.cloud.tools.jib.gradle.JibExtension> {
            setAllowInsecureRegistries(true)
            from {
                image = "192.168.3.201:8080/library/eclipse-temurin:17.0.6_10-jdk-focal"
                auth {
                    username = ciUsername
                    password = ciPassword
                }
            }
            to {
                image = imageNameFromEnv ?: "192.168.3.201:8082/bjknrt/${rootProjectName}/${moduleName}"
                auth {
                    username = ciUsername
                    password = ciPassword
                }
                tags = setOf("latest")
                if (imageTag.isNotBlank()) {
                    tags = setOf(imageTag)
                }
            }
            container {
                appRoot = "/bjknrt"
                workingDirectory = "/bjknrt"
                expandClasspathDependencies = true
                creationTime.set(java.time.ZonedDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME))
                filesModificationTime.set(creationTime.get())

                val debugPort = System.getenv("DEBUG_PORT") ?: System.getProperty("DEBUG_PORT")
                jvmFlags = mutableListOf(
                    "-XX:+HeapDumpOnOutOfMemoryError",
                    "-XX:HeapDumpPath=/bjknrt/logs/HeapDump/",
                    "-XX:+UseContainerSupport",
                    "-XX:MaxRAMPercentage=75",
                    "-Duser.timezone=Asia/Shanghai",
                    "-Dproject.name=${moduleName}"
                ).apply {
                    if (isSnapshot) {
                        add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$debugPort")
                    }
                }

                ports = mutableListOf("8080", debugPort)
                format = com.google.cloud.tools.jib.api.buildplan.ImageFormat.Docker
            }
        }
    }
}

fun getMavenArtifactUri(isLanCiServer: Boolean): String {
    val repositoryUri = if (isLanCiServer) {
        "http://192.168.3.201:8081/repository/"
    } else {
        "https://repo.gate.bjknrt.com/repository/"
//        "http://192.168.3.201:8081/repository/"
    }
    return repositoryUri
}

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
