/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml.bungee

import net.minecrell.pluginyml.InvalidPluginDescriptionException
import net.minecrell.pluginyml.PlatformPlugin
import net.minecrell.pluginyml.collectLibraries
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedComponentResult

class BungeePlugin : PlatformPlugin<BungeePluginDescription>("Bungee", "bungee.yml") {

    override fun createExtension(project: Project) = BungeePluginDescription()

    override fun setDefaults(project: Project, description: BungeePluginDescription) {
        description.name = description.name ?: project.name
        description.version = description.version ?: project.version.toString()
        description.description = description.description ?: project.description
        description.author = description.author ?: project.findProperty("author")?.toString()
    }

    override fun setLibraries(libraries: ResolvedComponentResult?, description: BungeePluginDescription) {
        description.libraries = libraries.collectLibraries(description.libraries)
    }

    override fun validate(description: BungeePluginDescription) {
        if (description.name.isNullOrEmpty()) throw InvalidPluginDescriptionException("Plugin name is not set")
        if (description.main.isNullOrEmpty()) throw InvalidPluginDescriptionException("Main class is not defined")
    }

}
