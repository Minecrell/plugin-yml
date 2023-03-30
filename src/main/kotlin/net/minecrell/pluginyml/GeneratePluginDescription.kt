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
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import net.minecrell.pluginyml.paper.PaperPluginDescription
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.lang.model.element.Modifier

abstract class GeneratePluginDescription : DefaultTask() {

    @get:Input
    abstract val fileName: Property<String>

    @get:Input
    @get:Optional
    abstract val packageName: Property<String>

    @get:Input
    @get:Optional
    abstract val generateLibsClass: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val generateReposClass: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val librariesRootComponent: Property<ResolvedComponentResult>

    @get:Nested
    abstract val pluginDescription: Property<PluginDescription>

    @get:OutputDirectory
    abstract val outputResourcesDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputSourceDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val factory = YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)

        val module = SimpleModule()
        @Suppress("UNCHECKED_CAST") // Too stupid to figure out the generics here...
        module.addSerializer(
            StdDelegatingSerializer(
                NamedDomainObjectCollection::class.java,
                NamedDomainObjectCollectionConverter as Converter<NamedDomainObjectCollection<*>, *>
            )
        )

        val mapper = ObjectMapper(factory)
            .registerKotlinModule()
            .registerModule(module)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        val pluginDescription = pluginDescription.get()

        mapper.writeValue(outputResourcesDirectory.file(fileName).get().asFile, pluginDescription)
        if (generateLibsClass.isPresent) buildDependencies(pluginDescription)
        if (generateReposClass.isPresent) buildRepos(pluginDescription)
    }

    private fun buildRepos(pluginDescription: PluginDescription) {
        if (pluginDescription is PaperPluginDescription) {
            var typeSpec = TypeSpec.enumBuilder("Repos")
            typeSpec.addModifiers(Modifier.PUBLIC)
            val remoteRepositoryClass = ClassName.get(RemoteRepository::class.java)
            this.project.repositories.filterIsInstance<MavenArtifactRepository>().forEach {
                typeSpec = typeSpec.addEnumConstant(
                    it.name.uppercase().replace('-', '_'),
                    TypeSpec.anonymousClassBuilder(
                        "new \$T.Builder(\$S,\"default\",\$S).build()",
                        *arrayOf(remoteRepositoryClass, it.name, it.url.toString())
                    ).build()
                )
            }
            typeSpec.addField(RemoteRepository::class.java, "value", Modifier.PRIVATE, Modifier.FINAL)
            typeSpec.addMethod(
                MethodSpec.constructorBuilder()
                    .addParameter(RemoteRepository::class.java, "value")
                    .addStatement("this.\$N = \$N", "value", "value")
                    .build()
            )
            typeSpec.addMethod(
                MethodSpec.methodBuilder("asRepo")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(RemoteRepository::class.java)
                    .addStatement("return this.value")
                    .build()
            )
            JavaFile.builder(packageName.getOrElse("net.minecrell.pluginyml"), typeSpec.build())
                .build()
                .writeTo(outputSourceDirectory.get().asFile)
        }
    }

    private fun buildDependencies(pluginDescription: PluginDescription) {
        if (pluginDescription is PaperPluginDescription && pluginDescription.libraries != null) {
            pluginDescription.libraries!!.toList().let { libs ->
                if (libs.isEmpty()) return@let
                var typeSpec = TypeSpec.enumBuilder("Libraries")
                typeSpec.addModifiers(Modifier.PUBLIC)
                val clazz = ClassName.get(DefaultArtifact::class.java)
                val dependencyClass = ClassName.get(Dependency::class.java)
                libs.forEach { it ->
                    if (it.count { it == ':' } != 2) throw IllegalArgumentException("Invalid library: $it")
                    val group = it.substringBefore(':')
                    val version = it.substringAfterLast(':')
                    val name = it.substringAfter(':').substringBefore(':')
                    typeSpec = typeSpec.addEnumConstant(
                        name.uppercase().replace('-', '_'),
                        TypeSpec.anonymousClassBuilder("new \$T(\$S)", *arrayOf(clazz, "$group:$name:$version")).build()
                    )
                }
                typeSpec.addField(DefaultArtifact::class.java, "value", Modifier.PRIVATE, Modifier.FINAL)
                typeSpec.addMethod(
                    MethodSpec.constructorBuilder()
                        .addParameter(DefaultArtifact::class.java, "value")
                        .addStatement("this.\$N = \$N", "value", "value")
                        .build()
                )
                typeSpec.addMethod(
                    MethodSpec.methodBuilder("asDependency")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Dependency::class.java)
                        .addStatement("return new \$T(this.value, null)", dependencyClass)
                        .build()
                )
                typeSpec.addMethod(
                    MethodSpec.methodBuilder("asArtifact")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(DefaultArtifact::class.java)
                        .addStatement("return this.value")
                        .build()
                )
                JavaFile.builder(packageName.getOrElse("net.minecrell.pluginyml"), typeSpec.build())
                    .build()
                    .writeTo(outputSourceDirectory.get().asFile)
            }
        }
    }

    object NamedDomainObjectCollectionConverter : StdConverter<NamedDomainObjectCollection<Any>, Map<String, Any>>() {
        override fun convert(value: NamedDomainObjectCollection<Any>): Map<String, Any> {
            val namer = value.namer
            return value.associateBy { namer.determineName(it) }
        }
    }
}
