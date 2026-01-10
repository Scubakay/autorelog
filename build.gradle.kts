import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.modmuss50.mpp.ReleaseType

plugins {
    `maven-publish`
    id("fabric-loom")
    //id("dev.kikugie.j52j")
    id("me.modmuss50.mod-publish-plugin")
    id("com.gradleup.shadow") version "9.3.0"
    kotlin("jvm") version "2.2.10"
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.17"
}

//region Shadow libraries
val shadowLibrary = configurations.create("shadowLibrary") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(shadowLibrary)
    archiveClassifier = "dev-shadow"
//    relocate("com.github.lookfirst", "com.scubakay.github.lookfirst")
}

tasks {
    remapJar {
        inputFile = shadowJar.get().archiveFile
    }
}
//endregion

//region Mod information
class ModData {
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val description = property("mod.description").toString()
    val homepage = property("mod.homepage").toString()
    val repository = property("mod.repository").toString()
    val discord = property("mod.discord").toString()
}

class Environment {
    val version = stonecutter.current.version
    val range = property("mc.range").toString()
    val title = property("mc.title").toString()
    val targets = property("mc.targets").toString().split("\\s+".toRegex()).map { it.trim() }

    val yarnBuild = property("build.yarn_build").toString()
    val fabricLoader = property("build.fabric_loader").toString()
    val fabricApi = property("build.fabric_api").toString()

    val isFabric = fabricLoader != "[VERSIONED]"
    val loader = if (isFabric) "fabric" else "neoforge"

    val channel = ReleaseType.of(property("publish.channel").toString())
    val dryRun = property("publish.dry_run").toString().toBoolean() || property("mod.id").toString() == "template"
    val modrinthId = property("publish.modrinth").toString()
    val curseforgeId = property("publish.curseforge").toString()

    val modrinthRuntime = property("modrinth.runtime").toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }.filter { checkSpecified("modrinth.runtime.$it") }
    val modrinthImplementation = property("modrinth.implementation").toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }.filter { checkSpecified("modrinth.implementation.$it") }
    val modrinthInclude = property("modrinth.include").toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }.filter { checkSpecified("modrinth.include.$it") }

    val modrinthVersionedRuntime = project.properties.filter { (dep, _) -> dep.startsWith("modrinth.runtime.") }
    val modrinthVersionedImplementation = project.properties.filter { (dep, _) -> dep.startsWith("modrinth.implementation.") }
    val modrinthVersionedInclude = project.properties.filter { (dep, _) -> dep.startsWith("modrinth.include.") }

    val shadowedLibraries = project.properties.filter { (dep, _) -> dep.startsWith("shadow.") }

    fun checkSpecified(depName: String): Boolean {
        val property = findProperty(depName)
        // Allow auto resolution if property is missing or set to [VERSIONED]
        return property == null || property == "[VERSIONED]"
    }
}

val mod = ModData()
val env = Environment()

version = "${mod.version}+${env.title}"
group = mod.group
base { archivesName.set(mod.id) }
//endregion

loom {
    decompilers {
        get("vineflower").apply {
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
    }
}

fletchingTable {
    mixins.create("main") {
        mixin("default", "${mod.id}.mixins.json")
    }
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    minecraft("com.mojang:minecraft:${env.version}")
    mappings("net.fabricmc:yarn:${env.version}+build.${env.yarnBuild}:v2")
    if (env.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${env.fabricLoader}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${env.fabricApi}")
    }

    // Automated dependency resolution
    env.modrinthRuntime.forEach { dep -> modLocalRuntime(fletchingTable.modrinth(dep, stonecutter.current.version, env.loader)) }
    env.modrinthImplementation.forEach { dep -> modImplementation(fletchingTable.modrinth(dep, stonecutter.current.version, env.loader)) }
    env.modrinthInclude.forEach { dep ->
        modImplementation(fletchingTable.modrinth(dep, stonecutter.current.version, env.loader))
        include(fletchingTable.modrinth(dep, stonecutter.current.version, env.loader))
    }

    // Specific versions
    env.modrinthVersionedRuntime.forEach { dep -> modLocalRuntime("maven.modrinth:${dep.key.removePrefix("modrinth.runtime.")}:${property(dep.key).toString()}-${env.loader}") }
    env.modrinthVersionedImplementation.forEach { dep -> modImplementation("maven.modrinth:${dep.key.removePrefix("modrinth.implementation.")}:${property(dep.key).toString()}-${env.loader}") }
    env.modrinthVersionedInclude.forEach { dep ->
        modImplementation("maven.modrinth:${dep.key.removePrefix("modrinth.include.")}:${property(dep.key).toString()}-${env.loader}")
        include("maven.modrinth:${dep.key.removePrefix("modrinth.include.")}:${property(dep.key).toString()}-${env.loader}")
    }

    env.shadowedLibraries.forEach { dep ->
        implementation("${dep.key.removePrefix("shadow.")}:${property(dep.key).toString()}")
        shadowLibrary("${dep.key.removePrefix("shadow.")}:${property(dep.key).toString()}")
    }
}

//region Building
java {
    withSourcesJar()
    val java = if (stonecutter.eval(env.version, ">=1.20.6")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.processResources {
    // Fabric's mod id changed in 1.19.3, but we can still use the old one: https://fabricmc.net/2022/11/24/1193.html
    // By using "fabric" for all <1.20 versions we can avoid splitting up 1.19
    val fabricKey = if (stonecutter.eval(env.version, ">=1.20")) "fabric-api" else "fabric"

    inputs.property("version", mod.version)
    inputs.property("id", mod.id)
    inputs.property("name", mod.name)
    inputs.property("range", env.range)
    inputs.property("description", mod.description)
    inputs.property("homepage", mod.homepage)
    inputs.property("repository", mod.repository)
    inputs.property("discord", mod.discord)
    inputs.property("range", env.range)
    inputs.property("fabricKey", fabricKey)

    val map = mapOf(
        "version" to mod.version,
        "id" to mod.id,
        "name" to mod.name,
        "range" to env.range,
        "description" to mod.description,
        "homepage" to mod.homepage,
        "repository" to mod.repository,
        "discord" to mod.discord,
        "range" to env.range,
        "fabrickey" to fabricKey,
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}
//endregion

//region Publishing
publishMods {
    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName = "${mod.name} ${mod.version} for ${env.title}"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = env.channel
    modLoaders.add("fabric")

    dryRun = env.dryRun
            || (env.modrinthId != "..." && providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null)
            || (env.curseforgeId != "..." && providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null)

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(env.targets)
        requires {
            slug = "fabric-api"
        }
    }

//    Uncomment publishing order in stonecutter.gradle.kts too if you want to publish to Curseforge
//    curseforge {
//        projectId = property("publish.curseforge").toString()
//        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
//        minecraftVersions.addAll(mod.targets)
//        requires {
//            slug = "fabric-api"
//        }
//    }
}

publishing {
    repositories {
        maven("...") {
            name = "..."
            credentials(PasswordCredentials::class.java)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${property("mod.group")}.${mod.id}"
            artifactId = mod.version
            version = env.version

            from(components["java"])
        }
    }
}
//endregion