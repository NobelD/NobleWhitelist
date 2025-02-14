import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta5"
}

group = "me.nobeld.noblewhitelist.discord"
version = "1.1.5"
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
            includeGroup("com.github.nobeld")
            includeGroup("com.github.nobeld.libby")
            includeGroup("com.github.MinnDevelopment")
        }
    }
}

dependencies {
    compileOnly(project(":NobleWhitelist"))
    compileOnly("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")

    compileOnly("com.github.nobeld.libby", "libby-bukkit", "2.0.0-beta.1") {
        exclude(module=("spigot-api"))
        exclude(module=("libby-core"))
    }
    compileOnly("com.github.nobeld.libby", "libby-core", "2.0.0-beta.1") {
        exclude(module=("spigot-api"))
    }
    compileOnly("com.github.nobeld","simplixstorage","3.2.9-rc.5")
    compileOnly("org.incendo", "cloud-jda5", "1.0.0-beta.3")
    compileOnly("org.incendo", "cloud-processors-requirements", "1.0.0-rc.1")

    compileOnly("net.dv8tion", "JDA", "5.3.0") {
        exclude(module= "opus-java")
    }
    compileOnly("com.github.MinnDevelopment", "emoji-java", "v6.1.0")
    compileOnly("club.minnced", "discord-webhooks", "0.8.4") {
        exclude(module= "okhttp")
    }
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.17.1")
}

tasks {
    jar {
        archiveClassifier.set("slim")
    }
    assemble {
        dependsOn(shadowJar)
    }
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
            "apiVersion" to "1.18"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        fun reloc(pkg: String) = relocate(pkg, "me.nobeld.noblewhitelist.discord.libs.$pkg")
        fun nwlreloc(pkg: String) = relocate(pkg, "me.nobeld.noblewhitelist.libs.$pkg")

        // JDA
        //#TODO Fix relocation
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

        nwlreloc("com.alessiodp.libby")
        nwlreloc("com.esotericsoftware")
        nwlreloc("de.leonhard")
        reloc("org.intellij")
        reloc("org.jetbrains")
        nwlreloc("org.incendo")
    }
    // Used by discord workflow
    register("printProjectName") {
        doLast {
            println(project.name)
        }
    }
    // Used to draft a release
    register("release") {
        dependsOn(build)
        doLast {
            val ver = version.toString()
            if (!ver.endsWith("-SNAPSHOT")) {
                val shadowFile = shadowJar.get().archiveFile.get().asFile
                val result = rootProject.layout.buildDirectory.get().asFile.toPath()
                    .resolve("libs" + File.separator + shadowFile.nameWithoutExtension + ".jar")
                result.toFile().mkdirs()
                Files.move(shadowFile.toPath(), result, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }
}