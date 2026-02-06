import io.wispforest.helpers.Utils
import io.wispforest.helpers.Extensions.modrinth
import io.wispforest.helpers.Extensions.modrinthImplementation

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
    //"developmentFabric" { extendsFrom(common) }
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
    maven ("https://maven.florens.be/releases")
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

    modrinthImplementation(
        "artifacts" to "rPIBUOto",
        "cloth-config" to "15.0.140+fabric",
    )
    modImplementation("be.florens:expandability-fabric:12.0.0")
    implementation("com.electronwill.night-config:toml:3.8.0")

    modrinthImplementation("map-atlases" to "fabric_1.21-6.3.6")

    modrinthImplementation(
        "gliders" to "1.1.8+fabric",
        "common-network" to "8yTr4pcd"
    )

    modrinthImplementation("more-mob-variants" to "1.3.1.1")

    modrinthImplementation(
        "supplementaries" to "fabric_1.21-3.4.14",
        "moonlight" to "1.21-2.23.7-fabric"
    )

    modrinthImplementation(
        "relics-rpg" to "1.0.9+1.21.1",
        "wizards" to "2.5.2+1.21.1",
        "ranged-weapon-api" to "2.1.1+1.21.1",
        "spell-power" to "1.3.1+1.21.1",
        "azurelib-armor" to "KEpNCz75",
        "structure-pool-api" to "1.1.3+1.21.1",
        "runes" to "1.1.3+1.21.1",
        "bundle-api" to "1.0.4",
        "spell-engine" to "1.7.3+1.21.1",
        "cloth-config" to "15.0.140+fabric",
        "playeranimator" to "2.0.1+1.21.1-fabric",
        //"tiny-config" to "3.0.0" 2.3.2
    )
    implementation("com.github.ZsoltMolnarrr:TinyConfig:2.3.2")

    modrinthImplementation("charm-of-undying" to "9.1.0+1.21.1")

    modrinthImplementation(
        "basicweapons" to "2.1.2+1.21.1",
        "bonded-basic-weapons-compat" to "2.0.0+1.21.1",
        "bonded" to "1.2.2+1.21.1",
        "forge-config-api-port" to "v21.1.4-1.21.1-Fabric",
        "architectury-api" to "13.0.8+fabric",
        "amber" to "S5OLOL4r"
    )

    modrinthImplementation(
        "sword-blocking-mechanics" to "v21.1.1-1.21.1-Fabric",
        "forge-config-api-port" to "v21.1.4-1.21.1-Fabric",
        "puzzles-lib" to "v21.1.38-1.21.1-Fabric"
    )

    modrinthImplementation(
        "inmis" to "2.8.2-1.21.1",
        "cloth-config" to "15.0.140+fabric",
    )
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