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

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy
import groovy.lang.Closure
import net.minecrell.pluginyml.PluginDescription
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

@JsonNaming(KebabCaseStrategy::class)
class PaperPluginDescription(project: Project) : PluginDescription() {
    @Input var apiVersion: String? = null
    @Input var name: String? = null
    @Input var version: String? = null
    @Input var main: String? = null
    @Input @Optional var bootstrapper: String? = null
    @Input @Optional var loader: String? = null
    @Input @Optional var description: String? = null
    @Input @Optional var load: PluginLoadOrder? = null
    @Input @Optional var author: String? = null
    @Input @Optional var authors: List<String>? = null
    @Input @Optional var contributors: List<String>? = null
    @Input @Optional var website: String? = null
    @Input @Optional var prefix: String? = null
    @Input @Optional var defaultPermission: Permission.Default? = null
    @Input @Optional var provides: List<String>? = null
    @Input @Optional var hasOpenClassloader: Boolean? = null
    @Input @Optional var foliaSupported: Boolean? = null

    @Nested @Optional @JsonIgnore
    var serverDependencies: NamedDomainObjectContainer<DependencyDefinition> = project.container(DependencyDefinition::class.java)
    @Nested @Optional @JsonIgnore
    var bootstrapDependencies: NamedDomainObjectContainer<DependencyDefinition> = project.container(DependencyDefinition::class.java)

    @JsonGetter
    fun dependencies(): Map<String, NamedDomainObjectContainer<DependencyDefinition>> = mapOf(
        "server" to serverDependencies,
        "bootstrap" to bootstrapDependencies,
    )

    @Nested val permissions: NamedDomainObjectContainer<Permission> = project.container(Permission::class.java)

    // For Groovy DSL
    fun permissions(closure: Closure<Unit>) = permissions.configure(closure)
    fun serverDependencies(closure: Closure<Unit>) = serverDependencies.configure(closure)
    fun bootstrapDependencies(closure: Closure<Unit>) = bootstrapDependencies.configure(closure)

    data class DependencyDefinition(@Input @JsonIgnore val name: String) {
        @Input var load: RelativeLoadOrder = RelativeLoadOrder.OMIT
        @Input var required: Boolean = true
        @Input var joinClasspath: Boolean = true
    }

    enum class RelativeLoadOrder {
        BEFORE,
        AFTER,
        OMIT,
    }

}
