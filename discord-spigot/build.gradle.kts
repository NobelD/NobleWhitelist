import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("java")
    id("xyz.jpenilla.resource-factory-bukkit-convention")
    id("xyz.jpenilla.resource-factory-paper-convention")
    alias(libs.plugins.shadow)
}

group = "me.nobeld.noblewhitelist.discord"
version = "2.0.0-SNAPSHOT"
description = "Discord integration for the NobleWhitelist plugin."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "jitpack.io"
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.MinnDevelopment")
        }
    }
    maven {
        name = "essentialsxReleases"
        url = uri("https://repo.essentialsx.net/releases")
    }
    maven {
        name = "discordsrv"
        url = uri("https://nexus.scarsz.me/content/groups/public/")
    }
}

dependencies {
    compileOnly(project(":nwl-core"))
    compileOnly(project(":nwl-spigot")) {
        exclude(module = "nwl-core")
    }
    implementation(project(":discord-core")) {
        isTransitive = false
        //exclude(module = "nwl-core")
        //exclude(group = "org.incendo")
        //exclude(group = "net.kyori")
        //exclude(group = "net.dv8tion")
        //exclude(group = "com.github.MinnDevelopment")
        //exclude(group = "com.google.code.gson")
    }
    compileOnly(libs.paperApi)

    compileOnly("com.alessiodp.libby", "libby-paper", "2.0.0-20240104.190327-5") {
        exclude(module = ("libby-core"))
    }
    compileOnly("com.alessiodp.libby", "libby-bukkit", "2.0.0-20240104.190327-5") {
        exclude(module = ("libby-core"))
    }
    compileOnly(libs.miscLibbyCore) {
        exclude(module = ("spigot-api"))
    }

    compileOnly(libs.miscSimplixStorage)
    compileOnly(libs.cloudJDA)
    compileOnly(libs.cloudRequeriments)

    compileOnly(libs.discordJDA) {
        exclude(module = "opus-java")
    }
    compileOnly(libs.discordEmojiJava)
    compileOnly(libs.discordWebhooks) {
        exclude(module = "okhttp")
    }
    compileOnly(libs.miscLog4j)

    compileOnly("net.essentialsx", "EssentialsXDiscord", "2.20.1")
    compileOnly("net.essentialsx", "EssentialsXDiscordLink", "2.20.1")
    compileOnly("com.discordsrv", "discordsrv", "1.28.0")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        dependencies {
            fun incdep(dependency: String) = include(dependency(dependency))

            incdep("me.nobeld.noblewhitelist.discord:discord-core")
            incdep("com.alessiodp.libby:libby-paper")
            incdep("com.alessiodp.libby:libby-bukkit")
            incdep("com.alessiodp.libby:libby-core")
        }

        archiveBaseName.set("${rootProject.extra.get("dsLowName")}-${rootProject.extra.get("spigotName")}")
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

        relocnwl("com.alessiodp.libby")
        relocnwl("com.esotericsoftware")
        relocnwl("de.leonhard")
        relocnwl("io.leangen.geantyref")
        reloc("org.intellij")
        reloc("org.jetbrains")
        // reloc("org.incendo")
    }
}

bukkitPluginYaml {
    main = "me.nobeld.noblewhitelist.discord.NWLDiscord"
    name = rootProject.extra.get("dsFormatName").toString()
    prefix = name
    version = project.version.toString()
    description = project.description
    apiVersion = rootProject.extra.get("apiVersion").toString()
    author = rootProject.extra.get("projectAuthor").toString()
    website = rootProject.extra.get("projectRepository").toString()
    depend.add(rootProject.extra.get("formatName").toString())
}

paperPluginYaml {
    main = bukkitPluginYaml.main
    name = bukkitPluginYaml.name
    prefix = bukkitPluginYaml.prefix
    description = bukkitPluginYaml.description
    version = bukkitPluginYaml.version
    apiVersion = bukkitPluginYaml.apiVersion
    author = bukkitPluginYaml.author
    website = bukkitPluginYaml.website
    dependencies.server(name= rootProject.extra.get("formatName").toString(), load= PaperPluginYaml.Load.BEFORE, required= true, joinClasspath = true)
}