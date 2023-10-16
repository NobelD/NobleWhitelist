plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.nobeld.minecraft.noblewhitelist"
version = "1.0.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
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
    }
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.20.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.4")
    implementation("io.papermc", "paperlib","1.0.7")
    implementation("com.github.simplix-softworks","simplixstorage","3.2.6")
    implementation("net.kyori","adventure-platform-bukkit","4.3.1")
    implementation("net.kyori","adventure-text-minimessage","4.14.0")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        fun reloc(pkg: String) = relocate(pkg, "me.nobeld.minecraft.noblewhitelist.libs.$pkg")

        reloc("io.papermc.lib")
        reloc("com.github.simplix-softworks")
        reloc("net.kyori.adventure-platform-bukkit")
        reloc("net.kyori.adventure-text-minimessage")
    }
}