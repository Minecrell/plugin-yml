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

        if (description.api?.isEmpty() != false) throw InvalidPluginDescriptionException("Nukkit API version is not set")
    }

}
