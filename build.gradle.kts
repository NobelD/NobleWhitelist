plugins {
    id("java")
}

group = "me.nobeld.noblewhitelist"
version = "2.0.0-SNAPSHOT"

buildscript {
    extra.set("realName", "NobleWhitelist")
    extra.set("lowName", "noblewhitelist")
    extra.set("realNameDS", "NWLDiscord")
    extra.set("lowNameDS", "nwldiscord")
}

repositories {
    mavenCentral()
}

dependencies {
}

tasks {
}