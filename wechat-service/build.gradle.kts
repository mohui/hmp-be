plugins {
    kotlin("jvm")
    kotlin("kapt") apply false
    kotlin("plugin.jpa") apply false
    kotlin("plugin.spring") apply false
}

subprojects {

    apply<JavaPlugin>()
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    val jvmVer = "1.8"
    java.sourceCompatibility = JavaVersion.VERSION_1_8

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = jvmVer
        targetCompatibility = jvmVer
        inputs.files(tasks.named("processResources"))
        options.compilerArgs.add("-parameters")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            javaParameters = true
            jvmTarget = jvmVer
        }
        inputs.files(tasks.named("processResources"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configure<org.jetbrains.kotlin.gradle.plugin.KaptExtension> {
        keepJavacAnnotationProcessors = true
    }

    dependencies {
        implementation(project(":framework-webapi"))
        add("kapt", project(":framework-webapi"))

        // kotlin
        implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
    }
}