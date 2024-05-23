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

        NobleWhitelist.log(Level.INFO, "Loading libraries, this could took a while...");
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
        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-platform-bukkit")
                .version("4.3.2")
                .resolveTransitiveDependencies(true)
                .build());

        libraries.add(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-minimessage")
                .version("4.15.0")
                .resolveTransitiveDependencies(true)
                .build());

        //Storage Library
        libraries.add(Library.builder()
                .groupId("com{}github{}simplix-softworks")
                .artifactId("simplixstorage")
                .version("3.2.6")
                .relocate(reloc("com{}esotericsoftware"))
                .relocate(reloc("de{}leonhard"))
                .repository("https://jitpack.io")
                .resolveTransitiveDependencies(true)
                .build());

        //Database library
        libraries.add(Library.builder()
                .groupId("com{}zaxxer")
                .artifactId("HikariCP")
                .version("5.1.0")
                .relocate(reloc("com{}zaxxer"))
                .build());

        //Command libraries
        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-paper")
                .version("2.0.0-beta.4")
                .relocate(reloc("org{}incendo"))
                .resolveTransitiveDependencies(true)
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-minecraft-extras")
                .version("2.0.0-beta.4")
                .relocate(reloc("org{}incendo"))
                .excludeTransitiveDependency("org{}incendo", "cloud-annotations")
                .excludeTransitiveDependency("org{}incendo", "cloud-core")
                .resolveTransitiveDependencies(true)
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-processors-confirmation")
                .version("1.0.0-beta.2")
                .relocate(reloc("org{}incendo"))
                .excludeTransitiveDependency("org{}incendo", "cloud-core")
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-processors-common")
                .version("1.0.0-beta.2")
                .relocate(reloc("org{}incendo"))
                .excludeTransitiveDependency("org{}incendo", "cloud-core")
                .build());

        if (additional != null && !additional.isEmpty()) libraries.addAll(additional);

        libraries.forEach(manager::loadLibrary);
    }
}
