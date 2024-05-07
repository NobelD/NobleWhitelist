plugins {
    id("java")
}

group = "me.nobeld.noblewhitelist"
version = "2.0.0"
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
    implementation("com.alessiodp.libby", "libby-core", "2.0.0-20240104.190327-5") {
        exclude(module=("spigot-api"))
    }
    compileOnly("com.github.simplix-softworks","simplixstorage","3.2.6")
    compileOnly("com.zaxxer", "HikariCP", "5.1.0")
    compileOnly("org.xerial", "sqlite-jdbc", "3.44.1.0")
    implementation("com.google.code.gson", "gson", "2.10.1")

    compileOnly("me.clip", "placeholderapi", "2.11.5")
    compileOnly("io.github.miniplaceholders", "miniplaceholders-api", "2.2.3")

    compileOnly("net.kyori","adventure-platform-bukkit","4.3.2")
    compileOnly("net.kyori","adventure-text-minimessage","4.15.0")

    compileOnly("org.incendo", "cloud-minecraft-extras", "2.0.0-beta.4")
    compileOnly("org.incendo", "cloud-processors-confirmation", "1.0.0-beta.2")
}

tasks.test {
    useJUnitPlatform()
}