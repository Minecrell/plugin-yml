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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.util.Converter
import com.fasterxml.jackson.databind.util.StdConverter
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.UrlArtifactRepository
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GeneratePluginDescription : DefaultTask() {

    @get:Input
    abstract val fileName: Property<String>

    @get:Input
    abstract val librariesJsonFileName: Property<String>

    @get:Input
    @get:Optional
    abstract val librariesRootComponent: Property<ResolvedComponentResult>

    @get:Nested
    abstract val pluginDescription: Property<PluginDescription>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val factory = YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
            .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)

        val module = SimpleModule()
        @Suppress("UNCHECKED_CAST") // Too stupid to figure out the generics here...
        module.addSerializer(StdDelegatingSerializer(NamedDomainObjectCollection::class.java,
            NamedDomainObjectCollectionConverter as Converter<NamedDomainObjectCollection<*>, *>))
        module.addSerializer(StdDelegatingSerializer(UrlArtifactRepository::class.java, UrlArtifactRepositoryConverter))

        val mapper = ObjectMapper(factory)
            .registerKotlinModule()
            .registerModule(module)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        val pluginDescription = pluginDescription.get()
        mapper.writeValue(outputDirectory.file(fileName).get().asFile, pluginDescription)

        if (pluginDescription.generateLibrariesJson) {
            val repos = this.project.repositories.withType(MavenArtifactRepository::class.java)
            val dependencies = librariesRootComponent.orNull.collectLibraries()
            val pluginLibraries = PluginLibraries(repos, dependencies)

            val jsonMapper = ObjectMapper()
                .registerKotlinModule()
                .registerModule(module)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            jsonMapper.writeValue(outputDirectory.file(librariesJsonFileName).get().asFile, pluginLibraries)
        }
    }

    object NamedDomainObjectCollectionConverter : StdConverter<NamedDomainObjectCollection<Any>, Map<String, Any>>() {
        override fun convert(value: NamedDomainObjectCollection<Any>): Map<String, Any> {
            val namer = value.namer
            return value.associateBy { namer.determineName(it) }
        }
    }

    object UrlArtifactRepositoryConverter : StdConverter<UrlArtifactRepository, String>() {
        override fun convert(value: UrlArtifactRepository): String = value.url.toString()
    }

    data class PluginLibraries(
        val repositories: NamedDomainObjectCollection<MavenArtifactRepository>,
        val dependencies: List<String>
    )

}
