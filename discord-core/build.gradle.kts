plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.nobeld.noblewhitelist.discord"
version = "2.0.0-SNAPSHOT"
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
    compileOnly(project(":nwl-core"))

    compileOnly("com.github.simplix-softworks", "simplixstorage", "3.2.6")
    implementation("org.incendo", "cloud-jda5", "1.0.0-beta.2")
    implementation("org.incendo", "cloud-processors-requirements", "1.0.0-beta.2")

    implementation("net.kyori", "adventure-text-minimessage", "4.15.0")

    implementation("net.dv8tion", "JDA", "5.0.0-beta.20") {
        exclude(module = "opus-java")
    }
    implementation("com.github.MinnDevelopment", "emoji-java", "v6.1.0")
    implementation("club.minnced", "discord-webhooks", "0.8.4") {
        exclude(module = "okhttp")
    }
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.17.1")
    implementation("com.google.code.gson", "gson", "2.10.1")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        dependencies {
            fun incdep(dependency: String) = include(dependency(dependency))

            // JDA
            incdep("net.dv8tion:JDA")
            incdep("com.neovisionaries:nv-websocket-client")
            incdep("com.squareup.okhttp3:okhttp")
            incdep("com.squareup.okio:okio")
            incdep("com.squareup.okio:okio-jvm")
            incdep("org.apache.commons:commons-collections4")
            incdep("net.sf.trove4j:trove4j")
            incdep("com.fasterxml.jackson.core:jackson-databind")
            incdep("com.fasterxml.jackson.core:jackson-core")
            incdep("com.fasterxml.jackson.core:jackson-annotations")
            incdep("org.slf4j:slf4j-api")
            incdep("org.jetbrains.kotlin:kotlin-stdlib")

            // Emoji
            incdep("com.github.MinnDevelopment:emoji-java")
            incdep("org.json:json")

            // discord-webhooks
            incdep("club.minnced:discord-webhooks")

            // TODO Temp comment, add base deps
            //incdep("org.incendo:")
            //incdep("com.esotericsoftware:")
            //incdep("de.leonhard:")
            //incdep("io.leangen.geantyref:")
        }

        archiveClassifier.set("")
        fun reloc(pkg: String) = relocate(pkg, "me.nobeld.noblewhitelist.discord.libs.$pkg")
        fun relocnwl(pkg: String) = relocate(pkg, "me.nobeld.noblewhitelist.libs.$pkg")

        //#TODO Fix relocation (cause: cloud java version)
        // JDA
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

        relocnwl("com.esotericsoftware")
        relocnwl("de.leonhard")
        relocnwl("io.leangen.geantyref")
        reloc("org.intellij")
        reloc("org.jetbrains")
        // reloc("org.incendo")
    }
}