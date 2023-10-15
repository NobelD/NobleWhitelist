package me.nobeld.minecraft.noblewhitelist.util;

import io.papermc.lib.PaperLib;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static me.nobeld.minecraft.noblewhitelist.config.ConfigManager.usePrefix;

public class ServerUtil {
    private static final boolean craftBukkit = hasClass("org.bukkit.Bukkit");
    private static final boolean spigot = hasClass("org.spigotmc.SpigotConfig");
    private static final boolean paper = PaperLib.isPaper();
    private static final boolean folia = hasClass("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
    public static boolean hasFolia() {
        return folia;
    }
    public static boolean hasPaper() {
        return paper;
    }
    public static boolean hasSpigot() {
        return spigot;
    }
    public static boolean hasBukkit() {
        return craftBukkit;
    }
    public static boolean craftBukkitWarning() {
        if (hasBukkit() && !hasSpigot()) {
            incompatibleApi("craftBukkit");
            return true;
        }
        return false;
    }
    public static void incompatibleApi(String string) {
        Bukkit.getLogger().log(Level.SEVERE, "You are running the server on " + string + ", this api is not compatible and the plugin will be disabled, consider using PaperMC.");
    }
    public static void incompatibleVer(String string, String ver) {
        Bukkit.getLogger().log(Level.SEVERE, "You are running the server on " + string + ", this version is not compatible and the plugin will be disabled, consider updating to minimum " + ver + ".");
    }
    public static boolean hasClass(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    public static boolean hasClass(String... classNames) {
        for (String className : classNames) {
            if (hasClass(className)) {
                return true;
            }
        }
        return false;
    }
    public static boolean canRun(NobleWhitelist plugin) {
        if (craftBukkitWarning()) {
           Bukkit.getPluginManager().disablePlugin(plugin);
           return false;
        }
        if (PaperLib.getMinecraftVersion() < 16) {
           incompatibleVer(Bukkit.getVersion(), "1.16.x");
           Bukkit.getPluginManager().disablePlugin(plugin);
           return false;
        }
        return true;
    }
    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByKey());

        Map<K, V> sorted = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }
    public static Component convertMsg(String msg, String name) {
        MiniMessage minimessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(prefix())
                        .resolver(name != null ? playerName(name) : playerNoName())
                        .build()
                )
                .build();
        return minimessage.deserialize(msg);
    }
    public static Component convertMsg(String msg) {
        MiniMessage minimessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(prefix())
                        .build()
                )
                .build();
        return minimessage.deserialize(msg);
    }
    private static Component base(String msg) {
        MiniMessage minimessage = MiniMessage.builder().tags(TagResolver.standard()).build();
        return minimessage.deserialize(msg);
    }
    private static @NotNull TagResolver prefix() {
        final Component component = base(usePrefix() ? (ConfigManager.getPrefix() + "<reset> ") : "");
        return TagResolver.resolver("prefix", Tag.selfClosingInserting(component));
    }
    private static @NotNull TagResolver playerName(String name) {
        final Component component = Component.text(name);
        return TagResolver.resolver("name", Tag.selfClosingInserting(component));
    }
    private static @NotNull TagResolver playerNoName() {
        final Component component = Component.text("");
        return TagResolver.resolver("name", Tag.selfClosingInserting(component));
    }
}
