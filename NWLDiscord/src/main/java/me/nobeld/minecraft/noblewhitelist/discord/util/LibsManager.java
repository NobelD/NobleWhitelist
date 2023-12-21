package me.nobeld.minecraft.noblewhitelist.discord.util;

import me.nobeld.minecraft.noblewhitelist.discord.NWLDiscord;
import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import net.byteflux.libby.relocation.Relocation;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class LibsManager {
    // Adding a lot of libraries until the libby api supports transitive dependencies.
    private final LibraryManager manager;
    public LibsManager(final LibraryManager manager) {
        this.manager = manager;
        manager.addMavenCentral();
        manager.addRepository("https://jitpack.io");

        NWLDiscord.log(Level.WARNING, "Loading libraries..., you can ignore this warning.");
        loadLibraries();
        NWLDiscord.log(Level.WARNING, "Libraries loaded..., you can ignore this warning.");
    }
    private Relocation reloc(String path) {
        return new Relocation(path, "me{}nobeld{}minecraft{}noblewhitelist{}discord{}libs{}" + path);
    }
    public void loadLibraries() {

        Set<Library> libraries = new HashSet<>(jdaLibraries());

        libraries.add(Library.builder()
                .groupId("com{}github{}simplix-softworks")
                .artifactId("simplixstorage")
                .version("3.2.6")
                .id("simplixstorage")
                .relocate(reloc("com{}esotericsoftware"))
                .relocate(reloc("de{}leonhard"))
                .build());

        libraries.forEach(manager::loadLibrary);
    }
    public Set<Library> jdaLibraries() {
        Set<Library> libraries = new HashSet<>();

        libraries.add(Library.builder()
                .groupId("net.dv8tion")
                .artifactId("JDA")
                .version("5.0.0-beta.18")
                .id("JDA")
                .relocate(reloc("net{}dv8tion{}jda"))
                .relocate(reloc("com{}iwebpp{}crypto"))
                .relocate(reloc("gnu{}trove"))
                .relocate(reloc("com{}neovisionaries{}ws"))
                .relocate(reloc("com{}fasterxml{}jackson{}databind"))
                .relocate(reloc("com{}fasterxml{}jackson{}core"))
                .relocate(reloc("com{}fasterxml{}jackson{}annotations"))
                .relocate(reloc("com{}vdurmont{}emoji"))
                .relocate(reloc("org{}json"))
                .build());

        libraries.add(Library.builder()
                .groupId("com{}neovisionaries")
                .artifactId("nv-websocket-client")
                .version("2.14")
                .id("nv-websocket-client")
                .relocate(reloc("com{}neovisionaries{}ws"))
                .build());

        libraries.add(Library.builder()
                .groupId("net{}sf{}trove4j")
                .artifactId("trove4j")
                .version("3.0.3")
                .id("trove4j")
                .relocate(reloc("gnu{}trove"))
                .build());

        libraries.add(Library.builder()
                .groupId("com{}squareup{}okhttp3")
                .artifactId("okhttp")
                .version("4.10.0")
                .id("okhttp")
                .build());

        libraries.add(Library.builder()
                .groupId("com{}squareup{}okio")
                .artifactId("okio")
                .version("3.0.0")
                .id("okio")
                .build());

        libraries.add(Library.builder()
                .groupId("com{}squareup{}okio")
                .artifactId("okio-jvm")
                .version("3.0.0")
                .id("okio-jvm")
                .build());

        libraries.add(Library.builder()
                .groupId("org{}apache{}commons")
                .artifactId("commons-collections4")
                .version("4.4")
                .id("commons-collections4")
                .build());

        libraries.add(Library.builder()
                .groupId("com{}fasterxml{}jackson{}core")
                .artifactId("jackson-databind")
                .version("2.14.1")
                .id("jackson-databind")
                .relocate(reloc("com{}fasterxml{}jackson{}databind"))
                .relocate(reloc("com{}fasterxml{}jackson{}core"))
                .build());

        libraries.add(Library.builder()
                .groupId("com{}fasterxml{}jackson{}core")
                .artifactId("jackson-core")
                .version("2.14.1")
                .id("jackson-core")
                .relocate(reloc("com{}fasterxml{}jackson{}core"))
                .build());

        libraries.add(Library.builder()
                .groupId("com{}fasterxml{}jackson{}core")
                .artifactId("jackson-annotations")
                .version("2.14.1")
                .id("jackson-annotations")
                .relocate(reloc("com{}fasterxml{}jackson{}annotations"))
                .relocate(reloc("com{}fasterxml{}jackson{}core"))
                .build());

        libraries.add(Library.builder()
                .groupId("org{}slf4j")
                .artifactId("slf4j-api")
                .version("1.7.36")
                .id("slf4j-api")
                .build());

        libraries.add(Library.builder()
                .groupId("org{}jetbrains{}kotlin")
                .artifactId("kotlin-stdlib-jdk8")
                .version("1.5.31")
                .id("kotlin-stdlib-jdk8")
                .build());

        libraries.add(Library.builder()
                .groupId("org{}jetbrains{}kotlin")
                .artifactId("kotlin-stdlib")
                .version("1.6.20")
                .id("kotlin-stdlib")
                .build());

        libraries.add(Library.builder()
                .groupId("org{}jetbrains{}kotlin")
                .artifactId("kotlin-stdlib-common")
                .version("1.6.20")
                .id("kotlin-stdlib-common")
                .build());

        libraries.add(Library.builder()
                .groupId("com{}github{}MinnDevelopment")
                .artifactId("emoji-java")
                .version("v6.1.0")
                .id("emoji-java")
                .relocate(reloc("com{}vdurmont{}emoji"))
                .build());

        libraries.add(Library.builder()
                .groupId("org{}json")
                .artifactId("json")
                .version("20230227")
                .id("json")
                .relocate(reloc("org{}json"))
                .build());

        libraries.add(Library.builder()
                .groupId("club{}minnced")
                .artifactId("discord-webhooks")
                .version("0.8.2")
                .id("discord-webhooks")
                .relocate(reloc("club{}minnced{}discord{}webhook"))
                .build());

        return libraries;
    }
}
