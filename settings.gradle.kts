pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.10"
}

val devVersion: String = providers.gradleProperty("settings.devVersion").orNull
    ?: error("devVersion property not found in gradle.properties")
val versions: List<String> = providers.gradleProperty("settings.versions").orNull
    ?.split(",")
    ?.map { it.trim() }
    ?: error("settings.versions property not found in gradle.properties")

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        version("dev", devVersion)
        versions(*versions.toTypedArray())
        vcsVersion = providers.gradleProperty("settings.vcsVersion").orNull
            ?: error("vcsVersion property not found in gradle.properties")
    }
    create(rootProject)
}

rootProject.name = "AutoRelog"