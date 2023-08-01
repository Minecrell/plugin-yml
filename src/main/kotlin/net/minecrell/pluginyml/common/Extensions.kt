package net.minecrell.pluginyml.common

import net.minecrell.pluginyml.InvalidPluginDescriptionException
import org.gradle.api.NamedDomainObjectContainer

fun NamedDomainObjectContainer<Command>.validate() = this.forEach { command ->
    if (command.name.contains(":"))
        throw InvalidPluginDescriptionException("Command '${command.name}' cannot contain ':'.")

    command.aliases?.forEach { alias ->
        if (alias.contains(":"))
            throw InvalidPluginDescriptionException("Alias '$alias' of '${command.name}' cannot contain ':'.")
    }
}

/* fun PluginDescription.validateCommands(nestedDomainObjectContainer: NamedDomainObjectContainer<Command>) {
    for (command in nestedDomainObjectContainer) {
        if (command.name.contains(":"))
            throw InvalidPluginDescriptionException("Command '${command.name}' cannot contain ':'.")

        command.aliases?.forEach {
            if (it.contains(":"))
                throw InvalidPluginDescriptionException("Alias '$it' of '${command.name}' cannot contain ':'.")
        }
    }
} */
