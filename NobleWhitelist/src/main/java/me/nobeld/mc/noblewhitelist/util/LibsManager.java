package me.nobeld.mc.noblewhitelist.util;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.relocation.Relocation;
import me.nobeld.mc.noblewhitelist.NobleWhitelist;

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
        manager.addRepository("https://jitpack.io");

        NobleWhitelist.log(Level.INFO, "Loading libraries....");
        loadLibraries(additional);
        NobleWhitelist.log(Level.INFO, "Libraries loaded.");
    }
    private Relocation reloc(String path) {
        return new Relocation(path, "me{}nobeld{}mc{}noblewhitelist{}libs{}" + path);
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
                .resolveTransitiveDependencies(true)
                .build());

        //PaperLib Library
        libraries.add(Library.builder()
                .groupId("io{}papermc")
                .artifactId("paperlib")
                .version("1.0.7")
                .relocate(reloc("io{}papermc"))
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
                .version("2.0.0-beta.3")
                .resolveTransitiveDependencies(true)
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-minecraft-extras")
                .version("2.0.0-beta.3")
                .resolveTransitiveDependencies(true)
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-processors-confirmation")
                .version("1.0.0-beta.2")
                .resolveTransitiveDependencies(true)
                .build());

        if (additional != null && !additional.isEmpty()) libraries.addAll(additional);

        libraries.forEach(manager::loadLibrary);
    }
}
