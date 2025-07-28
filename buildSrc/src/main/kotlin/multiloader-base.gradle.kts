import gradle.kotlin.dsl.accessors._049fa54a8c482cde147c17c9a808c570.main
import helpers.Utils
import helpers.UtilsJava
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.internal.declarativedsl.parsing.main

val libs get() = the<LibrariesForLibs>()

plugins {
    id("architectury-plugin")
    id("dev.architectury.loom")
    id("maven-publish")
    id("base")
    id("java")
    id("java-library")
}

architectury {
    minecraft = libs.versions.minecraft.asProvider().get()
    compileOnly()
}

base {
    archivesName = "${rootProject.property("mod_id")}${(if(project.name.isEmpty()) "" else "-${project.name.replace("-mojmap", "")}")}"
}

version = "${project.property("mod_version")}+${libs.versions.minecraft.base.get()}${(if(project.name.contains("mojmap")) "-mojmap" else "")}"
group = rootProject.property("mod_group")!!

loom {
    silentMojangMappingsLicense()

    if (project.path != ":common") {
        mods {
            try {
                named("main") {
                    this@named.sourceSet("main", project)
                    this@named.sourceSet("main", project(":common"))
                }
            } catch (e: UnknownDomainObjectException) {
                register("main") {
                    this@register.sourceSet("main", project)
                    this@register.sourceSet("main", project(":common"))
                }
            }


        }
    }
}

repositories {
    maven("https://maven.parchmentmc.org")
    maven("https://maven.wispforest.io/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases/")
    mavenCentral()
    gradlePluginPortal()
}

//--

val currentPlatform: String = Utils.currentPlatform(project)
val enabledTestmodPlatforms = (rootProject.property("enabled_testmod_platforms") as String).split(",");

if (currentPlatform != "common" && enabledTestmodPlatforms.contains(currentPlatform)) {
    sourceSets {
        create("testmod") {
            runtimeClasspath += sourceSets["main"].runtimeClasspath
            compileClasspath += sourceSets["main"].compileClasspath
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${libs.versions.minecraft.asProvider().get()}")

    mappings(
        loom.layered {
            this.officialMojangMappings()
            this.parchment("org.parchmentmc.data:parchment-${libs.versions.minecraft.asProvider().get()}:${libs.versions.parchment.get()}@zip")
        }
    )

    if (currentPlatform != "common" && enabledTestmodPlatforms.contains(currentPlatform)) {
        "testmodImplementation"(sourceSets.main.get().output)
    }

    // General Libs
    if (currentPlatform == "neoforge") {
        modApi(libs.owolib.neo)
        annotationProcessor(libs.owolib.neo)
    } else {
        modApi(libs.owolib.fabric)
        annotationProcessor(libs.owolib.fabric)
    }

    implementation(libs.endec)
    implementation(libs.endec.netty)
    implementation(libs.endec.gson)
    implementation(libs.endec.jankson)
    //--

    // Item Viewer Libs
    val enabledItemViewers = (rootProject.property("enabled_item_viewers") as String).split(",")
    val selectedItemViewer = (rootProject.property("selected_item_viewers") as String)

    if (currentPlatform == "common") {
        if (enabledItemViewers.contains("rei")) modCompileOnly(libs.bundles.rei.common)
        if (enabledItemViewers.contains("emi")) modCompileOnly("${libs.emi.common.get()}:api")
    } else {
        if (selectedItemViewer != "none" && !enabledItemViewers.contains(selectedItemViewer)) {
            throw IllegalStateException("Can not use $selectedItemViewer as given item viewer as such is not enabled!")
        }

        if (enabledItemViewers.contains("rei")) modCompileOnly(libs.create("rei.$currentPlatform.api").get())
        if (enabledItemViewers.contains("emi")) modCompileOnly("${libs.create("emi.$currentPlatform").get()}:api")
        when (selectedItemViewer) {
            "rei" -> modLocalRuntime(libs.create("rei.$currentPlatform").get())
            "emi" -> modLocalRuntime(libs.create("emi.$currentPlatform").get())
            "none" -> println("[Info]: No Item Viewer selected meaning none will be loaded in game!")
            else -> throw IllegalStateException("Selected '$selectedItemViewer' as item viewer but such was not found within the list of viewers: $enabledItemViewers");
        }
    }
    // --
}

tasks.processResources {
    val expandProps = mutableMapOf(
        "mod_id"                             to rootProject.property("mod_id"),
        "mod_name"                           to rootProject.property("mod_name"),
        "mod_version"                        to rootProject.property("mod_version"),
        "mod_license"                        to rootProject.property("mod_license"),
        "mod_credits"                        to rootProject.property("mod_credits"),
        "mod_authors"                        to rootProject.property("mod_authors"),
        "mod_contributors"                   to rootProject.property("mod_contributors"),
        "mod_group"                          to rootProject.property("mod_group"),
        "mod_description"                    to rootProject.property("mod_description"),
        "mod_issuepage"                      to rootProject.property("mod_issuepage"),
        "mod_sourcepage"                     to rootProject.property("mod_sourcepage"),
        "minecraft_version"                  to libs.versions.minecraft.asProvider().get(),
        "fabric_minecraft_version_range"     to libs.versions.fabric.minecraft.range.get(),
        "fabric_api_version"                 to libs.versions.fabric.api.asProvider().get(),
        "fabric_api_version_range"           to libs.versions.fabric.api.range.get(),
        "fabric_loader_version"              to libs.versions.fabric.loader.asProvider().get(),
        "fabric_loader_version_range"        to libs.versions.fabric.loader.range.get(),
        "owo_fabric_version"                 to libs.versions.owo.fabric.asProvider().get(),
        "owo_fabric_version_range"           to libs.versions.owo.fabric.range.get(),
        "accessories_fabric_version_range"   to libs.versions.accessories.fabric.range.get(),
        "neoforge_minecraft_version_range"   to libs.versions.neoforge.minecraft.range.get(),
        "neoforge_version"                   to libs.versions.neoforge.asProvider().get(),
        "neoforge_version_range"             to libs.versions.neoforge.range.get(),
        "neoforge_loader_version_range"      to libs.versions.neoforge.loader.range.get(),
        "owo_neoforge_version"               to libs.versions.owo.neoforge.asProvider().get(),
        "owo_neoforge_version_range"         to libs.versions.owo.neoforge.range.get(),
        "accessories_neoforge_version_range" to libs.versions.accessories.neoforge.range.get(),
        "java_version"                       to libs.versions.java.get()
    )

    // Fabric: More Info about contacts like links
    expandProps["contact_entry"] = Utils.buildMapEntry(project, "homepage", "sourcepage", "issuepage")

    expandProps["list_of_authors"] = Utils.buildListEntry(project, (rootProject.property("mod_authors") as String).split(","))
    expandProps["list_of_contributors"] = Utils.buildListEntry(project, (rootProject.property("mod_contributors") as String).split(","))
    // --

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/neoforge.mods.toml"/*, "*.mixins.json"*/)) {
        expand(expandProps)
    }

    // Fabric: Remove various entries that are there due to inability to properly add string data without breaking FMJ before looms reading
    // Note: Stupid Cast to remove error that is wrong
    filesMatching(listOf("fabric.mod.json")) { filter(UtilsJava.removeLineTransformer())  }
    // --

    inputs.properties(expandProps)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = Integer.parseInt(libs.versions.java.get())
}

java {
    withSourcesJar()
}