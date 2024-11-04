plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.0"
    id("com.diffplug.spotless") version "6.25.0"
}

val url: String by extra

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.1") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.1")
}

spotless {
    kotlin{
        licenseHeaderFile(rootProject.file("HEADER.txt"))
    }
    java {
        target("**/*.java")
    }
}

gradlePlugin {
    website = url
    vcsUrl = url

    plugins {
        register("bukkit") {
            id = "de.eldoria.plugin-yml.bukkit"
            displayName = "plugin-yml (Bukkit)"
            description = "Generate plugin.yml for Bukkit plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.bukkit.BukkitPlugin"
            tags = listOf("bukkit")
        }
        register("bungee") {
            id = "de.eldoria.plugin-yml.bungee"
            displayName = "plugin-yml (BungeeCord)"
            description = "Generate bungee.yml for BungeeCord plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.bungee.BungeePlugin"
            tags = listOf("bungee")
        }
        register("nukkit") {
            id = "de.eldoria.plugin-yml.nukkit"
            displayName = "plugin-yml (Nukkit)"
            description = "Generate nukkit.yml for Nukkit plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.nukkit.NukkitPlugin"
            tags = listOf("nukkit")
        }
        register("paper") {
            id = "de.eldoria.plugin-yml.paper"
            displayName = "plugin-yml (Paper)"
            description = "Generate paper-plugin.yml for Paper plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.paper.PaperPlugin"
            tags = listOf("paper")
        }
    }
}
