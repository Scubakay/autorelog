plugins {
    `maven-publish`
    id("fabric-loom")
    //id("dev.kikugie.j52j")
    id("me.modmuss50.mod-publish-plugin")
    id("com.star-zero.gradle.githook") version "1.2.1"
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

class ModDependencies {
    operator fun get(name: String) = property("deps.$name").toString()
}

class DevDependencies {
    operator fun get(name: String) = property("dev.$name").toString()
}

val mod = ModData()
val deps = ModDependencies()
val dev = DevDependencies()
val mcVersion = stonecutter.current.version
val authmeVersion = dev["authme"]
val mcDep = property("mod.mc_dep").toString()

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set(mod.id) }

loom {
    mods {
        create("template") {
            sourceSet(sourceSets["main"])
        }
    }
}

stonecutter {
    // ConnectScreen was moved in 1.20.5
    replacement(eval(current.version, "<1.20.5"), "import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;", "import net.minecraft.client.gui.screen.ConnectScreen;", identifier = "reconnect_import")

    // Before 1.20 we need to get ServerInfo somewhere else
    replacement(eval(current.version, "<1.20"), "server = handler.getServerInfo();", "server = MinecraftClient.getInstance().getCurrentServerEntry();", identifier = "reconnect_serverinfo")

    // The disconnect method was introduced in GameMenuScreen in 1.20
    replacement(eval(current.version, "<1.20"), "import com.scubakay.autorelog.util.Reconnect;", "import com.scubakay.autorelog.util.Reconnect;\nimport net.minecraft.client.gui.widget.ButtonWidget;", identifier = "gamemenuscreenmixin_import")
    replacement(eval(current.version, "<1.20"), "@Inject(method = \"disconnect\", at = @At(\"HEAD\"))", "@Inject(method = \"method_19836\", at = @At(\"HEAD\"))", identifier = "gamemenuscreenmixin_inject")
    replacement(eval(current.version, "<1.20"), "injectDisconnect(CallbackInfo ci)", "injectDisconnect(ButtonWidget button, CallbackInfo ci)", identifier = "gamemenuscreenmixin_signature")

    // DrawContext was introduced in 1.20
    replacement(eval(current.version, "<1.20"), "import net.minecraft.client.gui.DrawContext;", "import net.minecraft.client.util.math.MatrixStack;\nimport net.minecraft.client.gui.DrawableHelper;", identifier = "screenmixin_import")
    replacement(eval(current.version, "<1.20"), "injectCountdown(DrawContext context", "injectCountdown(MatrixStack matrices", identifier = "screenmixin_signature")
    replacement(eval(current.version, "<1.20"), "context.drawTextWithShadow(this.textRenderer", "DrawableHelper.drawTextWithShadow(matrices, this.textRenderer", identifier = "screenmixin_drawwithshadow")
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
    fun fapi(vararg modules: String) = modules.forEach {
        modImplementation(fabricApi.module(it, deps["fabric_api"]))
    }

    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:$mcVersion+build.${deps["yarn_build"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${deps["fabric_loader"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${deps["fabric_api"]}")

    fapi(
        // Add modules from https://github.com/FabricMC/fabric
        "fabric-api-base",
        "fabric-command-api-v2",
        "fabric-lifecycle-events-v1"
    )

    // Dependencies
    modImplementation("maven.modrinth:midnightlib:${deps["midnightlib"]}")
    include("maven.modrinth:midnightlib:${deps["midnightlib"]}")

    // Dev mods
    modImplementation("maven.modrinth:modmenu:${dev["modmenu"]}-fabric")
    modImplementation("maven.modrinth:auth-me:${dev["authme"]}-fabric")
    if (stonecutter.eval(mcVersion, ">=1.21.6")) {
        modImplementation("maven.modrinth:resourceful-config:${dev["authmeconfig"]}-fabric")
    } else {
        modImplementation("maven.modrinth:cloth-config:${dev["authmeconfig"]}-fabric")
    }
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(mcVersion, ">=1.20.6")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

githook {
    hooks {
        register("pre-commit") {
            shell = "./gradlew \"Reset active project\""
        }
    }
}

tasks.processResources {
    // Fabric's mod id changed in 1.19.3, but we can still use the old one: https://fabricmc.net/2022/11/24/1193.html
    // By using "fabric" for all <1.20 versions we can avoid splitting up 1.19
    val fabricKey = if (stonecutter.eval(mcVersion, ">=1.19.3")) "fabric-api" else "fabric"

    inputs.property("id", mod.id)
    inputs.property("name", mod.name)
    inputs.property("version", mod.version)
    inputs.property("mcdep", mcDep)
    inputs.property("fabrickey", fabricKey)

    val map = mapOf(
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "mcdep" to mcDep,
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

publishMods {
    fun versionList(prop: String) = findProperty(prop)?.toString()
        ?.split("\\s+".toRegex())
        ?.map { it.trim() }
        ?: emptyList()
    val versions = versionList("mod.mc_targets")

    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName = "${mod.name} ${mod.version} for $mcVersion"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN")
        .getOrNull() == null
    // || providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(versions)
        requires {
            slug = "fabric-api"
        }
    }

//    curseforge {
//        projectId = property("publish.curseforge").toString()
//        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
//        minecraftVersions.add(mcVersion)
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
            version = mcVersion

            from(components["java"])
        }
    }
}