# plugin-yml
[plugin-yml] is a simple Gradle plugin that generates the `plugin.yml` plugin description file for Bukkit plugins,
`bungee.yml` for Bungee plugins or `nukkit.yml` for Nukkit plugins based on the Gradle project. Various properties
are set automatically (e.g. project name, version or description) and additional properties can be added using a
simple DSL.

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
    depend = [
        // Required dependency
        dependency("WorldEdit", required : true, boostrap : true),
        // Optional dependency
        dependency("Essentials")
    ]
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
    load = PaperPluginDescription.PluginLoadOrder.STARTUP // or POSTWORLD 
    authors = listOf("Notch", "Notch2")
    depend = listOf(
        // Required dependency
        dependency("WorldEdit", required = true, boostrap = true),
        // Optional dependency
        dependency("Essentials"))
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
