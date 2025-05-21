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
}

dependencies {
    // Core Libs
    modImplementation(libs.fabric.loader)
    compileOnly(libs.mixin.extras.common)
    annotationProcessor(libs.mixin.extras.common)
    // --

    // General Libs
    modApi(libs.owolib.fabric)
    annotationProcessor(libs.owolib.fabric)
    modCompileOnlyApi(fabricApi.module("fabric-api-base", libs.versions.fabric.api.asProvider().get())){
        (this as ModuleDependency).exclude(group = "fabric-api", module = "")
    }
    // --
}

sourceSets {
    main {
        resources.srcDirs.add(File("src/generated"))
    }
}
