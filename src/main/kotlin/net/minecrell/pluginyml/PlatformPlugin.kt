/*
 * plugin-yml
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

package net.minecrell.pluginyml

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.task
import org.gradle.kotlin.dsl.withType
import java.io.Serializable

abstract class PlatformPlugin<T : Serializable>(private val platformName: String, private val fileName: String) : Plugin<Project> {

    protected abstract fun createExtension(project: Project): T

    override final fun apply(project: Project) {
        project.run {
            val description = createExtension(this)

            // Add extension
            extensions.add(platformName.decapitalize(), description)

            // Create task
            val generateTask = task<GeneratePluginDescription>("generate${platformName}PluginDescription") {
                fileName = this@PlatformPlugin.fileName
                pluginDescription = description
            }

            generateTask.doFirst {
                prepare(project, description)
            }

            plugins.withType<JavaBasePlugin> {
                val processResources: AbstractCopyTask by tasks
                processResources.from(generateTask)
            }
        }
    }

    private fun prepare(project: Project, description: T) {
        setDefaults(project, description)
        validate(description)
    }

    protected abstract fun setDefaults(project: Project, description: T)
    protected abstract fun validate(description: T)

}
