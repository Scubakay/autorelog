pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.6"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        versions("1.19", "1.20", "1.20.5", "1.21")
        vcsVersion = "1.21"
    }
    create(rootProject)
}

rootProject.name = "AutoRelog"