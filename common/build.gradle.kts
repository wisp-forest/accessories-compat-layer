import java.util.function.BiConsumer
import java.util.function.BiFunction

plugins {
    id("multiloader-mojmap")
    id("multiloader-publishing")
}

architectury {
    common((rootProject.property("enabled_platforms") as String).split(","))
}

loom {
    val awPath = "src/main/resources/${rootProject.property("mod_id")}.accesswidener"
    val awFile = file(awPath);

    if (!awFile.exists()) {
        throw IllegalStateException("Unable to locate the given AccessWidener File at the given path: $awPath")
    }

    accessWidenerPath = awFile;
}

repositories {
    mavenCentral()
    gradlePluginPortal()

    // Fabric Loader Lib
    maven("https://maven.fabricmc.net/")
    //--

    // REI Item Viewer
    maven("https://maven.shedaniel.me/")
    maven("https://maven.architectury.dev/")
    // --

    // EMI Item Viewer
    maven("https://maven.terraformersmc.com/releases")
    // --

    maven("https://maven.terraformersmc.com/")
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

    // Core Libs
    modImplementation(libs.fabric.loader)
    compileOnly(libs.mixin.extras.common)
    annotationProcessor(libs.mixin.extras.common)
    // --

    // General Libs
    modApi(libs.owolib.fabric)
    annotationProcessor(libs.owolib.fabric)

    fabricModule(this::modCompileOnlyApi, "fabric-api-base")
    // --

    modImplementation(libs.accessories.common)

    modCompileOnly(libs.trinkets)
    fabricModule(this::modCompileOnly, "fabric-resource-loader-v0", "fabric-events-interaction-v0")

    modCompileOnly(libs.curios)

    annotationProcessor(libs.mixin.squared.common)
    implementation(libs.mixin.squared.common)
}

sourceSets {
    main {
        resources.srcDirs.add(File("src/generated"))
    }
}
