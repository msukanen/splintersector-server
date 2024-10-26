plugins {
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

tasks.register("runAllTests") {
    dependsOn(":composeApp:test")
    dependsOn(":server:test")
    dependsOn(":shared:test")
}
