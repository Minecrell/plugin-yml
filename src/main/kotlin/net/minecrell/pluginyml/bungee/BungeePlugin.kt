/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.minecrell.pluginyml.bungee

import net.minecrell.pluginyml.InvalidPluginDescriptionException
import net.minecrell.pluginyml.PlatformPlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class BungeePlugin : PlatformPlugin<BungeePluginDescription>("Bungee", "bungee.yml") {

    override fun createExtension(project: Project) = BungeePluginDescription()

    override fun setDefaults(project: Project, description: BungeePluginDescription) {
        description.name = description.name ?: project.name
        description.version = description.version ?: project.version.toString()
        description.description = description.description ?: project.description
        description.author = description.author ?: project.findProperty("author")?.toString()
    }

    override fun resolve(libraries: Configuration?, description: BungeePluginDescription) {
        description.libraries = (
                (description.libraries ?: listOf()) + libraries!!
                    .resolvedConfiguration
                    .firstLevelModuleDependencies
                    .map { it.module.id.toString() }
                )
    }

    override fun validate(description: BungeePluginDescription) {
        if (description.name.isNullOrEmpty()) throw InvalidPluginDescriptionException("Plugin name is not set")
        if (description.main.isNullOrEmpty()) throw InvalidPluginDescriptionException("Main class is not defined")
    }

}
