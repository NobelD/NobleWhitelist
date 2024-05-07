package me.nobeld.noblewhitelist.discord.util;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.relocation.Relocation;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class LibsManager {
    private final LibraryManager manager;
    public LibsManager(NWLDsData data, final LibraryManager manager, @Nullable List<Library> additional) {
        this.manager = manager;
        manager.setLogLevel(LogLevel.WARN);
        manager.addMavenCentral();

        data.logger().log(Level.INFO, "Loading libraries, this could took a while...");
        long time = System.currentTimeMillis();
        loadLibraries(additional);
        data.logger().log(Level.INFO, "Libraries loaded. (took " + (System.currentTimeMillis() - time) + "ms)");
    }
    private Relocation reloc(String path) {
        return new Relocation(path, "me{}nobeld{}noblewhitelist{}discord{}libs{}" + path);
    }
    //#TODO Fix relocation (unsupported class file major version by the relocator)
    public void loadLibraries(@Nullable List<Library> additional) {
        Set<Library> libraries = new HashSet<>();

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-jda5")
                .version("1.0.0-beta.2")
                .excludeTransitiveDependency("net{}dv8tion", "JDA")
                .resolveTransitiveDependencies(true)
                .build());
                /*
                .relocate(reloc("org{}incendo"))
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
                .excludeTransitiveDependency("org{}incendo", "cloud-core")
                .build());

        libraries.add(Library.builder()
                .groupId("org{}incendo")
                .artifactId("cloud-processors-common")
                .version("1.0.0-beta.2")
                .excludeTransitiveDependency("org{}incendo", "cloud-core")
                .build());

        libraries.add(Library.builder()
                .groupId("net.dv8tion")
                .artifactId("JDA")
                .version("5.0.0-beta.20")
                .excludeTransitiveDependency("org{}jetbrains{}kotlin", "kotlin-stdlib")
                .excludeTransitiveDependency("org{}jetbrains{}kotlin", "kotlin-stdlib-common")
                .excludeTransitiveDependency("org{}jetbrains{}kotlin", "kotlin-stdlib-jdk7")
                .excludeTransitiveDependency("org{}jetbrains{}kotlin", "kotlin-stdlib-jdk8")
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
                .groupId("com{}github{}minndevelopment")
                .artifactId("emoji-java")
                .version("v6.1.0")
                .repository("https://jitpack.io")
                .excludeTransitiveDependency("org{}jetbrains{}kotlin", "kotlin-stdlib")
                .excludeTransitiveDependency("org{}jetbrains{}kotlin", "kotlin-stdlib-common")
                .excludeTransitiveDependency("org{}jetbrains{}kotlin", "kotlin-stdlib-jdk7")
                .excludeTransitiveDependency("org{}jetbrains{}kotlin", "kotlin-stdlib-jdk8")
                .resolveTransitiveDependencies(true)
                .build());
                //.relocate(reloc("com{}vdurmont{}emoji"))

        libraries.add(Library.builder()
                .groupId("club{}minnced")
                .artifactId("discord-webhooks")
                .version("0.8.4")
                .build());
                //.relocate(reloc("club{}minnced{}discord{}webhook"))

        libraries.add(Library.builder()
                .groupId("org{}slf4j")
                .artifactId("slf4j-api")
                .version("1.7.36")
                .build());

        if (additional != null) libraries.addAll(additional);

        libraries.forEach(manager::loadLibrary);
    }
}
