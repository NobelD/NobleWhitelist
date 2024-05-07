plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.nobeld.noblewhitelist.discord"
version = "1.1.1"
description = "Discord integration for the NobleWhitelist plugin."

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
        name = "jitpack.io"
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.simplix-softworks")
            includeGroup("com.github.MinnDevelopment")
        }
    }
}

dependencies {
    compileOnly(project(":nwl-spigot"))
    compileOnly(project(":nwl-core"))

    implementation("com.alessiodp.libby", "libby-paper", "2.0.0-20240104.190327-5") {
        exclude(module=("libby-core"))
    }
    implementation("com.alessiodp.libby", "libby-bukkit", "2.0.0-20240104.190327-5") {
        exclude(module=("libby-core"))
    }
    implementation("com.alessiodp.libby", "libby-core", "2.0.0-20240104.190327-5") {
        exclude(module=("spigot-api"))
    }
    compileOnly("com.github.simplix-softworks","simplixstorage","3.2.6")
    compileOnly("org.incendo", "cloud-jda5", "1.0.0-beta.2")
    compileOnly("org.incendo", "cloud-processors-requirements", "1.0.0-beta.2")

    compileOnly("net.kyori","adventure-platform-bukkit","4.3.2")
    compileOnly("net.kyori","adventure-text-minimessage","4.15.0")

    compileOnly("net.dv8tion", "JDA", "5.0.0-beta.20") {
        exclude(module= "opus-java")
    }
    compileOnly("com.github.MinnDevelopment", "emoji-java", "v6.1.0")
    compileOnly("club.minnced", "discord-webhooks", "0.8.4") {
        exclude(module= "okhttp")
    }
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.17.1")
    implementation("com.google.code.gson", "gson", "2.10.1")
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
        dependencies {
            include(dependency("com.alessiodp.libby:libby-paper"))
            include(dependency("com.alessiodp.libby:libby-bukkit"))
            include(dependency("com.alessiodp.libby:libby-core"))
        }

        archiveClassifier.set("")
        fun reloc(pkg: String) = relocate(pkg, "me.nobeld.noblewhitelist.discord.libs.$pkg")

        // JDA
        //#TODO Fix relocation
        /*
        reloc("net.dv8tion.jda")
        reloc("com.neovisionaries.ws")
        reloc("okhttp3")
        reloc("com.iwebpp.crypto")
        reloc("org.apache.commons.collections4")
        reloc("com.fasterxml.jackson.databind")
        reloc("com.fasterxml.jackson.core")
        reloc("com.fasterxml.jackson.annotation")
        reloc("gnu.trove")

        // Emoji
        reloc("com.vdurmont.emoji")
        reloc("org.json")

        // discord-webhooks
        reloc("club.minnced.discord.webhook")
        */

        reloc("com.alessiodp.libby")
        relocate("com.esotericsoftware", "me.nobeld.noblewhitelist.libs.com.esotericsoftware")
        relocate("de.leonhard", "me.nobeld.noblewhitelist.libs.de.leonhard")
        reloc("org.intellij")
        reloc("org.jetbrains")
        // reloc("org.incendo")
    }
}