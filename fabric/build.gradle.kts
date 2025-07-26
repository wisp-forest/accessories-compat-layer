import helpers.Utils

plugins {
    id("multiloader-base")
    id("multiloader-publishing")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    fabric {
        platformPackage = "fabric"
    }
}

val common by configurations.creating
val shadowCommon by configurations.creating

configurations {
    common
    shadowCommon // Don't use shadow from the shadow plugin since it *excludes* files.
    compileClasspath { extendsFrom(common) }
    runtimeClasspath { extendsFrom(common) }
    "developmentFabric" { extendsFrom(common) }
}

repositories {
    mavenCentral()
    gradlePluginPortal()

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

    // Mixin Squard
    maven("https://maven.bawnorton.com/releases")
}

dependencies {
    "common"(project(":common", "namedElements")) { this.setTransitive(false) }
    "shadowCommon"(project(":common", "transformProductionFabric")) { this.setTransitive(false) }

    // Core Libs
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    // --

    // General Libs
    modCompileOnly(libs.modmenu)
    modLocalRuntime(libs.modmenu)
    //--

    modImplementation(libs.trinkets)

    modImplementation(libs.accessories.fabric)

    annotationProcessor(libs.mixin.squared.fabric)
    implementation(libs.mixin.squared.fabric)
    include(libs.mixin.squared.fabric)
}

loom {
    accessWidenerPath = file("src/main/resources/${rootProject.property("mod_id")}-fabric.accesswidener")
    runs (Utils.getSetupRunsAction(project))
}

tasks.shadowJar {
    exclude("architectury.common.json")

    configurations = mutableListOf<FileCollection>(project.configurations["shadowCommon"])
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    injectAccessWidener = true
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