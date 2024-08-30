plugins {
    id("java")
    alias(libs.plugins.resourceFactory) apply false
}

group = "me.nobeld.noblewhitelist"
version = "2.0.0-SNAPSHOT"

buildscript {
    extra.set("projectAuthor", "NobelD")
    extra.set("projectRepository", "https://github.com/NobelD/NobleWhitelist")

    extra.set("spigotName", "spigot")
    extra.set("apiVersion", "1.17")

    extra.set("formatName", "NobleWhitelist")
    extra.set("dsFormatName", "NWLDiscord")

    extra.set("lowName", "noblewhitelist")
    extra.set("dsLowName", "nwldiscord")
}

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
        maven {
            name = "jitpack.io"
            url = uri("https://jitpack.io")
            content {
                includeGroup("me.nobeld")
            }
        }
    }
}

dependencies {
}

tasks {
}