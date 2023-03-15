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

package net.minecrell.pluginyml.paper

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.lang.Closure
import net.minecrell.pluginyml.PluginDescription
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

class PaperPluginDescription(project: Project) : PluginDescription {
    @Input
    @Optional
    @JsonProperty("api-version") var apiVersion: PluginVersion? = null

    @Input
    var bootstrapper: String? = null

    @Input
    var loader: String? = null

    @Input
    @Optional
    @JsonProperty("has-open-classloader")
    var hasOpenClassloader: Boolean? = null

    @Input
    @Optional
    var contributors: List<String>? = null

    @Nested
    val dependencies: NamedDomainObjectContainer<DependencyConfiguration> = project.container(DependencyConfiguration::class.java)

    @JsonProperty("load-after")
    @Nested
    val loadAfter: NamedDomainObjectContainer<LoadConfiguration> = project.container(LoadConfiguration::class.java)

    @JsonProperty("load-before")
    @Nested
    val loadBefore: NamedDomainObjectContainer<LoadConfiguration> = project.container(LoadConfiguration::class.java)

    // For Groovy DSL
    fun loadAfter(closure: Closure<Unit>) = loadAfter.configure(closure)
    fun loadBefore(closure: Closure<Unit>) = loadBefore.configure(closure)
    fun dependencies(closure: Closure<Unit>) = dependencies.configure(closure)

    @Input
    var name: String? = null

    @Input
    var version: String? = null

    @Input
    var main: String? = null

    @Input
    @Optional
    var description: String? = null

    @Input
    @Optional
    var load: BukkitPluginDescription.PluginLoadOrder? = null

    @Input
    @Optional
    var author: String? = null

    @Input
    @Optional
    var authors: List<String>? = null

    @Input
    @Optional
    var website: String? = null

    @Input
    @Optional
    var prefix: String? = null

    @Input
    @Optional
    @JsonProperty("default-permission") var defaultPermission: Permission.Default? = null

    @Input
    @Optional
    var provides: List<String>? = null

    @Input @Optional var libraries: List<String>? = null

    enum class PluginVersion(val version: String) {
        V1_19("1.19"),
    }
    @Nested
    val commands: NamedDomainObjectContainer<Command> = project.container(Command::class.java)

    @Nested
    val permissions: NamedDomainObjectContainer<Permission> = project.container(Permission::class.java)
    // For Groovy DSL
    fun commands(closure: Closure<Unit>) = commands.configure(closure)
    fun permissions(closure: Closure<Unit>) = permissions.configure(closure)

    enum class PluginLoadOrder {
        STARTUP,
        POSTWORLD
    }

    data class DependencyConfiguration(@Input val name: String) {
        @Input @Optional var required: Boolean? = null
        @Input @Optional var bootstrap: Boolean? = null
    }
    data class LoadConfiguration(@Input val name: String) {
        @Input @Optional var bootstrap: Boolean? = null
    }

    data class Command(@Input @JsonIgnore val name: String) {
        @Input @Optional var description: String? = null
        @Input @Optional var aliases: List<String>? = null
        @Input @Optional var permission: String? = null
        @Input @Optional @JsonProperty("permission-message") var permissionMessage: String? = null
        @Input @Optional var usage: String? = null
    }

    data class Permission(@Input @JsonIgnore val name: String) {
        @Input @Optional var description: String? = null
        @Input @Optional var default: Default? = null
        var children: List<String>?
            @Internal @JsonIgnore get() = childrenMap?.filterValues { it }?.keys?.toList()
            set(value) {
                childrenMap = value?.associateWith { true }
            }
        @Input @Optional @JsonProperty("children") var childrenMap: Map<String, Boolean>? = null

        enum class Default {
            @JsonProperty("true")   TRUE,
            @JsonProperty("false")  FALSE,
            @JsonProperty("op")     OP,
            @JsonProperty("!op")    NOT_OP
        }
    }
}