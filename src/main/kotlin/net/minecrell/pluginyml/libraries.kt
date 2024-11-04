/*
 *     SPDX-License-Identifier: MIT
 *
 *     Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *     Copyright (c) 2024 EldoriaRPG and Contributor <https://github.com/eldoriarpg>
 */

package net.minecrell.pluginyml

import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult

fun ResolvedComponentResult?.collectLibraries(additional: List<String>? = null): List<String> {
    val resolved = this?.dependencies?.map {
        d -> (d as? ResolvedDependencyResult)?.selected?.moduleVersion?.toString() ?: error("No moduleVersion for $d")
    }
    return ((additional ?: listOf()) + (resolved ?: listOf())).distinct()
}
