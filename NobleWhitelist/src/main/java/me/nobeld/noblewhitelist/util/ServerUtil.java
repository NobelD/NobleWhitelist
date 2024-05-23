package me.nobeld.noblewhitelist.util;

import io.papermc.lib.PaperLib;
import me.nobeld.noblewhitelist.NobleWhitelist;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ServerUtil {
    private static final boolean craftBukkit = hasClass("org.bukkit.Bukkit");
    private static final boolean spigot = PaperLib.isSpigot();
    private static final boolean paper = PaperLib.isPaper();
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
    public static String toS(boolean b) {
        return b ? "yes" : "no";
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
    public static String buildString(Object... string) {
        StringBuilder builder = new StringBuilder();
        for (Object s : string) {
            if (s == null || (s instanceof String && s.equals("null"))) continue;
            builder.append(s);
        }
        return builder.toString();
    }
}
