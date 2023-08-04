plugins {
    `java-library`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.plugin.publishing)
}

group = "com.bybutter.sisyphus.tools"
version = "2.0.0"
description = "Plugin for easy configuring Gradle and plugins in Sisyphus Framework"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.gradle.kotlin)

    compileOnly(libs.nebula.publishing)
    compileOnly(libs.nebula.info)
    compileOnly(libs.nebula.contacts)
    compileOnly(libs.gradle.docker)
    compileOnly(libs.gradle.spring)
    compileOnly(libs.gradle.ktlint)

    testImplementation(kotlin("test"))
}

gradlePlugin {
    website.set("https://github.com/ButterCam/sisyphus")
    vcsUrl.set("https://github.com/ButterCam/sisyphus-project-gradle-plugin")

    plugins {
        create("sisyphus") {
            id = "com.bybutter.sisyphus.project"
            displayName = "Sisyphus Project Plugin"
            description = "Easy configure develop environment for project based on sisyphus framework."
            implementationClass = "com.bybutter.sisyphus.project.gradle.SisyphusProjectPlugin"
            tags.set(listOf("sisyphus", "project"))
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
