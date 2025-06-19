pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7-alpha.22"
}

val devVersion = providers.gradleProperty("mod.dev_version").get()
val gitVersion = providers.gradleProperty("mod.git_version").get()

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        vers("dev", devVersion)
        versions("1.21", "1.20.5", "1.20", "1.19.3", "1.19")
        vcsVersion = gitVersion
    }
    create(rootProject)
}

rootProject.name = "AutoRelog"