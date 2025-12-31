package me.nobeld.noblewhitelist.util;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.relocation.Relocation;
import me.nobeld.noblewhitelist.NobleWhitelist;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class LibsManager {
    private final LibraryManager manager;
    public LibsManager(LibraryManager manager, @Nullable List<Library> additional) {
        this.manager = manager;
        manager.setLogLevel(LogLevel.WARN);
        manager.addMavenCentral();
        manager.addRepository("https://repo.papermc.io/repository/maven-public/");

        NobleWhitelist.log(Level.INFO, "Loading libraries, this might take a little while...");
        long time = System.currentTimeMillis();
        loadLibraries(additional);
        NobleWhitelist.log(Level.INFO, "Libraries loaded. (took " + (System.currentTimeMillis() - time) + "ms)");
    }
    private Relocation reloc(String path) {
        return new Relocation(path, "me{}nobeld{}noblewhitelist{}libs{}" + path);
    }
    public void loadLibraries(@Nullable List<Library> additional) {
        Set<Library> libraries = new HashSet<>();
        //Adventure libraries
        if (!ServerUtil.hasAdventure()) {
            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-platform-bukkit")
                                  .version("4.4.1")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-platform-api")
                                  .version("4.4.1")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-text-serializer-bungeecord")
                                  .version("4.4.1")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-text-serializer-legacy")
                                  .version("4.24.0")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-nbt")
                                  .version("4.24.0")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-text-serializer-gson")
                                  .version("4.24.0")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-text-serializer-json")
                                  .version("4.24.0")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-text-serializer-gson-legacy-impl")
                                  .version("4.24.0")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-text-serializer-json-legacy-impl")
                                  .version("4.24.0")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-platform-facet")
                                  .version("4.4.1")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-platform-viaversion")
                                  .version("4.4.1")
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("adventure-text-minimessage")
                                  .version("4.24.0")
                                  .resolveTransitiveDependencies(true)
                                  .build());

            libraries.add(Library.builder()
                                  .groupId("net{}kyori")
                                  .artifactId("option")
                                  .version("1.1.0")
                                  .build());
        }

        //Storage Library
        libraries.add(Library.builder()
                .groupId("com{}github{}nobeld")
                .artifactId("simplixstorage")
                .version("3.2.9-rc.5")
                .relocate(reloc("com{}esotericsoftware"))
                .relocate(reloc("de{}leonhard"))
                .repository("https://jitpack.io")
                .resolveTransitiveDependencies(true)
                .excludeTransitiveDependency("org{}jetbrains", "annotations")
                .build());

        //Database library
        libraries.add(Library.builder()
                .groupId("com{}zaxxer")
                .artifactId("HikariCP")
                .version("7.0.2")
                .relocate(reloc("com{}zaxxer"))
                .excludeTransitiveDependency("org{}slf4j", "slf4j-api")
                .build());

        libraries.add(Library.builder()
                .groupId("com{}github{}nobeld{}slf4j-ov")
                .artifactId("slf4j-jdk14")
                .version("ffbbfcf8e2")
                .relocate(reloc("org{}slf4j"))
                .repository("https://jitpack.io")
                .resolveTransitiveDependencies(true)
                .build());

        //Command libraries
        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-paper")
                .version("2.0.0-beta.14")
                .relocate(reloc("org{}incendo"))
                .resolveTransitiveDependencies(true)
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-core")
                .version("2.0.0")
                .relocate(reloc("org{}incendo"))
                .resolveTransitiveDependencies(true)
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-minecraft-extras")
                .version("2.0.0-beta.14")
                .relocate(reloc("org{}incendo"))
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-processors-confirmation")
                .version("1.0.0-rc.1")
                .relocate(reloc("org{}incendo"))
                .excludeTransitiveDependency("org{}incendo", "cloud-core")
                .resolveTransitiveDependencies(true)
                .build());

        if (additional != null && !additional.isEmpty()) libraries.addAll(additional);

        libraries.forEach(manager::loadLibrary);
    }

    public void loadExtra(List<Library> extra) {
        extra.forEach(manager::loadLibrary);
    }
}
