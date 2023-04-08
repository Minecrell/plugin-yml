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
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import groovy.lang.Closure
import net.minecrell.pluginyml.PluginDescription
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

class PaperPluginDescription(project: Project) : PluginDescription {

    @Input @JsonProperty("api-version") var apiVersion: String? = null
    @Input var name: String? = null
    @Input var version: String? = null
    @Input var main: String? = null
    @Input @JsonIgnore var generatePluginLibraries: Boolean = false
    @Input @Optional var bootstrapper: String? = null
    @Input @Optional var loader: String? = null
    @Input @Optional var description: String? = null
    @Input @Optional var load: PluginLoadOrder? = null
    @Input @Optional var author: String? = null
    @Input @Optional var authors: List<String>? = null
    @Input @Optional var contributors: List<String>? = null
    @Input @Optional var website: String? = null
    @Input @Optional var prefix: String? = null
    @Input @Optional @JsonProperty("defaultPerm") var defaultPermission: Permission.Default? = null
    @Input @Optional var provides: List<String>? = null
    @Input @Optional @JsonIgnore var libraries: List<String>? = null
    @Input @Optional @JsonProperty("has-open-classloader") var hasOpenClassloader: Boolean? = null
    @Input @Optional @JsonProperty("folia-supported") var foliaSupported: Boolean? = null

    @Nested @Optional @JsonProperty("dependencies") @JsonSerialize(`as` = Collection::class)
    var depends: NamedDomainObjectContainer<DependencyDefinition> = project.container(DependencyDefinition::class.java)
    @Nested @Optional @JsonProperty("load-before") @JsonSerialize(`as` = Collection::class)
    var loadBefore: NamedDomainObjectContainer<LoadDefinition> = project.container(LoadDefinition::class.java)
    @Nested @Optional @JsonProperty("load-after") @JsonSerialize(`as` = Collection::class)
    var loadAfter: NamedDomainObjectContainer<LoadDefinition> = project.container(LoadDefinition::class.java)

    @Nested val permissions: NamedDomainObjectContainer<Permission> = project.container(Permission::class.java)

    // For Groovy DSL
    fun permissions(closure: Closure<Unit>) = permissions.configure(closure)
    fun depends(closure: Closure<Unit>) = depends.configure(closure)
    fun loadBefore(closure: Closure<Unit>) = loadBefore.configure(closure)
    fun loadAfter(closure: Closure<Unit>) = loadAfter.configure(closure)

    enum class PluginLoadOrder {
        STARTUP,
        POSTWORLD
    }

    data class DependencyDefinition(@Input val name: String) {
        @Input var required: Boolean = false
        @Input var bootstrap: Boolean = false
    }

    data class LoadDefinition(@Input val name: String) {
        @Input var bootstrap: Boolean = false
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
