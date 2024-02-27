package me.nobeld.mc.noblewhitelist.discord.util;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.relocation.Relocation;
import me.nobeld.mc.noblewhitelist.discord.NWLDiscord;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class LibsManager {
    private final LibraryManager manager;
    public LibsManager(final LibraryManager manager, @Nullable List<Library> additional) {
        this.manager = manager;
        manager.setLogLevel(LogLevel.WARN);
        manager.addMavenCentral();
        manager.addRepository("https://jitpack.io");

        NWLDiscord.log(Level.INFO, "Loading libraries, this could took a while...");
        loadLibraries(additional);
        NWLDiscord.log(Level.INFO, "Libraries loaded.");
    }
    private Relocation reloc(String path) {
        return new Relocation(path, "me{}nobeld{}mc{}noblewhitelist{}discord{}libs{}" + path);
    }
    //#TODO Fix relocation
    public void loadLibraries(@Nullable List<Library> additional) {
        Set<Library> libraries = new HashSet<>();

        libraries.add(Library.builder()
                .groupId("com{}github{}simplix-softworks")
                .artifactId("simplixstorage")
                .version("3.2.6")
                .relocate(reloc("com{}esotericsoftware"))
                .relocate(reloc("de{}leonhard"))
                .resolveTransitiveDependencies(true)
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-jda5")
                .version("1.0.0-beta.2")
                .resolveTransitiveDependencies(true)
                .build());
                /*
                .relocate(reloc("net{}dv8tion{}jda"))
                .relocate(reloc("com{}iwebpp{}crypto"))
                .relocate(reloc("gnu{}trove"))
                .relocate(reloc("com{}neovisionaries{}ws"))
                .relocate(reloc("com{}fasterxml{}jackson{}databind"))
                .relocate(reloc("com{}fasterxml{}jackson{}core"))
                .relocate(reloc("com{}fasterxml{}jackson{}annotations"))
                .relocate(reloc("com{}vdurmont{}emoji"))
                .relocate(reloc("org{}json"))
                .build());*/

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-processors-requirements")
                .version("1.0.0-beta.2")
                .resolveTransitiveDependencies(true)
                .build());

        libraries.add(Library.builder()
                .groupId("net.dv8tion")
                .artifactId("JDA")
                .version("5.0.0-beta.18")
                .excludeTransitiveDependency("club{}minnced", "opus-java")
                .resolveTransitiveDependencies(true)
                .build());
                /*
                .relocate(reloc("net{}dv8tion{}jda"))
                .relocate(reloc("com{}iwebpp{}crypto"))
                .relocate(reloc("gnu{}trove"))
                .relocate(reloc("com{}neovisionaries{}ws"))
                .relocate(reloc("com{}fasterxml{}jackson{}databind"))
                .relocate(reloc("com{}fasterxml{}jackson{}core"))
                .relocate(reloc("com{}fasterxml{}jackson{}annotations"))
                .relocate(reloc("com{}vdurmont{}emoji"))
                .relocate(reloc("org{}json"))
                .build());*/

        libraries.add(Library.builder()
                .groupId("com{}github{}MinnDevelopment")
                .artifactId("emoji-java")
                .version("v6.1.0")
                .resolveTransitiveDependencies(true)
                .build());
                //.relocate(reloc("com{}vdurmont{}emoji"))

        libraries.add(Library.builder()
                .groupId("club{}minnced")
                .artifactId("discord-webhooks")
                .version("0.8.2")
                .resolveTransitiveDependencies(true)
                .build());
                //.relocate(reloc("club{}minnced{}discord{}webhook"))

        if (additional != null) libraries.addAll(additional);

        libraries.forEach(manager::loadLibrary);
    }
}
