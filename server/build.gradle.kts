plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
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
    implementation(libs.exposed.core)
    implementation(libs.exposed.crypt)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.json)
    implementation(libs.exposed.money)
    //implementation(libs.exposed.spring.boot.starter)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
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
