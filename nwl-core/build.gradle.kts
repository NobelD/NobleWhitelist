plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.nobeld.noblewhitelist"
version = "2.0.0-SNAPSHOT"
description = "The core logic for the whitelist."

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
    implementation("com.github.simplix-softworks", "simplixstorage", "3.2.6")
    implementation("com.zaxxer", "HikariCP", "5.1.0")
    implementation("org.xerial", "sqlite-jdbc", "3.44.1.0")
    implementation("com.google.code.gson", "gson", "2.10.1")

    compileOnly("me.clip", "placeholderapi", "2.11.5")
    compileOnly("io.github.miniplaceholders", "miniplaceholders-api", "2.2.3")

    implementation("net.kyori", "adventure-text-minimessage", "4.15.0")

    implementation("org.incendo", "cloud-minecraft-extras", "2.0.0-beta.4")
    implementation("org.incendo", "cloud-processors-confirmation", "1.0.0-beta.2")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        archiveClassifier.set("")
        fun reloc(pkg: String) = relocate(pkg, "me.nobeld.noblewhitelist.libs.$pkg")

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