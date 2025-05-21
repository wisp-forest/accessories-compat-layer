package helpers

import gradle.kotlin.dsl.accessors._049fa54a8c482cde147c17c9a808c570.compileClasspath
import gradle.kotlin.dsl.accessors._049fa54a8c482cde147c17c9a808c570.sourceSets
import net.fabricmc.loom.configuration.ide.RunConfigSettings
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.kotlin.dsl.get

object Utils {
    fun getSetupRunsAction(project: Project): Action<NamedDomainObjectContainer<RunConfigSettings>> {
        return Action {
            val currentPlatform: String = ((project.properties["loom.platform"] as String?) ?: "common")
            val rootProject = project.rootProject;

            val enabledMixinDebuggingPlatforms = (rootProject.property("enabled_mixin_debugging_platforms") as String).split(",")
            val enabledRenderDocPlatforms = (rootProject.property("enabled_renderdoc_debugging_platforms") as String).split(",");
            val enabledTestmodPlatforms = (rootProject.property("enabled_renderdoc_debugging_platforms") as String).split(",");

            val renderDocPath = System.getenv("renderDocPath");

            val addMixinDebugginRuns = enabledMixinDebuggingPlatforms.contains(currentPlatform)
            val addTestModRuns = enabledTestmodPlatforms.contains(currentPlatform)

            fun setupTestMod(settings: RunConfigSettings) {
                project.afterEvaluate {
                    settings.source(project.sourceSets["testmod"])
                }

                if (currentPlatform != "neoforge") return

                settings.mods {
                    create("${rootProject.property("test_mod_id")}") { sourceSet(project.sourceSets["testmod"]) }
                    create("${rootProject.property("mod_id")}") { sourceSet(project.sourceSets["main"]) }
                }
            }

            if (addTestModRuns) {
                create("testmodClient") {
                    client()
                    ideConfigGenerated(true)
                    name("Testmod Client")
                    setupTestMod(this)
                }
                create("testmodServer") {
                    server()
                    ideConfigGenerated(true)
                    name("Testmod Server")
                    setupTestMod(this)
                }
            }

            if (addMixinDebugginRuns) {
                fun addMixinAsJavaAgent(settings: RunConfigSettings) {
                    // TODO: OUTSOURCE TO METHOD!
                    try {
                        project.afterEvaluate {
                            val mixin = this.configurations.compileClasspath.get()
                                .allDependencies
                                .asIterable()
                                .firstOrNull { it.name == "sponge-mixin" }
                            if (mixin != null) {
                                settings.vmArg("-javaagent:\"${this.configurations.compileClasspath.get().files(mixin).first().path}\"")
                                println("[Info]: Mixin Hotswap Run should be working")
                            } else {
                                println("[Warning]: Unable to locate file path for Mixin Jar, HotSwap Run will not work!!!")
                            }
                        }
                    } catch (e: Exception) {
                        println("[Error]: MixinHotswap Run had a issue!")
                        e.printStackTrace()
                    }
                }

                create("clientMixinDebug") {
                    client()
                    ideConfigGenerated(true)
                    name("Minecraft Client - (Mixin Debug)")
                    vmArg("-Dfabric.dli.config=${project.file(".gradle/loom-cache/launch.cfg")}")
                    vmArg("-Dfabric.dli.env=client")
                    vmArg("-Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotClient")

                    addMixinAsJavaAgent(this);

                    vmArg("-Dlog4j.configurationFile=${project.file(".gradle/loom-cache/log4j.xml")}")
                    vmArg("-Dfabric.log.disableAnsi=false")
                    vmArg("-Dmixin.debug.export=true")
                }

                if (addTestModRuns) {
                    create("testmodClientMixinDebug") {
                        client()
                        ideConfigGenerated(true)
                        name("Testmod Client - (Mixin Debug)")
                        vmArg("-Dfabric.dli.config=${project.file(".gradle/loom-cache/launch.cfg")}")
                        vmArg("-Dfabric.dli.env=client")
                        vmArg("-Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotClient")

                        addMixinAsJavaAgent(this);

                        vmArg("-Dlog4j.configurationFile=${project.file(".gradle/loom-cache/log4j.xml")}")
                        vmArg("-Dfabric.log.disableAnsi=false")
                        vmArg("-Dmixin.debug.export=true")

                        setupTestMod(this)
                    }
                }
            } else {
                println("Mixin Debugging for $currentPlatform is disabled just a FYI, adjust the gradle.properties 'enabled_mixin_debugging_platforms' field to contain such if you want the runs!")
            }

            if (enabledRenderDocPlatforms.contains(currentPlatform)) {
                if (renderDocPath != null) {
                    create("clientRenderDoc") {
                        client()
                        ideConfigGenerated(true)
                        name("Minecraft Client - (RenderDoc)")
                        source(project.sourceSets["main"])
                        vmArg("-Dowo.renderdocPath=$renderDocPath")
                    }
                    if (addTestModRuns) {
                        create("testmodClientRenderDoc") {
                            client()
                            ideConfigGenerated(true)
                            name("Testmod Client - (RenderDoc)")
                            source(project.sourceSets["testmod"])
                            vmArg("-Dowo.renderdocPath=$renderDocPath")

                            setupTestMod(this)
                        }
                    }
                } else {
                    println("Unable to create Render Doc runs due to the renderDocPath variable is not found! Please add such and regenerate runs to get access if desired!")
                }
            } else {
                println("Render Doc Debugging for $currentPlatform is disabled just a FYI, adjust the gradle.properties 'enabled_mixin_debugging_platforms' field to contain such if you want the runs!")
            }
        }
    }

    //-- Fabric FMJ Entry Utils
    val removeLineTarget = "#REMOVE_LINE#"
    val baseIndentation = "  "

    fun indentation (level: Int): String {
        return baseIndentation.repeat(level);
    }

    val separator = ",\n"

    fun buildListEntry (project: Project, keys: List<String>): String {
        val rootProject = project.rootProject;
        var entries = "";

        keys.forEachIndexed { i: Int, key: String ->
            if (key.isBlank()) return@forEachIndexed

            val contactKey = key.toLowerCase().replace(" ", "_");
            val hasContact = rootProject.hasProperty(contactKey)

            if (hasContact) {
                if (i == 0) entries += "$removeLineTarget\"$separator"

                entries +=
                    """{
  "name": "$key",
  "contact": { 
    "homepage": "${rootProject.property(contactKey)}"
  }
}""".prependIndent(indentation(2))
            } else {
                if (i != 0) entries += "\"";

                entries += "$key\"";
            }
            entries += if (i < keys.size - 1) separator else "\n";
        }

        return entries + "${indentation(2)}\"$removeLineTarget";
    }

    fun buildMapEntry(project: Project, vararg keys: String): String {
        val rootProject = project.rootProject;
        var fullEntry = "$removeLineTarget\": \"\"$separator";

        keys.forEachIndexed { i, key ->
            fullEntry += "${indentation(2)}${createMapEntry(rootProject, key)}"
            fullEntry += if (i < keys.size - 1) separator else "\n";
        }

        fullEntry += "${indentation(2)}\"$removeLineTarget"

        return fullEntry;
    }

    fun createMapEntry(rootProject: Project, key: String): String {
        return createMapEntry(rootProject, "mod_$key", key)
    }

    fun createMapEntry(rootProject: Project, propKey: String, entryKey: String): String {
        val propertyValue = rootProject.property(propKey) as String;

        return (if (propertyValue.isNotBlank()) "\"$entryKey\": \"$propertyValue\"" else "");
    }

    fun getFilterTransform(): Transformer<String?, String> {
        return Transformer { value ->
            return@Transformer ((if (value.contains(removeLineTarget) ) null else value) as String)
        }
    }

    fun currentPlatform(project: Project): String {
        return ((project.properties["loom.platform"] as String?) ?: "common");
    }
}