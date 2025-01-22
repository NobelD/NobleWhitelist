import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta5"
}

group = "me.nobeld.noblewhitelist"
version = "1.2.6"
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
    compileOnly("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")
    implementation("io.papermc", "paperlib", "1.0.7")
    implementation("com.alessiodp.libby", "libby-bukkit", "2.0.0-SNAPSHOT") {
        exclude(module=("spigot-api"))
    }
    compileOnly("com.github.simplix-softworks","simplixstorage","3.2.6")
    compileOnly("com.zaxxer", "HikariCP", "5.1.0")
    compileOnly("org.xerial", "sqlite-jdbc", "3.44.1.0")

    compileOnly("me.clip", "placeholderapi", "2.11.5")
    compileOnly("io.github.miniplaceholders", "miniplaceholders-api", "2.2.3")

    compileOnly("net.kyori","adventure-platform-bukkit","4.3.3")
    compileOnly("net.kyori","adventure-text-minimessage","4.17.0")

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
                    .resolve("libs" + File.separator + shadowFile.name + ".jar")
                result.toFile().mkdirs()
                Files.move(shadowFile.toPath(), result, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }
}