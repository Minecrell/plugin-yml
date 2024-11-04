/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml.nukkit

import net.minecrell.pluginyml.InvalidPluginDescriptionException
import net.minecrell.pluginyml.PlatformPlugin
import org.gradle.api.Project

class NukkitPlugin : PlatformPlugin<NukkitPluginDescription>("Nukkit", "nukkit.yml") {
    override fun createExtension(project: Project) = NukkitPluginDescription(project)

    override fun setDefaults(project: Project, description: NukkitPluginDescription) {
        description.name = description.name ?: project.name
        description.version = description.version ?: project.version.toString()
        description.description = description.description ?: project.description
        description.website = description.website ?: project.findProperty("url")?.toString()
        description.author = description.author ?: project.findProperty("author")?.toString()
    }

    override fun validate(description: NukkitPluginDescription) {
        if (description.name.isNullOrEmpty()) throw InvalidPluginDescriptionException("Plugin name is not set")
        if (description.version.isNullOrEmpty()) throw InvalidPluginDescriptionException("Plugin version class is not set")

        val main = description.main ?: throw InvalidPluginDescriptionException("Main class is not defined")
        if (main.isEmpty()) throw InvalidPluginDescriptionException("Main class cannot be empty")
        if (main.startsWith("cn.nukkit.")) throw InvalidPluginDescriptionException("Main class cannot be within cn.nukkit. package")

        if (description.api.isNullOrEmpty()) throw InvalidPluginDescriptionException("Nukkit API version is not set")

        for (command in description.commands) {
            if (command.name.contains(':')) throw InvalidPluginDescriptionException("Command '${command.name}' cannot contain ':'")
            command.aliases?.forEach { alias ->
                if (alias.contains(':')) throw InvalidPluginDescriptionException("Alias '$alias' of '${command.name}' cannot contain ':'")
            }
        }
    }

}
