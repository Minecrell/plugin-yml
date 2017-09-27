import org.gradle.api.tasks.bundling.Jar

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("net.minecrell.licenser") version "0.3"
}

repositories {
    jcenter()
}

dependencies {
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.1") {
        exclude(group = "org.jetbrains.kotlin")
    }
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.1")
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

// Workaround for https://github.com/gradle/kotlin-dsl/issues/509
configurations.compile.run { setExtendsFrom(extendsFrom.minus<Configuration>(configurations.embeddedKotlin)) }
configurations.compileOnly.extendsFrom(configurations.embeddedKotlin)
