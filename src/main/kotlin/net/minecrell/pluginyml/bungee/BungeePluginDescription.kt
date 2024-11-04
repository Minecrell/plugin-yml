/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml.bungee

import net.minecrell.pluginyml.PluginDescription
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class BungeePluginDescription : PluginDescription() {
    @Input var name: String? = null
    @Input var main: String? = null
    @Input @Optional var version: String? = null
    @Input @Optional var author: String? = null
    @Input @Optional var depends: Set<String>? = null
    @Input @Optional var softDepends: Set<String>? = null
    @Input @Optional var description: String? = null
    @Input @Optional var libraries: List<String>? = null
}
