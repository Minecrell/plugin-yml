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

package net.minecrell.pluginyml

import net.minecrell.pluginyml.paper.PaperPluginDescription
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.register
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

abstract class PlatformPlugin<T : PluginDescription>(private val platformName: String, private val fileName: String) : Plugin<Project> {

    protected abstract fun createExtension(project: Project): T

    protected open fun createConfiguration(project: Project): Configuration? {
        val library = project.configurations.maybeCreate("library")
        val prefix = platformName.replaceFirstChar(Char::lowercase)
        return project.configurations.create("${prefix}Library").extendsFrom(library)
    }

    final override fun apply(project: Project) {
        project.run {
            val description = createExtension(this)

            // Add extension
            extensions.add(platformName.replaceFirstChar(Char::lowercase), description)

            val generatedResourcesDirectory = layout.buildDirectory.dir("generated/plugin-yml/$platformName/resources")
            val generatedSourcesDirectory = layout.buildDirectory.dir("generated/plugin-yml/$platformName/sources")

            // Add library configuration
            val libraries = createConfiguration(this)

            // Create task
            val task = tasks.register<GeneratePluginDescription>("generate${platformName}PluginDescription") {
                group = "PluginYML"
                if (description is PaperPluginDescription) {
                    generateReposClass.set(description.generateReposClass)
                    generateLibsClass.set(description.generateLibClass)
                    packageName.set(description.generatedPackageName)
                }

                fileName.set(this@PlatformPlugin.fileName)
                librariesRootComponent.set(libraries?.incoming?.resolutionResult?.root)
                outputResourcesDirectory.set(generatedResourcesDirectory)
                outputSourceDirectory.set(generatedSourcesDirectory)
                pluginDescription.set(provider {
                    setDefaults(project, description)
                    description
                })

                doFirst {
                    setLibraries(librariesRootComponent.orNull, description)
                    validate(description)
                }
            }
            tasks.withType(JavaCompile::class.java) {
                dependsOn(task)
            }
            tasks.withType(ProcessResources::class.java) {
                dependsOn(task)
            }
            plugins.withType<JavaPlugin> {
                extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
                    resources.srcDir(generatedResourcesDirectory)
                    java.srcDir(generatedSourcesDirectory)
                    if (libraries != null) {
                        configurations.getByName(compileOnlyConfigurationName).extendsFrom(libraries)
                    }
                }
            }

        }
    }

    protected abstract fun setDefaults(project: Project, description: T)
    protected abstract fun setLibraries(libraries: ResolvedComponentResult?, description: T)
    protected abstract fun validate(description: T)

}
