plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.parchmentmc.org")
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("architectury-plugin:architectury-plugin.gradle.plugin:3.4-SNAPSHOT")
    implementation("dev.architectury:architectury-loom:1.9-SNAPSHOT")

    // Required to get Version Catalogs to show in buildSrc
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

