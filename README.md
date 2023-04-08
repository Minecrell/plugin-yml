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
    id 'net.minecrell.plugin-yml.bukkit' version '0.5.3'
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    library 'com.google.code.gson:gson:2.8.7' // All platforms
    bukkitLibrary 'com.google.code.gson:gson:2.8.7' // Bukkit only
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
    
    // API version (should be set for 1.13+)
    apiVersion = '1.13'
    
    // Other possible properties from plugin.yml (optional)
    load = 'STARTUP' // or 'POSTWORLD' 
    authors = ['Notch', 'Notch2']
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
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    library(kotlin("stdlib")) // All platforms
    library("com.google.code.gson", "gson", "2.8.7") // All platforms
    bukkitLibrary("com.google.code.gson", "gson", "2.8.7") // Bukkit only
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
    
    // API version (should be set for 1.13+)
    apiVersion = "1.13"
    
    // Other possible properties from plugin.yml (optional)
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP // or POSTWORLD 
    authors = listOf("Notch", "Notch2")
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

#### Importing library via paperLibrary

Paperplugins do not support library loading like spigot does. 
Instead, libraries and repositories need to be defined via a PluginLoader implementation inside your plugin.
To give you access to repositories and dependencies marked as `paperLibrary` creates a file called `plugin-libraries.json`.
To generate this file you need to set `generatePluginLibraries` to `true`
You can load them as a resource afterward.

An example `PluginLoader` implementation could look like this:
```java
public class Loader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        PluginLibraries pluginLibraries = load();
        pluginLibraries.asDependencies().forEach(resolver::addDependency);
        pluginLibraries.asRepositories().forEach(resolver::addRepository);
        classpathBuilder.addLibrary(resolver);
    }

    public PluginLibraries load() {
        try (var in = getClass().getResourceAsStream("/plugin-libraries.json")) {
            return new Gson().fromJson(new String(in.readAllBytes()), PluginLibraries.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record PluginLibraries(List<String> repositories, List<String> dependencies) {
        public List<Dependency> asDependencies() {
            return dependencies.stream()
                    .map(d -> new Dependency(new DefaultArtifact(d), null))
                    .toList();
        }

        public List<RemoteRepository> asRepositories() {
            AtomicInteger integer = new AtomicInteger();
            return repositories.stream()
                    .map(d -> new RemoteRepository.Builder("maven" + integer.getAndIncrement(), "default", d).build())
                    .toList();
        }
    }
}
```

<details>
<summary><strong>Groovy</strong></summary>

```groovy
plugins {
    id 'net.minecrell.plugin-yml.paper' version '0.5.3'
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    library 'com.google.code.gson:gson:2.8.7' // All platforms
    paperLibrary 'com.google.code.gson:gson:2.8.7' // Bukkit only
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

    // generate plugin-libraries.json
    generatePluginLibraries = true

    // Mark plugin for supporting Folia
    foliaSupported = true

    // API version (Needs to be 1.19 or higher)
    apiVersion = '1.19'
    
    // Other possible properties from plugin.yml (optional)
    load = 'STARTUP' // or 'POSTWORLD' 
    authors = ['Notch', 'Notch2']
    depends {
        'WorldEdit' {
            required : true
            bootstrap: true
        }
        'Essentials' {
        }
    }
    
    loadBefore {
        'BrokenPlugin' {
            bootstrap: true
        }
    }
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
    id("net.minecrell.plugin-yml.paper") version "0.5.3"
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    library(kotlin("stdlib")) // All platforms
    library("com.google.code.gson", "gson", "2.8.7") // All platforms
    paperLibrary("com.google.code.gson", "gson", "2.8.7") // Bukkit only
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
    
    // generate plugin-libraries.json
    generatePluginLibraries = true
    
    // Mark plugin for supporting Folia
    foliaSupported = true

    // API version (Needs to be 1.19 or higher)
    apiVersion = "1.19"
    
    // Other possible properties from plugin.yml (optional)
    load = PaperPluginDescription.PluginLoadOrder.STARTUP // or POSTWORLD 
    authors = listOf("Notch", "Notch2")
    
    depends {
        // Required dependency
        register("WorldEdit") {
            required = true
            bootstrap = true
        }
        // Optional dependency
        register("Essentials") {
        }
    }
    
    loadBefore {
        register("BrokenPlugin") {
            bootstrap = true
        }
    }
    
    prefix = "TEST"
    defaultPermission = PaperPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
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
            default = PaperPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
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
    id 'net.minecrell.plugin-yml.bungee' version '0.5.3'
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    library 'com.google.code.gson:gson:2.8.7' // All platforms
    bungeeLibrary 'com.google.code.gson:gson:2.8.7' // Bungee only
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
    id("net.minecrell.plugin-yml.bungee") version "0.5.3"
}

dependencies {
    // Downloaded from Maven Central when the plugin is loaded
    library(kotlin("stdlib")) // All platforms
    library("com.google.code.gson", "gson", "2.8.7") // All platforms
    bungeeLibrary("com.google.code.gson", "gson", "2.8.7") // Bungee only
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
    id 'net.minecrell.plugin-yml.nukkit' version '0.5.3'
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
    id("net.minecrell.plugin-yml.nukkit") version "0.5.3"
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

[plugin-yml]: https://github.com/Minecrell/plugin-yml
