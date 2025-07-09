@file:Suppress("UnstableApiUsage")

import dev.ithundxr.silk.ChangelogText

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
    id("dev.ithundxr.silk")
}

val loader = prop("loom.platform")!!
val loaderCap = loader.upperCaseFirst()
val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}.project

val ci = System.getenv("CI")?.toBoolean() ?: false
val release = System.getenv("RELEASE")?.toBoolean() ?: false
val nightly = ci && !release
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()

version = "${mod.version}${if (release) "" else "-dev"}+mc.${minecraft}-${loader}${if (nightly) "-build.${buildNumber}" else ""}"
group = "${mod.group}.$loader"
base.archivesName = mod.id

architectury {
    platformSetupLoomIde()
    neoForge()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}
val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}
configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentNeoForge").extendsFrom(commonBundle)
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    silentMojangMappingsLicense()
    accessWidenerPath = common.loom.accessWidenerPath
    runConfigs {
        create("DataGenFabric") {
            data()

            name("Fabric Data Generation")
            programArgs("--all", "--mod", mod.id)
            programArgs("--output", "${project.rootProject.file("fabric/src/generated/resources")}")
            programArgs("--existing", "${project.rootProject.file("src/main/resources")}")
            vmArg("-Dtestmod.datagen.platform=fabric")
        }
        create("DataGenNeoForge") {
            data()

            name("NeoForge Data Generation")
            programArgs("--all", "--mod", mod.id)
            programArgs("--output", "${project.rootProject.file("neoforge/src/generated/resources")}")
            programArgs("--existing", "${project.rootProject.file("src/main/resources")}")
            vmArg("-Dtestmod.datagen.platform=neoforge")
        }
        all {
            isIdeConfigGenerated = true
            runDir = "../../../run"
            vmArgs("-Dmixin.debug.export=true")
        }
    }
}

dependencies {
    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionNeoForge")) { isTransitive = false }

    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        officialMojangMappings { nameSyntheticMembers = false }
        parchment("org.parchmentmc.data:parchment-$minecraft:${common.mod.dep("parchment_version")}@zip")
    })

    "neoForge"("net.neoforged:neoforge:${common.mod.dep("neoforge_loader")}")

    files("build/libs/visceralib-${mod.dep("visceralib")}-dev+mc.$minecraft-$loader-${mod.dep("visceralib_build")}.jar").let {
        implementation(it)
        annotationProcessor(it)
        include(it)
    }

    "net.createmod.ponder:Ponder-NeoForge-$minecraft:${common.mod.dep("ponder")}".let {
        modImplementation(it)
        include(it)
    }
    compileOnly("dev.engine-room.flywheel:flywheel-$loader-api-$minecraft:${common.mod.dep("flywheel")}")
    "dev.engine-room.flywheel:flywheel-$loader-$minecraft:${common.mod.dep("flywheel")}".let {
        modImplementation(it)
        include(it)
    }
    "foundry.veil:veil-$loader-$minecraft:${common.mod.dep("veil")}".let {
        implementation(it)
        include(it)
    }

    "io.github.llamalad7:mixinextras-$loader:${mod.dep("mixin_extras")}".let {
        implementation(it)
        include(it)
    }

    modRuntimeOnly("dev.engine-room.vanillin:vanillin-$loader-$minecraft:${mod.dep("vanillin")}")
    modCompileOnly("maven.modrinth:sodium:mc$minecraft-${mod.dep("sodium")}-$loader")
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.jar { archiveClassifier = "dev" }

tasks.remapJar {
    injectAccessWidener.set(true)
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
    exclude("fabric.mod.json", "architectury.common.json")
}

tasks.processResources {
    properties(listOf("META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id, "name" to mod.name, "license" to mod.license,
        "version" to mod.version, "minecraft" to common.mod.prop("mc_dep_forgelike"),
        "authors" to mod.authors, "description" to mod.description,
        "flywheel" to common.mod.dep("flywheel_range_forge"),
        "veil" to common.mod.dep("veil_range_forge"),
        "ponder" to common.mod.dep("ponder_range_forge"),
        "visceralib" to common.mod.dep("visceralib")
    )
}

sourceSets {
    main {
        resources { // include generated resources in resources
            srcDir("src/generated/resources")
            exclude("src/generated/resources/.cache")
        }
    }
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

tasks.register("runDataGen") {
    group = "loom"
    description = "Generate data for " + mod.id
    dependsOn("runDataGenFabric", "runDataGenNeoForge")
}

// Modmuss Publish
publishMods {
    file = tasks.remapJar.get().archiveFile
    changelog = ChangelogText.getChangelogText(rootProject).toString()
    displayName = "${common.mod.version} for $loaderCap $minecraft"
    modLoaders.add("neoforge")
    type = ALPHA

    curseforge {
        projectId = "publish.curseforge"
        accessToken = System.getenv("CURSEFORGE_TOKEN")
        minecraftVersions.add(minecraft)

        embeds (
        )
    }
    modrinth {
        projectId = "publish.modrinth"
        accessToken = System.getenv("MODRINTH_TOKEN")
        minecraftVersions.add(minecraft)

        embeds(
            "veil"
        )
    }

    dryRun = System.getenv("DRYRUN")?.toBoolean() ?: true
}