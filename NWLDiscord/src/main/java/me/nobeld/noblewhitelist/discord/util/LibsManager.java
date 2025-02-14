package me.nobeld.noblewhitelist.discord.util;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.relocation.Relocation;
import me.nobeld.noblewhitelist.discord.NWLDiscord;

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

        NWLDiscord.log(Level.INFO, "Loading libraries, this could took a while...");
        long time = System.currentTimeMillis();
        loadLibraries(additional);
        NWLDiscord.log(Level.INFO, "Libraries loaded. (took " + (System.currentTimeMillis() - time) + "ms)");
    }
    private Relocation reloc(String path) {
        return new Relocation(path, "me{}nobeld{}noblewhitelist{}discord{}libs{}" + path);
    }
    private Relocation nwlreloc(String path) {
        return new Relocation(path, "me{}nobeld{}noblewhitelist{}libs{}" + path);
    }
    public void loadLibraries(@Nullable List<Library> additional) {
        Set<Library> libraries = new HashSet<>();

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-jda5")
                .version("1.0.0-beta.3")
                .relocate(nwlreloc("org{}incendo"))
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
                .groupId("org{}incendo")
                .artifactId("cloud-discord-common")
                .version("1.0.0-beta.3")
                .relocate(nwlreloc("org{}incendo"))
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-processors-requirements")
                .version("1.0.0-rc.1")
                .relocate(nwlreloc("org{}incendo"))
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-processors-common")
                .version("1.0.0-rc.1")
                .relocate(nwlreloc("org{}incendo"))
                .build());

        libraries.add(Library.builder()
                .groupId("net.dv8tion")
                .artifactId("JDA")
                .version("5.3.0")
                .resolveTransitiveDependencies(true)
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
                .groupId("com{}github{}minndevelopment")
                .artifactId("emoji-java")
                .version("v6.1.0")
                .repository("https://jitpack.io")
                .relocate(reloc("com{}vdurmont{}emoji"))
                .build());

        libraries.add(Library.builder()
                .groupId("club{}minnced")
                .artifactId("discord-webhooks")
                .version("0.8.4")
                .relocate(reloc("club{}minnced{}discord{}webhook"))
                .build());

        libraries.add(Library.builder()
                .groupId("org{}slf4j")
                .artifactId("slf4j-api")
                .version("1.7.36")
                .build());

        if (additional != null) libraries.addAll(additional);

        libraries.forEach(manager::loadLibrary);
    }
}
