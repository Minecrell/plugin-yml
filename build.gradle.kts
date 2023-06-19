plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.0"
    id("org.cadixdev.licenser") version "0.6.1"
}

val url: String by extra

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
}

gradlePlugin {
    website.set(url)
    vcsUrl.set(url)

    plugins {
        register("bukkit") {
            id = "net.minecrell.plugin-yml.bukkit"
            displayName = "plugin-yml (Bukkit)"
            description = "Generate plugin.yml for Bukkit plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.bukkit.BukkitPlugin"
            tags.set(listOf("bukkit"))
        }
        register("bungee") {
            id = "net.minecrell.plugin-yml.bungee"
            displayName = "plugin-yml (BungeeCord)"
            description = "Generate bungee.yml for BungeeCord plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.bungee.BungeePlugin"
            tags.set(listOf("bungee"))
        }
        register("nukkit") {
            id = "net.minecrell.plugin-yml.nukkit"
            displayName = "plugin-yml (Nukkit)"
            description = "Generate nukkit.yml for Nukkit plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.nukkit.NukkitPlugin"
            tags.set(listOf("nukkit"))
        }
        register("paper") {
            id = "net.minecrell.plugin-yml.paper"
            displayName = "plugin-yml (Paper)"
            description = "Generate paper-plugin.yml for Paper plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.paper.PaperPlugin"
            tags.set(listOf("paper"))
        }
    }
}
