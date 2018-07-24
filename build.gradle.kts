import org.gradle.api.tasks.bundling.Jar

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.9.10"
    id("net.minecrell.licenser") version "0.3"
}

val url: String by extra

repositories {
    gradlePluginPortal()
}

dependencies {
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.6") {
        exclude(group = "org.jetbrains.kotlin")
    }
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.6")
}

var sourceJar = task<Jar>("sourceJar") {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}
artifacts.add("archives", sourceJar)

gradlePlugin {
    (plugins) {
        "bukkit" {
            id = "net.minecrell.plugin-yml.bukkit"
            implementationClass = "net.minecrell.pluginyml.bukkit.BukkitPlugin"
        }
        "bungee" {
            id = "net.minecrell.plugin-yml.bungee"
            implementationClass = "net.minecrell.pluginyml.bungee.BungeePlugin"
        }
        "nukkit" {
            id = "net.minecrell.plugin-yml.nukkit"
            implementationClass = "net.minecrell.pluginyml.nukkit.NukkitPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourceJar)
        }
    }
}

pluginBundle {
    website = url
    vcsUrl = url
    description = project.description
    tags = listOf("bukkit", "bungee", "nukkit")

    (plugins) {
        "bukkit" {
            id = "net.minecrell.plugin-yml.bukkit"
            displayName = "plugin-yml (Bukkit)"
        }
        "bungee" {
            id = "net.minecrell.plugin-yml.bungee"
            displayName = "plugin-yml (Bungee)"
        }
        "nukkit" {
            id = "net.minecrell.plugin-yml.nukkit"
            displayName = "plugin-yml (Nukkit)"
        }
    }
}
