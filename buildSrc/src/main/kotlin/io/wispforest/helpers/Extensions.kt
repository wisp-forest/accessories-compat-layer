package io.wispforest.helpers

import gradle.kotlin.dsl.accessors._049fa54a8c482cde147c17c9a808c570.fabricApi
import gradle.kotlin.dsl.accessors._049fa54a8c482cde147c17c9a808c570.modCompileOnly
import gradle.kotlin.dsl.accessors._049fa54a8c482cde147c17c9a808c570.modImplementation
import gradle.kotlin.dsl.accessors._049fa54a8c482cde147c17c9a808c570.modLocalRuntime
import gradle.kotlin.dsl.accessors._049fa54a8c482cde147c17c9a808c570.modRuntimeOnly
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.internal.BiAction
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.the
import java.util.function.BiConsumer
import kotlin.text.get

object Extensions {
    val Project.libs: LibrariesForLibs get() {
        return the<LibrariesForLibs>();
    }

    fun Project.fabricModule(dependencyMethod: BiConsumer<Dependency, Action<Dependency>>, vararg moduleNames: String, action: Action<Dependency>? = null) {
        for (moduleName in moduleNames) {
            dependencyMethod.accept(fabricApi.module(moduleName, libs.versions.fabric.api.asProvider().get())){
                (this as ModuleDependency).exclude(group = "fabric-api", module = "")

                action?.execute(this)
            }
        }
    }

    fun DependencyHandler.modrinthLocalRuntime(vararg projects: Pair<String, String>, action: Action<Entry>? = null) {
        modrinth(this::modLocalRuntime, *projects, action = action);
    }

    fun DependencyHandler.modrinthCompileOnly(vararg projects: Pair<String, String>, action: Action<Entry>? = null) {
        modrinth(this::modCompileOnly, *projects, action = action);
    }

    fun DependencyHandler.modrinthImplementation(vararg projects: Pair<String, String>, action: Action<Entry>? = null) {
        modrinth(this::modImplementation, *projects, action = action);
    }

    fun DependencyHandler.modrinth(dependencyMethod: BiConsumer<kotlin.String, Action<org.gradle.api.artifacts.ExternalModuleDependency>>, vararg projects: Pair<String, String>, action: Action<Entry>? = null) {
        projects.forEach { (projectId, version) ->
            dependencyMethod.accept("maven.modrinth:${projectId}:${version}"){
                action?.execute(Entry(this, projectId))
            }
        }
    }

    data class Entry(val dependency: ExternalModuleDependency, val projectId: String) {

    }
}