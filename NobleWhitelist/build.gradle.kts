plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.nobeld.minecraft.noblewhitelist"
version = "1.1.1"
description = "A simple plugin for whitelist management."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
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
    }
    maven {
        name = "AlessioDP"
        url = uri("https://repo.alessiodp.com/releases/")
    }
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.20.2-R0.1-SNAPSHOT")
    compileOnly("io.papermc", "paperlib", "1.0.7")

    implementation("net.byteflux", "libby-bukkit", "1.3.0")
    compileOnly("com.github.simplix-softworks","simplixstorage","3.2.6")
    compileOnly("com.zaxxer", "HikariCP", "5.1.0")
    compileOnly("org.xerial", "sqlite-jdbc", "3.44.1.0")

    compileOnly("me.clip:placeholderapi:2.11.4")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.2.3")

    compileOnly("net.kyori","adventure-platform-bukkit","4.3.1")
    compileOnly("net.kyori","adventure-text-minimessage","4.14.0")

    compileOnly("cloud.commandframework", "cloud-paper", "1.8.4")
    compileOnly("cloud.commandframework", "cloud-minecraft-extras", "1.8.4")
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
        fun reloc(pkg: String) = relocate(pkg, "me.nobeld.minecraft.noblewhitelist.libs.$pkg")

        reloc("net.byteflux.libby")
        reloc("io.papermc")
        reloc("com.esotericsoftware")
        reloc("com.zaxxer")
        reloc("de.leonhard")
        reloc("org.intellij")
        reloc("org.jetbrains")
        reloc("org.json")
    }
}