pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7-alpha.21"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        versions("1.19", "1.19.3", "1.20", "1.20.5", "1.21")
        vcsVersion = "1.21"
    }
    create(rootProject)
}

rootProject.name = "AutoRelog"