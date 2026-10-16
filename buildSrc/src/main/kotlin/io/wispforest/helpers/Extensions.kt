package io.wispforest.helpers

import net.fabricmc.loom.configuration.FabricApiExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.the
import java.util.function.BiConsumer

object Extensions {
    val Project.libs: LibrariesForLibs get() {
        return the<LibrariesForLibs>();
    }

    fun Project.fabricModule(dependencyMethod: BiConsumer<Dependency, Action<Dependency>>, vararg moduleNames: String, action: Action<Dependency>? = null) {
        for (moduleName in moduleNames) {
            dependencyMethod.accept(the<FabricApiExtension>().module(moduleName, libs.versions.fabric.api.asProvider().get())){
                (this as ModuleDependency).exclude(group = "fabric-api", module = "")

                action?.execute(this)
            }
        }
    }

    fun DependencyHandler.modrinthLocalRuntime(vararg projects: Pair<String, String>, action: Action<Entry>? = null) {
        modrinth(configurationMethod("modLocalRuntime"), *projects, action = action);
    }

    fun DependencyHandler.modrinthCompileOnly(vararg projects: Pair<String, String>, action: Action<Entry>? = null) {
        modrinth(configurationMethod("modCompileOnly"), *projects, action = action);
    }

    fun DependencyHandler.modrinthImplementation(vararg projects: Pair<String, String>, action: Action<Entry>? = null) {
        modrinth(configurationMethod("modImplementation"), *projects, action = action);
    }

    fun DependencyHandler.modrinth(dependencyMethod: BiConsumer<String, Action<ExternalModuleDependency>>, vararg projects: Pair<String, String>, action: Action<Entry>? = null) {
        projects.forEach { (projectId, version) ->
            dependencyMethod.accept("maven.modrinth:${projectId}:${version}"){
                action?.execute(Entry(this, projectId))
            }
        }
    }

    private fun DependencyHandler.configurationMethod(configurationName: String): BiConsumer<String, Action<ExternalModuleDependency>> {
        return BiConsumer { dependencyNotation, action ->
            action.execute(add(configurationName, dependencyNotation) as ExternalModuleDependency)
        }
    }

    data class Entry(val dependency: ExternalModuleDependency, val projectId: String) {

    }
}