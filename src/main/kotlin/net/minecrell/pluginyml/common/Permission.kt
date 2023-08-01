package net.minecrell.pluginyml.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

class Permission(@Input @JsonIgnore val name: String, project: Project) {

    @Input
    @Optional
    var description: String? = null
    @Input
    @Optional
    var default: Default? = null

    @Nested
    val children: NamedDomainObjectContainer<Permission> = project.container(Permission::class.java)

    // For Groovy DSL
    fun children(closure: Closure<Unit>) = children.configure(closure)

    enum class Default {
        @JsonProperty("true")
        TRUE,
        @JsonProperty("false")
        FALSE,
        @JsonProperty("op")
        OP,
        @JsonProperty("!op")
        NOT_OP
    }
}

/* original from NukkitPluginDescription
class Permission(@Input @JsonIgnore val name: String, project: Project) {

    @Input @Optional var description: String? = null
    @Input @Optional var default: Default? = null

    @Nested val children: NamedDomainObjectContainer<Permission> = project.container(Permission::class.java)

    // For Groovy DSL
    fun children(closure: Closure<Unit>) = children.configure(closure)

    enum class Default {
        @JsonProperty("true")   TRUE,
        @JsonProperty("false")  FALSE,
        @JsonProperty("op")     OP,
        @JsonProperty("!op")    NOT_OP
    }
}*/
