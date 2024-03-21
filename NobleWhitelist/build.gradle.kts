plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.nobeld.noblewhitelist"
version = "1.2.1"
description = "A simple plugin for whitelist management."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven {
        name = "sonatype"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        name = "jitpack.io"
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.simplix-softworks")
        }
    }
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.20.2-R0.1-SNAPSHOT")
    compileOnly("io.papermc", "paperlib", "1.0.7")

    implementation("com.alessiodp.libby", "libby-bukkit", "2.0.0-SNAPSHOT") {
        exclude(module=("spigot-api"))
    }
    compileOnly("com.github.simplix-softworks","simplixstorage","3.2.6")
    compileOnly("com.zaxxer", "HikariCP", "5.1.0")
    compileOnly("org.xerial", "sqlite-jdbc", "3.44.1.0")

    compileOnly("me.clip", "placeholderapi", "2.11.5")
    compileOnly("io.github.miniplaceholders", "miniplaceholders-api", "2.2.3")

    compileOnly("net.kyori","adventure-platform-bukkit","4.3.2")
    compileOnly("net.kyori","adventure-text-minimessage","4.15.0")

    compileOnly("org.incendo", "cloud-paper", "2.0.0-beta.4")
    compileOnly("org.incendo", "cloud-minecraft-extras", "2.0.0-beta.4")
    compileOnly("org.incendo", "cloud-processors-confirmation", "1.0.0-beta.2")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
                "name" to project.name,
                "version" to project.version,
                "description" to project.description,
                "apiVersion" to "1.17"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        fun reloc(pkg: String) = relocate(pkg, "me.nobeld.noblewhitelist.libs.$pkg")

        reloc("com.alessiodp.libby")
        reloc("io.papermc")
        reloc("com.esotericsoftware")
        reloc("com.zaxxer")
        reloc("de.leonhard")
        reloc("org.intellij")
        reloc("org.jetbrains")
        reloc("org.json")
        reloc("org.incendo")
    }
}