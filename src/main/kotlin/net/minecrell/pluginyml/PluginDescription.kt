/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml

import com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.api.tasks.Input

abstract class PluginDescription {
    @Input @JsonIgnore var generateLibrariesJson: Boolean = false
}
