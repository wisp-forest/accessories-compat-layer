import helpers.Utils
import java.util.function.BiConsumer

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("multiloader-base")
}

architectury {
    platformSetupLoomIde()
    neoForge {
        platformPackage = "neoforge"
    }
}

val common by configurations.creating
val shadowCommon by configurations.creating

configurations {
    common
    shadowCommon // Don't use shadow from the shadow plugin since it *excludes* files.
    compileClasspath { extendsFrom(common) }
    runtimeClasspath { extendsFrom(common) }
    //"developmentNeoForge" { extendsFrom(common) }
}

repositories {
    mavenCentral()
    gradlePluginPortal()

    // Neoforge Lib
    maven("https://maven.neoforged.net/releases/")
    // --

    // Fabric API Event Lib
    maven("https://maven.su5ed.dev/releases")
    // --
    
    // oωo (owo-lib) and Endec Lib
    maven("https://maven.wispforest.io/releases")
    // --

    // REI Item Viewer
    maven("https://maven.shedaniel.me/")
    maven("https://maven.architectury.dev/")
    // --

    // EMI Item Viewer
    maven("https://maven.terraformersmc.com/releases")
    // --

    maven("https://maven.ladysnake.org/releases")

    maven("https://maven.theillusivec4.top/")

    // Mixin Squard
    maven("https://maven.bawnorton.com/releases")
}

dependencies {
    fun fabricModule(dependencyMethod: BiConsumer<Dependency, Action<Dependency>>, vararg moduleNames: String, action: Action<Dependency>? = null) {
        for (moduleName in moduleNames) {
            dependencyMethod.accept(fabricApi.module(moduleName, libs.versions.fabric.api.asProvider().get())){
                (this as ModuleDependency).exclude(group = "fabric-api", module = "")

                action?.execute(this)
            }
        }
    }

    "common"(project(":common", "namedElements")) { this.setTransitive(false) }
    "shadowCommon"(project(":common", "transformProductionNeoForge")) { this.setTransitive(false) }

    // Core Libs
    neoForge(libs.neoforge)
    // --

    // Neoforge: Required as these are General Libs that are used by owo and can be used but must be added to runtime path due to not being mods
    forgeRuntimeLibrary(libs.endec)
    forgeRuntimeLibrary(libs.endec.netty)
    forgeRuntimeLibrary(libs.endec.gson)
    forgeRuntimeLibrary(libs.endec.jankson)
    forgeRuntimeLibrary(libs.jankson)
    // --

    //modCompileOnly(libs.trinkets)
    fabricModule(this::modCompileOnly, "fabric-resource-loader-v0", "fabric-events-interaction-v0")

    modImplementation(libs.curios)

    modImplementation(libs.accessories.neoforge)

    annotationProcessor(libs.mixin.squared.neoforge)
    implementation(libs.mixin.squared.neoforge)
    include(libs.mixin.squared.neoforge)
}

loom {
    accessWidenerPath = project(":common").loom.accessWidenerPath

    runs (Utils.getSetupRunsAction(project))

    neoForge  {}
}

tasks.shadowJar {
    exclude("fabric.mod.json")
    exclude("architectury.common.json")

    configurations = mutableListOf<FileCollection>(project.configurations["shadowCommon"]);
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveClassifier.set("")
}

tasks.sourcesJar {
    val commonSources = project(":common").tasks.sourcesJar
    dependsOn(commonSources)
    from(commonSources.get().archiveFile.map { zipTree(it) })
}

with(components["java"] as AdhocComponentWithVariants) {
    withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) { skip() }
}