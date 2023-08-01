package net.minecrell.pluginyml.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

data class Command(@Input @JsonIgnore val name: String) {

    @Input
    @Optional
    var description: String? = null

    @Input
    @Optional
    var aliases: List<String>? = null

    @Input
    @Optional
    var permission: String? = null

    @Input
    @Optional
    @JsonProperty("permission-message")
    var permissionMessage: String? = null

    @Input
    @Optional
    var usage: String? = null
}

/* original from BukkitPluginDescription
data class Command(@Input @JsonIgnore val name: String) {
    @Input @Optional var description: String? = null
    @Input @Optional var aliases: List<String>? = null
    @Input @Optional var permission: String? = null
    @Input @Optional @JsonProperty("permission-message") var permissionMessage: String? = null
    @Input @Optional var usage: String? = null
} */
