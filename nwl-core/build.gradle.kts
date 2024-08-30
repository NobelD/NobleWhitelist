plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "me.nobeld.noblewhitelist"
version = "2.0.0-SNAPSHOT"
description = "The core logic for the whitelist."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(libs.miscSimplixStorage)
    implementation(libs.dbHikariCP)
    implementation(libs.dbSqliteJDBC)
    implementation(libs.miscGson)

    //compileOnly("me.clip", "placeholderapi", "2.11.5")
    compileOnly("io.github.miniplaceholders", "miniplaceholders-api", "2.2.3")

    implementation(libs.adventureMiniMessage)

    implementation(libs.cloudMCExtras)
    implementation(libs.cloudConfirmation)
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