/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

abstract class PlatformPlugin<T : PluginDescription>(private val platformName: String, private val fileName: String) : Plugin<Project> {

    protected abstract fun createExtension(project: Project): T

    final override fun apply(project: Project) {
        project.run {
            val description = createExtension(this)

            // Add extension
            val prefix = platformName.replaceFirstChar(Char::lowercase)
            extensions.add(prefix, description)

            val generatedResourcesDirectory = layout.buildDirectory.dir("generated/plugin-yml/$platformName")

            // Add library configuration
            val library = project.configurations.maybeCreate("library")
            val libraries = project.configurations.create("${prefix}Library").extendsFrom(library)

            // Create task
            val generateTask = tasks.register<GeneratePluginDescription>("generate${platformName}PluginDescription") {
                group = "plugin-yml"

                fileName.set(this@PlatformPlugin.fileName)
                librariesJsonFileName.set("$prefix-libraries.json")
                librariesRootComponent.set(libraries.incoming.resolutionResult.root)
                outputDirectory.set(generatedResourcesDirectory)
                pluginDescription.set(provider {
                    setDefaults(project, description)
                    description
                })

                doFirst {
                    setLibraries(librariesRootComponent.orNull, description)
                    validate(description)
                }
            }

            plugins.withType<JavaPlugin> {
                extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
                    resources.srcDir(generateTask)
                    configurations.getByName(compileOnlyConfigurationName).extendsFrom(libraries)
                }
            }
        }
    }

    protected abstract fun setDefaults(project: Project, description: T)
    protected open fun setLibraries(libraries: ResolvedComponentResult?, description: T) {}
    protected abstract fun validate(description: T)

}
