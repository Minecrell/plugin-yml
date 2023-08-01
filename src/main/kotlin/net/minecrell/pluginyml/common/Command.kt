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
