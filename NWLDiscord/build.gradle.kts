plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.nobeld.noblewhitelist.discord"
version = "1.1.0"
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
    compileOnly(project(":NobleWhitelist"))
    compileOnly("io.papermc.paper", "paper-api", "1.20.2-R0.1-SNAPSHOT")

    implementation("com.alessiodp.libby", "libby-bukkit", "2.0.0-SNAPSHOT") {
        exclude(module=("spigot-api"))
    }
    compileOnly("com.github.simplix-softworks","simplixstorage","3.2.6")
    compileOnly("org.incendo", "cloud-jda5", "1.0.0-beta.2")
    compileOnly("org.incendo", "cloud-processors-requirements", "1.0.0-beta.2")

    compileOnly("net.dv8tion", "JDA", "5.0.0-beta.20") {
        exclude(module= "opus-java")
    }
    compileOnly("com.github.MinnDevelopment", "emoji-java", "v6.1.0")
    compileOnly("club.minnced", "discord-webhooks", "0.8.4") {
        exclude(module= "okhttp")
    }
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.17.1")
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
            include(dependency("com.alessiodp.libby:libby-bukkit"))
            include(dependency("com.alessiodp.libby:libby-core"))
            /*

            // JDA
            include(dependency("net.dv8tion:JDA"))
            include(dependency("com.neovisionaries:nv-websocket-client"))
            include(dependency("com.squareup.okhttp3:okhttp"))
            include(dependency("com.squareup.okio:okio"))
            include(dependency("com.squareup.okio:okio-jvm"))
            include(dependency("org.apache.commons:commons-collections4"))
            include(dependency("net.sf.trove4j:trove4j"))
            include(dependency("com.fasterxml.jackson.core:jackson-databind"))
            include(dependency("com.fasterxml.jackson.core:jackson-core"))
            include(dependency("com.fasterxml.jackson.core:jackson-annotations"))
            include(dependency("org.slf4j:slf4j-api"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))

            // Emoji
            include(dependency("com.github.MinnDevelopment:emoji-java"))
            include(dependency("org.json:json"))

            // discord-webhooks
            include(dependency("club.minnced:discord-webhooks"))
            */
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

        reloc("io.papermc")
        reloc("com.alessiodp.libby")
        relocate("com.esotericsoftware", "me.nobeld.noblewhitelist.libs.com.esotericsoftware")
        relocate("de.leonhard", "me.nobeld.noblewhitelist.libs.de.leonhard")
        reloc("net.kyori.examination")
        reloc("org.intellij")
        reloc("org.jetbrains")
        // reloc("org.incendo")
    }
}