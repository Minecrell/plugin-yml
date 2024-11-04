/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml.bukkit

import net.minecrell.pluginyml.InvalidPluginDescriptionException
import net.minecrell.pluginyml.PlatformPlugin
import net.minecrell.pluginyml.collectLibraries
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedComponentResult

class BukkitPlugin : PlatformPlugin<BukkitPluginDescription>("Bukkit", "plugin.yml") {

    companion object {
        @JvmStatic
        private val VALID_NAME = Regex("^[A-Za-z0-9 _.-]+$")

        @JvmStatic
        private val VALID_API_VERSION = Regex("^1\\.[0-9]+$")

        @JvmStatic
        private val INVALID_NAMESPACES =
            listOf("net.minecraft.", "org.bukkit.", "io.papermc.", "com.destroystokoyo.paper.", "org.spigotmc")
    }

    override fun createExtension(project: Project) = BukkitPluginDescription(project)

    override fun setDefaults(project: Project, description: BukkitPluginDescription) {
        description.name = description.name ?: project.name
        description.version = description.version ?: project.version.toString()
        description.description = description.description ?: project.description
        description.website = description.website ?: project.findProperty("url")?.toString()
        description.author = description.author ?: project.findProperty("author")?.toString()
    }

    override fun setLibraries(libraries: ResolvedComponentResult?, description: BukkitPluginDescription) {
        description.libraries = libraries.collectLibraries(description.libraries)
    }

    override fun validate(description: BukkitPluginDescription) {
        val name = description.name ?: throw InvalidPluginDescriptionException("Plugin name is not set")
        if (!VALID_NAME.matches(name)) throw InvalidPluginDescriptionException("Invalid plugin name: should match $VALID_NAME")
        if (description.apiVersion != null) {
            val apiVersion = description.apiVersion!!
            val splitVersion = apiVersion.split("\\.").map { v -> v.toInt() }
            if (splitVersion.size == 2) {
                if (!VALID_API_VERSION.matches(apiVersion)) throw InvalidPluginDescriptionException("Invalid api version: should match $VALID_API_VERSION")
                if (apiVersion < "1.13") throw InvalidPluginDescriptionException("Invalid api version: should be at least 1.13")
            } else if (splitVersion.size == 3) {
                if (splitVersion[1] < 20) throw InvalidPluginDescriptionException("Invalid api version: Minor versions are not supported before 1.20.5")
                if (splitVersion[1] == 20 && splitVersion[2] < 5) throw InvalidPluginDescriptionException("Invalid api version: Minor versions are not supported before 1.20.5")
            } else {
                throw InvalidPluginDescriptionException("Invalid api version: $VALID_API_VERSION")
            }
        }

        if (description.version.isNullOrEmpty()) throw InvalidPluginDescriptionException("Plugin version is not set")

        val main = description.main ?: throw InvalidPluginDescriptionException("Main class is not defined")
        if (main.isEmpty()) throw InvalidPluginDescriptionException("Main class cannot be empty")
        validateNamespace(main, "Main")

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

    private fun validateNamespace(namespace: String, name: String) {
        for (invalidNamespace in INVALID_NAMESPACES) {
            if (namespace.startsWith(invalidNamespace)) {
                throw InvalidPluginDescriptionException("$name may not be within the $invalidNamespace namespace")
            }
        }
    }
}
