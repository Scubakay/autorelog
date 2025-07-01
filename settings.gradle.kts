pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7-beta.3"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        vers("dev", "1.21.7")
        versions("1.21.6", "1.21", "1.20.5", "1.20.2", "1.20", "1.19.3", "1.19")
        vcsVersion = "dev"
    }
    create(rootProject)
}

rootProject.name = "AutoRelog"