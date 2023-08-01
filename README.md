# plugin-yml
[plugin-yml] is a simple Gradle plugin that generates the `plugin.yml` plugin description file for Bukkit plugins,
`paper-plugin.yml` for Paper plugins, `bungee.yml` for Bungee plugins or `nukkit.yml` for Nukkit plugins based on
the Gradle project. Various properties are set automatically (e.g. project name, version or description) and
additional properties can be added using a simple DSL.

## Usage
[plugin-yml] requires at least **Gradle 7.4**. Using the latest version of Gradle is recommended.
If you are using an older version of Gradle, try using an older version of plugin-yml as well.
plugin-yml `0.5.2` still supports Gradle 5.0+.

### Default values

| Property | Value |
| ------------- | ------------- |
| Plugin name | Project name |
| Plugin version | Project version |
| Plugin description | Project description |
| Plugin URL (Bukkit only) | `url` project property |
| Plugin author | `author` project property |

### Bukkit

<details>
<summary><strong>Groovy</strong></summary>

```groovy
plugins {
    id 'net.minecrell.plugin-yml.bukkit' version '0.6.0'
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    library 'com.google.code.gson:gson:2.10.1' // All platform plugins
    bukkitLibrary 'com.google.code.gson:gson:2.10.1' // Bukkit only
}

bukkit {
    // Default values can be overridden if needed
    // name = 'TestPlugin'
    // version = '1.0'
    // description = 'This is a test plugin'
    // website = 'https://example.com'
    // author = 'Notch'

    // Plugin main class (required)
    main = 'com.example.testplugin.TestPlugin'

    // Mark plugin for supporting Folia
    foliaSupported = true

    // API version (should be set for 1.13+)
    apiVersion = '1.13'

    // Other possible properties from plugin.yml (optional)
    load = 'STARTUP' // or 'POSTWORLD'
    authors = ['Notch', 'Notch2']
    contributors = ['Notch3', 'Notch4']
    depend = ['WorldEdit']
    softDepend = ['Essentials']
    loadBefore = ['BrokenPlugin']
    prefix = 'TEST'
    defaultPermission = 'OP' // 'TRUE', 'FALSE', 'OP' or 'NOT_OP'
    provides = ['TestPluginOldName', 'TestPlug']

    commands {
        test {
            description = 'This is a test command!'
            aliases = ['t']
            permission = 'testplugin.test'
            usage = 'Just run the command!'
            // permissionMessage = 'You may not test this command!'
        }
        // ...
    }

    permissions {
        'testplugin.*' {
            children = ['testplugin.test'] // Defaults permissions to true
            // You can also specify the values of the permissions
            childrenMap = ['testplugin.test': false]
        }
        'testplugin.test' {
            description = 'Allows you to run the test command'
            setDefault('OP') // 'TRUE', 'FALSE', 'OP' or 'NOT_OP'
        }
    }
}
```
</details>

<details>
<summary><strong>kotlin-dsl</strong></summary>

```kotlin
plugins {
    java // or `kotlin("jvm") version "..."`
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    // library(kotlin("stdlib")) // When using kotlin
    library("com.google.code.gson", "gson", "2.10.1") // All platform plugins
    bukkitLibrary("com.google.code.gson", "gson", "2.10.1") // Bukkit only
}

bukkit {
    // Default values can be overridden if needed
    // name = "TestPlugin"
    // version = "1.0"
    // description = "This is a test plugin"
    // website = "https://example.com"
    // author = "Notch"

    // Plugin main class (required)
    main = "com.example.testplugin.TestPlugin"

    // Mark plugin for supporting Folia
    foliaSupported = true

    // API version (should be set for 1.13+)
    apiVersion = "1.13"

    // Other possible properties from plugin.yml (optional)
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP // or POSTWORLD
    authors = listOf("Notch", "Notch2")
    contributors = listOf("Notch3", "Notch4")
    depend = listOf("WorldEdit")
    softDepend = listOf("Essentials")
    loadBefore = listOf("BrokenPlugin")
    prefix = "TEST"
    defaultPermission = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
    provides = listOf("TestPluginOldName", "TestPlug")

    commands {
        register("test") {
            description = "This is a test command!"
            aliases = listOf("t")
            permission = "testplugin.test"
            usage = "Just run the command!"
            // permissionMessage = "You may not test this command!"
        }
        // ...
    }

    permissions {
        register("testplugin.*") {
            children = listOf("testplugin.test") // Defaults permissions to true
            // You can also specify the values of the permissions
            childrenMap = mapOf("testplugin.test" to true)
        }
        register("testplugin.test") {
            description = "Allows you to run the test command"
            default = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
        }
    }
}
```
</details>

### Paper
<details>
<summary><strong>Groovy</strong></summary>

```groovy
plugins {
    id 'java'
    id 'net.minecrell.plugin-yml.paper' version '0.6.0'
}

repositories {
    mavenCentral()
    maven { url "https://papermc.io/repo/repository/maven-public/" }
}

// NOTE: Paper does not support plugin libraries without additional setup!
// Please see "Plugin Libraries JSON" in the README for instructions.
dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    library 'com.google.code.gson:gson:2.10.1' // All platform plugins
    paperLibrary 'com.google.code.gson:gson:2.10.1' // Paper only

    // Make use of classes included by `bootstrapDependencies` and `serverDependencies` sections below
    //   compileOnly 'com.sk89q.worldedit:worldedit-bukkit:7.2.14'
}

paper {
    // Default values can be overridden if needed
    // name = 'TestPlugin'
    // version = '1.0'
    // description = 'This is a test plugin'
    // website = 'https://example.com'
    // author = 'Notch'

    // Plugin main class (required)
    main = 'com.example.testplugin.TestPlugin'

    // Plugin bootstrapper/loader (optional)
    bootstrapper = 'com.example.testplugin.bootstrap.TestPluginBootstrap'
    loader = 'com.example.testplugin.loader.TestPluginLoader'
    hasOpenClassloader = false

    // Generate paper-libraries.json from `library` and `paperLibrary` in `dependencies`
    generateLibrariesJson = true

    // Mark plugin for supporting Folia
    foliaSupported = true

    // API version (needs to be 1.19 or higher)
    apiVersion = '1.19'

    // Other possible properties from paper-plugin.yml (optional)
    load = 'STARTUP' // or 'POSTWORLD'
    authors = ['Notch', 'Notch2']
    contributors = ['Notch3', 'Notch4']
    prefix = 'TEST'
    provides = ['TestPluginOldName', 'TestPlug']

    // Bootstrap dependencies - Very rarely needed
    bootstrapDependencies {
        // Required dependency during bootstrap
        'WorldEdit' {}

        // During bootstrap, load BeforePlugin's bootstrap code before ours
        'BeforePlugin' {
            load = 'BEFORE'
            required = false
            joinClasspath = false
        }
        // During bootstrap, load AfterPlugin's bootstrap code after ours
        'AfterPlugin' {
            load = 'AFTER'
            required = false
            joinClasspath = false
        }
    }

    serverDependencies {
        // During server run time, require LuckPerms, add it to the classpath, and load it before us
        'LuckPerms' {
            load = 'BEFORE'
        }

        // During server run time, require WorldEdit, add it to the classpath, and load it before us
        'WorldEdit' {
            load = 'BEFORE'
        }

        // Optional dependency, add it to classpath if it is available
        'ProtocolLib' {
            required = false
        }

        // During server run time, optionally depend on Essentials but do not add it to the classpath
        'Essentials' {
            required = false
            joinClasspath = false
        }
    }

    commands {
<<<<<<< HEAD
        test {
            description = 'This is a test command!'
            aliases = ['t']
            permission = 'testplugin.test'
            usage = 'Just run the command!'
            // permissionMessage = 'You may not test this command!'
        }
        // ...
    }
    
=======
        register("test") {
            description = "This is a test command!"
            aliases = listOf("t")
            permission = "testplugin.test"
            usage = "Just run the command!"
            // permissionMessage = "You may not test this command!"
        }
        // ...
    }

>>>>>>> impl-paper-commands
    permissions {
        'testplugin.*' {
            children = ['testplugin.test'] // Defaults permissions to true
            // You can also specify the values of the permissions
            childrenMap = ['testplugin.test': false]
        }
        'testplugin.test' {
            description = 'Allows you to run the test command'
            setDefault('OP') // 'TRUE', 'FALSE', 'OP' or 'NOT_OP'
        }
    }
}
```
</details>

<details>
<summary><strong>kotlin-dsl</strong></summary>

```kotlin
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java // or `kotlin("jvm") version "..."`
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

// NOTE: Paper does not support plugin libraries without additional setup!
// Please see "Plugin Libraries JSON" in the README for instructions.
dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    // library(kotlin("stdlib")) // When using kotlin
    library("com.google.code.gson", "gson", "2.10.1") // All platform plugins
    paperLibrary("com.google.code.gson", "gson", "2.10.1") // Paper only

    // Make use of classes included by `bootstrapDependencies` and `serverDependencies` sections below
    //   compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.14")
}

paper {
    // Default values can be overridden if needed
    // name = "TestPlugin"
    // version = "1.0"
    // description = "This is a test plugin"
    // website = "https://example.com"
    // author = "Notch"

    // Plugin main class (required)
    main = "com.example.testplugin.TestPlugin"

    // Plugin bootstrapper/loader (optional)
    bootstrapper = "com.example.testplugin.bootstrap.TestPluginBootstrap"
    loader = "com.example.testplugin.loader.TestPluginLoader"
    hasOpenClassloader = false

    // Generate paper-libraries.json from `library` and `paperLibrary` in `dependencies`
    generateLibrariesJson = true

    // Mark plugin for supporting Folia
    foliaSupported = true

    // API version (Needs to be 1.19 or higher)
    apiVersion = "1.19"

    // Other possible properties from plugin.yml (optional)
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP // or POSTWORLD
    authors = listOf("Notch", "Notch2")

    prefix = "TEST"
    defaultPermission = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
    provides = listOf("TestPluginOldName", "TestPlug")

    bootstrapDependencies {
        // Required dependency during bootstrap
        register("WorldEdit")

        // During bootstrap, load BeforePlugin's bootstrap code before ours
        register("BeforePlugin") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        // During bootstrap, load AfterPlugin's bootstrap code after ours
        register("AfterPlugin") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
    }

    serverDependencies {
        // During server run time, require LuckPerms, add it to the classpath, and load it before us
        register("LuckPerms") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        // During server run time, require WorldEdit, add it to the classpath, and load it before us
        register("WorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        // Optional dependency, add it to classpath if it is available
        register("ProtocolLib") {
            required = false
        }

        // During server run time, optionally depend on Essentials but do not add it to the classpath
        register("Essentials") {
            required = false
            joinClasspath = false
        }
    }

    commands {
        register("test") {
            description = "This is a test command!"
            aliases = listOf("t")
            permission = "testplugin.test"
            usage = "Just run the command!"
            // permissionMessage = "You may not test this command!"
        }
        // ...
    }

    permissions {
        register("testplugin.*") {
            children = listOf("testplugin.test") // Defaults permissions to true
            // You can also specify the values of the permissions
            childrenMap = mapOf("testplugin.test" to true)
        }
        register("testplugin.test") {
            description = "Allows you to run the test command"
            default = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
        }
    }
}
```
</details>

### BungeeCord

<details>
<summary><strong>Groovy</strong></summary>

```groovy
plugins {
    id 'net.minecrell.plugin-yml.bungee' version '0.6.0'
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    library 'com.google.code.gson:gson:2.10.1' // All platform plugins
    bungeeLibrary 'com.google.code.gson:gson:2.10.1' // Bungee only
}

bungee {
    // Default values can be overridden if needed
    // name = 'TestPlugin'
    // version = '1.0'
    // description = 'This is a test plugin'

    // Plugin main class (required)
    main = 'com.example.testplugin.TestPlugin'

    // Other possible properties from bungee.yml
    author = 'Notch'
    depends = ['Yamler']
    softDepends = ['ServerListPlus']
}
```
</details>

<details>
<summary><strong>kotlin-dsl</strong></summary>

```kotlin
plugins {
    java // or `kotlin("jvm") version "..."`
    id("net.minecrell.plugin-yml.bungee") version "0.6.0"
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    // library(kotlin("stdlib")) // When using kotlin
    library("com.google.code.gson", "gson", "2.10.1") // All platform plugins
    bungeeLibrary("com.google.code.gson", "gson", "2.10.1") // Bungee only
}

bungee {
    // Default values can be overridden if needed
    // name = "TestPlugin"
    // version = "1.0"
    // description = "This is a test plugin"

    // Plugin main class (required)
    main = "com.example.testplugin.TestPlugin"

    // Other possible properties from bungee.yml
    author = "Notch"
    depends = setOf("Yamler")
    softDepends = setOf("ServerListPlus")
}
```
</details>

### Nukkit

<details>
<summary><strong>Groovy</strong></summary>

```groovy
plugins {
    id 'net.minecrell.plugin-yml.nukkit' version '0.6.0'
}

nukkit {
    // Default values can be overridden if needed
    // name = 'TestPlugin'
    // version = '1.0'
    // description = 'This is a test plugin'
    // website = 'https://example.com'
    // author = 'Notch'

    // Plugin main class and api (required)
    main = 'com.example.testplugin.TestPlugin'
    api = ['1.0.0']

    // Other possible properties from nukkit.yml (optional)
    load = 'STARTUP' // or 'POSTWORLD'
    authors = ['Notch', 'Notch2']
    depend = ['PlotSquared']
    softDepend = ['LuckPerms']
    loadBefore = ['BrokenPlugin']
    prefix = 'TEST'

    commands {
        test {
            description = 'This is a test command!'
            aliases = ['t']
            permission = 'testplugin.test'
            usage = 'Just run the command!'
        }
        // ...
    }

    permissions {
        'testplugin.*' {
            description = 'Allows you to run all testplugin commands'
            children {
                'testplugin.test' {
                    description = 'Allows you to run the test command'
                    setDefault('OP') // 'TRUE', 'FALSE', 'OP' or 'NOT_OP'
                }
            }
        }
    }
}
```
</details>

<details>
<summary><strong>kotlin-dsl</strong></summary>

```kotlin
plugins {
    java // or `kotlin("jvm") version "..."`
    id("net.minecrell.plugin-yml.nukkit") version "0.6.0"
}

nukkit {
    // Default values can be overridden if needed
    // name = "TestPlugin"
    // version = "1.0"
    // description = "This is a test plugin"
    // website = "https://example.com"
    // author = "Notch"

    // Plugin main class and api (required)
    main = "com.example.testplugin.TestPlugin"
    api = listOf("1.0.0")

    // Other possible properties from nukkit.yml (optional)
    load = NukkitPluginDescription.PluginLoadOrder.STARTUP // or POSTWORLD
    authors = listOf("Notch", "Notch2")
    depend = listOf("PlotSquared")
    softDepend = listOf("LuckPerms")
    loadBefore = listOf("BrokenPlugin")
    prefix = "TEST"

    commands {
        register("test") {
            description = "This is a test command!"
            aliases = listOf("t")
            permission = "testplugin.test"
            usage = "Just run the command!"
        }
        // ...
    }

    permissions {
        register("testplugin.*") {
            description = "Allows you to run all testplugin commands"
            children {
                register("testplugin.test") {
                    description = "Allows you to run the test command"
                    default = NukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
                }
            }
        }
    }
}
```
</details>

## Plugin Libraries JSON
Paper and Nukkit do not support specifying libraries directly in the plugin description file.
plugin-yml still allows defining dependencies as `paperLibrary` and `nukkitLibrary` but these dependencies are not
exported by default. Additional runtime plugin code is needed to set them up and load them. To simplify this, plugin-yml
can export them in a `paper-libraries.json` / `nukkit-libraries.json` file with the following structure:

```json
{
    "repositories": {"MavenRepo": "https://repo.maven.apache.org/maven2/"},
    "dependencies": ["com.google.code.gson:gson:2.10.1"]
}
```

This file is only generated after setting `generateLibrariesJson` to `true`, e.g.:

```kotlin
paper {
    // generate paper-libraries.json
    generateLibrariesJson = true
}
```

The JSON file is included in the plugin JAR and can be parsed at runtime to load the additional libraries.

### Paper
Define a custom `PluginLoader` inside your plugin code, for example:

<details>
<summary><strong>Example PluginLoader</strong></summary>

```kotlin
paper {
    loader = "com.example.testplugin.PluginLibrariesLoader"
    generateLibrariesJson = true
}
```

```java
import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PluginLibrariesLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        PluginLibraries pluginLibraries = load();
        pluginLibraries.asDependencies().forEach(resolver::addDependency);
        pluginLibraries.asRepositories().forEach(resolver::addRepository);
        classpathBuilder.addLibrary(resolver);
    }

    public PluginLibraries load() {
        try (var in = getClass().getResourceAsStream("/paper-libraries.json")) {
            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record PluginLibraries(Map<String, String> repositories, List<String> dependencies) {
        public Stream<Dependency> asDependencies() {
            return dependencies.stream()
                    .map(d -> new Dependency(new DefaultArtifact(d), null));
        }

        public Stream<RemoteRepository> asRepositories() {
            return repositories.entrySet().stream()
                    .map(e -> new RemoteRepository.Builder(e.getKey(), "default", e.getValue()).build());
        }
    }
}
```

</details>

### Nukkit
(No example code available yet)

### Bukkit/Bungee
`generateLibrariesJson` is also supported on Bukkit/Bungee (to generate `bukkit-libraries.json`/`bungee-libraries.json`).
However, since these two allow specifying libraries directly inside the `plugin.yml` the option is generally not needed
there.

[plugin-yml]: https://github.com/Minecrell/plugin-yml
