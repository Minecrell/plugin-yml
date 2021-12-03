plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.18.0"
    id("org.cadixdev.licenser") version "0.6.1"
}

val url: String by extra

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.0")
}

java {
    withSourcesJar()
}

gradlePlugin {
    plugins {
        register("bukkit") {
            id = "net.minecrell.plugin-yml.bukkit"
            displayName = "plugin-yml (Bukkit)"
            description = "Generate plugin.yml for Bukkit plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.bukkit.BukkitPlugin"
        }
        register("bungee") {
            id = "net.minecrell.plugin-yml.bungee"
            displayName = "plugin-yml (BungeeCord)"
            description = "Generate bungee.yml for BungeeCord plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.bungee.BungeePlugin"
        }
        register("nukkit") {
            id = "net.minecrell.plugin-yml.nukkit"
            displayName = "plugin-yml (Nukkit)"
            description = "Generate nukkit.yml for Nukkit plugins based on the Gradle project"
            implementationClass = "net.minecrell.pluginyml.nukkit.NukkitPlugin"
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

pluginBundle {
    website = url
    vcsUrl = url
    description = project.description
    tags = listOf("bukkit", "bungee", "nukkit")
}
