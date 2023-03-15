package net.minecrell.pluginyml.paper

import net.minecrell.pluginyml.InvalidPluginDescriptionException
import net.minecrell.pluginyml.PlatformPlugin
import net.minecrell.pluginyml.bukkit.BukkitPlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult

class PaperPlugin : PlatformPlugin<PaperPluginDescription>("Paper", "plugin.yml") {

    companion object {
        @JvmStatic private val VALID_NAME = Regex("^[A-Za-z0-9 _.-]+$")
    }
    override fun createExtension(project: Project): PaperPluginDescription  = PaperPluginDescription(project)

    override fun validate(description: PaperPluginDescription) {
        val name = description.name ?: throw InvalidPluginDescriptionException("Plugin name is not set")
        if (!VALID_NAME.matches(name)) throw InvalidPluginDescriptionException("Invalid plugin name: should match $VALID_NAME")

        if (description.version.isNullOrEmpty()) throw InvalidPluginDescriptionException("Plugin version is not set")

        val main = description.main ?: throw InvalidPluginDescriptionException("Main class is not defined")
        if (main.isEmpty()) throw InvalidPluginDescriptionException("Main class cannot be empty")
        if (main.startsWith("org.bukkit.")) throw InvalidPluginDescriptionException("Main may not be within the org.bukkit namespace")

        for (dependency in description.dependencies) {
            if (dependency.name.isEmpty()) throw InvalidPluginDescriptionException("LoadBefore '${dependency.name}' cannot be empty")
        }

        for (loadBefore in description.loadBefore) {
            if (loadBefore.name.isEmpty()) throw InvalidPluginDescriptionException("LoadBefore '${loadBefore.name}' cannot be empty")
        }

        for (loadAfter in description.loadAfter) {
            if (loadAfter.name.isEmpty()) throw InvalidPluginDescriptionException("LoadAfter '${loadAfter.name}' cannot be empty")
        }

        for (command in description.commands) {
            if (command.name.contains(':')) throw InvalidPluginDescriptionException("Command '${command.name}' cannot contain ':'")
            command.aliases?.forEach { alias ->
                if (alias.contains(':')) throw InvalidPluginDescriptionException("Alias '$alias' of '${command.name}' cannot contain ':'")
            }
        }

        if (description.provides?.all(VALID_NAME::matches) == false) {
            throw InvalidPluginDescriptionException("Invalid plugin provides name: all should match $VALID_NAME")
        }
    }

    override fun setLibraries(libraries: ResolvedComponentResult?, description: PaperPluginDescription) {
        val resolved = libraries?.let {
            it.dependencies.map { d -> (d as? ResolvedDependencyResult)?.selected?.moduleVersion?.toString() ?: error("No moduleVersion for $d") }
        }
        description.libraries = ((description.libraries ?: listOf()) + (resolved ?: listOf())).distinct()
    }

    override fun setDefaults(project: Project, description: PaperPluginDescription) {
        description.name = description.name ?: project.name
        description.version = description.version ?: project.version.toString()
        description.description = description.description ?: project.description
        description.website = description.website ?: project.findProperty("url")?.toString()
        description.author = description.author ?: project.findProperty("author")?.toString()
    }

}