/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml.paper

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
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
    @Input @Optional @JsonProperty("defaultPerm") var defaultPermission: Permission.Default? = null
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

    @JsonNaming(KebabCaseStrategy::class)
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
