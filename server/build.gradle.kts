import org.gradle.kotlin.dsl.support.kotlinCompilerOptions

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "net.msukanen.splintersector_server"
version = "0.1.0"
application {
    mainClass.set("net.msukanen.splintersector_server.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.exposed.core)
    implementation(libs.exposed.crypt)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.json)
    implementation(libs.exposed.money)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.mysql.connector.java)
    implementation(libs.jjwt)
    //implementation(libs.exposed.spring.boot.starter)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.client.content.negotiation)
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_21)
        portMappings.set(listOf(
            io.ktor.plugin.features.DockerPortMapping(
                15551, 15551,
                io.ktor.plugin.features.DockerPortMappingProtocol.TCP
            )
        ))
        localImageName.set("splintersector-server")
        imageTag.set("0.1.0-prealpha")
        environmentVariable("DATABASE_URL", "jdbc:mysql://mysql:3306/sss_test")
    }
}

jib {
    from {
        image = "eclipse-temurin:21-jdk-alpine"
    }
}

tasks {
    named<Task>("jibDockerBuild") {
        doLast {
            exec {
                commandLine("docker-compose", "up", "-d")
            }
        }
    }
}
