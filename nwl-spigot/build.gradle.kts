plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.nobeld.noblewhitelist"
var apiType = "spigot"
version = "2.0.0-SNAPSHOT"
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
    implementation(project(":nwl-core")) {
        exclude(group = "com.github.simplix-softworks")
        exclude(group = "com.zaxxer")
        exclude(group = "org.xerial")
        exclude(group = "com.google.code.gson")
        exclude(group = "net.kyori")
        exclude(group = "org.incendo")
    }
    compileOnly("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")
    implementation("io.papermc", "paperlib", "1.0.7")

    implementation("com.alessiodp.libby", "libby-paper", "2.0.0-20240104.190327-5") {
        exclude(module = ("libby-core"))
    }
    implementation("com.alessiodp.libby", "libby-bukkit", "2.0.0-20240104.190327-5") {
        exclude(module = ("libby-core"))
    }
    compileOnly("com.alessiodp.libby", "libby-core", "2.0.0-20240104.190327-5") {
        exclude(module = ("spigot-api"))
    }
    compileOnly("com.github.simplix-softworks", "simplixstorage", "3.2.6")
    compileOnly("com.zaxxer", "HikariCP", "5.1.0")
    compileOnly("org.xerial", "sqlite-jdbc", "3.44.1.0")

    compileOnly("me.clip", "placeholderapi", "2.11.5")
    compileOnly("io.github.miniplaceholders", "miniplaceholders-api", "2.2.3")

    compileOnly("net.kyori", "adventure-platform-bukkit", "4.3.3")
    compileOnly("net.kyori", "adventure-text-minimessage", "4.17.0")

    compileOnly("org.incendo", "cloud-paper", "2.0.0-beta.9")
    compileOnly("org.incendo", "cloud-minecraft-extras", "2.0.0-beta.9") {
        exclude(module=("adventure-text-minimessage"))
        exclude(module=("adventure-api"))
    }
    compileOnly("org.incendo", "cloud-processors-confirmation", "1.0.0-beta.3") {
        exclude(module=("cloud-core"))
    }
}

tasks {
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
            "name" to rootProject.extra.get("realName"),
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.17"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveBaseName.set("${rootProject.extra.get("lowName")}-${apiType}")
        archiveClassifier.set("")
        fun reloc(pkg: String) = relocate(pkg, "me.nobeld.noblewhitelist.libs.$pkg")

        reloc("com.alessiodp.libby")
        reloc("io.papermc.lib")
        reloc("com.esotericsoftware")
        reloc("de.leonhard")
        reloc("io.leangen.geantyref")
        reloc("com.zaxxer")
        reloc("org.intellij")
        reloc("org.jetbrains")
        reloc("org.json")
    }
}