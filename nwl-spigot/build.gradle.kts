import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("java")
    id("xyz.jpenilla.resource-factory-bukkit-convention")
    id("xyz.jpenilla.resource-factory-paper-convention")
    alias(libs.plugins.shadow)
}

group = "me.nobeld.noblewhitelist"
version = "2.0.0-SNAPSHOT"
description = "A simple plugin for whitelist management."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(project(":nwl-core")) {
        isTransitive = false
        //exclude(group = "com.github.simplix-softworks")
        //exclude(group = "com.zaxxer")
        //exclude(group = "org.xerial")
        //exclude(group = "com.google.code.gson")
        //exclude(group = "net.kyori")
        //exclude(group = "org.incendo")
    }
    compileOnly(libs.paperApi)
    implementation(libs.paperLib)

    implementation("com.alessiodp.libby", "libby-paper", "2.0.0-20240104.190327-5") {
        isTransitive = false
        //exclude(module = ("libby-core"))
    }
    implementation("com.alessiodp.libby", "libby-bukkit", "2.0.0-20240104.190327-5") {
        isTransitive = false
        //exclude(module = ("libby-core"))
    }
    compileOnly(libs.miscLibbyCore) {
        isTransitive = false
        //exclude(module = ("spigot-api"))
    }
    compileOnly(libs.miscSimplixStorage)
    compileOnly(libs.dbHikariCP)
    compileOnly(libs.dbSqliteJDBC)

    compileOnly("me.clip", "placeholderapi", "2.11.5")
    compileOnly("io.github.miniplaceholders", "miniplaceholders-api", "2.2.3")

    compileOnly("net.kyori", "adventure-platform-bukkit", "4.3.3")
    //compileOnly("net.kyori", "adventure-text-minimessage", "4.17.0")

    compileOnly("org.incendo", "cloud-paper", "2.0.0-beta.9")
    compileOnly(libs.cloudMCExtras) {
        exclude(module=("adventure-text-minimessage"))
        exclude(module=("adventure-api"))
    }
    compileOnly(libs.cloudConfirmation) {
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

    shadowJar {
        archiveBaseName.set("${rootProject.extra.get("lowName")}-${rootProject.extra.get("spigotName")}")
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


bukkitPluginYaml {
    val permissionNode = "noblewhitelist."

    main = "me.nobeld.noblewhitelist.NobleWhitelist"
    name = rootProject.extra.get("formatName").toString()
    prefix = name
    description = project.description
    version = project.version.toString()
    apiVersion = rootProject.extra.get("apiVersion").toString()
    author = rootProject.extra.get("projectAuthor").toString()
    website = rootProject.extra.get("projectRepository").toString()
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    loadBefore.add("NWLDiscord")

    fun permOf(name: String, description: String? = null, def: Permission.Default) = run {
        val perm = permissions.create(permissionNode + name)
        if (!description.isNullOrBlank()) perm.description = description
        perm.default = def
        perm
    }
    fun addPermOf(name: String, description: String? = null, def: Permission.Default) = run {
        permissions.add(permOf(name, description, def))
    }
    fun addChildren(perm: Permission, vararg nodes: String) = run {
        for (node in nodes) {
            perm.children(permissionNode + node)
        }
        perm
    }

    addPermOf("admin", "Permission to use commands", Permission.Default.OP)
    addPermOf("admin.notify", "Notify if there is an available update", Permission.Default.OP)
    val adminAll = permOf("admin.*", "Adds you all the plugin permissions", Permission.Default.OP)
    addChildren(adminAll, "admin.confirm", "admin.add", "admin.remove", "admin.toggle",
        "admin.list", "admin.list*", "admin.status", "admin.find", "admin.reload", "admin.notify")
    permissions.add(adminAll)
    addPermOf("admin.confirm", "Permission to confirm an important command", Permission.Default.OP)
    addPermOf("admin.add", "Permission to add players to the whitelist", Permission.Default.OP)
    addPermOf("admin.remove", "Permission to remove players from the whitelist", Permission.Default.OP)
    addPermOf("admin.toggle", "Permission to toggle player join status", Permission.Default.OP)
    addPermOf("admin.list", "Permission to see the player list", Permission.Default.OP)
    val listAll = permOf("admin.list.*", "Permission to clear the whitelist", Permission.Default.OP)
    addChildren(listAll, "admin.list.clear")
    permissions.add(listAll)
    addPermOf("admin.list.clear", "Permission to clear the whitelist", Permission.Default.OP)
    addPermOf("admin.status", "Permission to get the plugin status", Permission.Default.OP)
    addPermOf("admin.find", "Permission to find the whitelist data from a player", Permission.Default.OP)
    addPermOf("admin.reload", "Permission to reload the plugin", Permission.Default.OP)
    addPermOf("bypass", "Permits you to pass the whitelist", Permission.Default.OP)
    addPermOf(name= "bypass.1", def= Permission.Default.OP)
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
    dependencies.server(name= rootProject.extra.get("dsFormatName").toString(), load= PaperPluginYaml.Load.AFTER, required= false, joinClasspath = true)
    permissions.addAll(bukkitPluginYaml.permissions)
}