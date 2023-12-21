package me.nobeld.minecraft.noblewhitelist.util;

import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
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
        manager.addRepository("https://repo.papermc.io/repository/maven-public/");

        NobleWhitelist.log(Level.WARNING, "Loading libraries..., you can ignore this warning.");
        loadLibraries();
        NobleWhitelist.log(Level.WARNING, "Libraries loaded..., you can ignore this warning.");
    }
    private Relocation reloc(String path) {
        return new Relocation(path, "me{}nobeld{}minecraft{}noblewhitelist{}libs{}" + path);
    }
    public void loadLibraries() {
        Set<Library> libraries = new HashSet<>();
        libraries.addAll(adventureLibraries());
        libraries.addAll(cloudLibraries());

        libraries.add(Library.builder()
                .groupId("io{}papermc")
                .artifactId("paperlib")
                .version("1.0.7")
                .id("paperlib")
                .relocate(reloc("io{}papermc"))
                .build());

        libraries.add(Library.builder()
                .groupId("com{}github{}simplix-softworks")
                .artifactId("simplixstorage")
                .version("3.2.6")
                .id("simplixstorage")
                .relocate(reloc("com{}esotericsoftware"))
                .relocate(reloc("de{}leonhard"))
                .build());

        libraries.add(Library.builder()
                .groupId("com{}zaxxer")
                .artifactId("HikariCP")
                .version("5.1.0")
                .id("HikariCP")
                .relocate(reloc("com{}zaxxer"))
                .build());

        libraries.forEach(manager::loadLibrary);
    }
    public Set<Library> adventureLibraries() {
        Set<Library> libraries = new HashSet<>();

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-platform-bukkit")
                .version("4.3.1")
                .id("adventure-bukkit")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-platform-api")
                .version("4.3.1")
                .id("adventure-platform-api")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-platform-facet")
                .version("4.3.1")
                .id("adventure-platform-facet")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-gson-legacy-impl")
                .version("4.13.1")
                .id("adventure-text-serializer-gson-legacy-impl")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-bungeecord")
                .version("4.3.1")
                .id("adventure-text-serializer-bungeecord")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-legacy")
                .version("4.13.1")
                .id("adventure-text-serializer-legacy")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-gson")
                .version("4.13.1")
                .id("adventure-text-serializer-gson")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-api")
                .version("4.13.1")
                .id("adventure-api")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-key")
                .version("4.13.1")
                .id("adventure-key")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-nbt")
                .version("4.13.1")
                .id("adventure-nbt")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-api")
                .version("1.3.0")
                .id("examination-api")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-string")
                .version("1.3.0")
                .id("examination-string")
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-minimessage")
                .version("4.14.0")
                .id("adventure-text-minimessage")
                .build());

        return libraries;
    }
    public Set<Library> cloudLibraries() {
        Set<Library> libraries = new HashSet<>();

        libraries.add(Library.builder()
                .groupId("cloud{}commandframework")
                .artifactId("cloud-paper")
                .version("1.8.4")
                .id("cloud-paper")
                .build());

        libraries.add(Library.builder()
                .groupId("cloud{}commandframework")
                .artifactId("cloud-bukkit")
                .version("1.8.4")
                .id("cloud-bukkit")
                .build());

        libraries.add(Library.builder()
                .groupId("cloud{}commandframework")
                .artifactId("cloud-core")
                .version("1.8.4")
                .id("cloud-core")
                .build());

        libraries.add(Library.builder()
                .groupId("cloud{}commandframework")
                .artifactId("cloud-tasks")
                .version("1.8.4")
                .id("cloud-tasks")
                .build());

        libraries.add(Library.builder()
                .groupId("cloud{}commandframework")
                .artifactId("cloud-services")
                .version("1.8.4")
                .id("cloud-services")
                .build());

        libraries.add(Library.builder()
                .groupId("io{}leangen{}geantyref")
                .artifactId("geantyref")
                .version("1.3.13")
                .id("geantyref")
                .build());

        libraries.add(Library.builder()
                .groupId("cloud{}commandframework")
                .artifactId("cloud-brigadier")
                .version("1.8.4")
                .id("cloud-brigadier")
                .build());

        libraries.add(Library.builder()
                .groupId("org{}checkerframework")
                .artifactId("checker-qual")
                .version("3.28.0")
                .id("checker-qual")
                .build());

        libraries.add(Library.builder()
                .groupId("org{}apiguardian")
                .artifactId("apiguardian-api")
                .version("1.1.2")
                .id("apiguardian-api")
                .build());

        libraries.add(Library.builder()
                .groupId("cloud{}commandframework")
                .artifactId("cloud-minecraft-extras")
                .version("1.8.4")
                .id("cloud-minecraft-extras")
                .build());

        return libraries;
    }
}
