package me.nobeld.noblewhitelist.util;

import me.nobeld.noblewhitelist.NobleWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Version parser format

1.21.11
> major   -> 21
> version -> 11
> patch   -> none

26.1.2
> major   -> 26
> version -> 1
> patch   -> 2
 */
public class ServerUtil {
    static {
        Pattern versionPattern = Pattern.compile("(?i)\\(MC: (\\d+)\\.?(\\d+)\\.?(\\d+)?(?: Pre-Release )?(\\d)?\\)");
        Matcher matcher = versionPattern.matcher(Bukkit.getVersion());
        int major = 0;
        int version = 0;
        int patch = 0;
        int preRelease = 0;
        if (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            try {
                major = Integer.parseInt(matchResult.group(1), 10);
            } catch (Exception ignored) {
            }
            try {
                version = Integer.parseInt(matchResult.group(2), 10);
            } catch (Exception ignored) {
            }
            if (matchResult.groupCount() >= 3) {
                try {
                    patch = Integer.parseInt(matchResult.group(3), 10);
                } catch (Exception ignored) {
                }
            }
            if (matchResult.groupCount() >= 4) {
                try {
                    preRelease = Integer.parseInt(matcher.group(4));
                } catch (Exception ignored) {
                }
            }
        }
        if (major == 1) {
            minecraftVersionMajor = version;
            minecraftVersion = patch;
            minecraftVersionPatch = 0;
        } else {
            minecraftVersionMajor = major;
            minecraftVersion = version;
            minecraftVersionPatch = patch;
        }
        minecraftPreReleaseVersion = preRelease;
        StringBuilder builder = new StringBuilder();
        builder.append(ServerUtil.minecraftVersionMajor).append('.').append(ServerUtil.minecraftVersion);
        if (ServerUtil.minecraftVersionPatch > 0) builder.append('.').append(ServerUtil.minecraftVersionPatch);
        minecraftVersionString = builder.toString();
    }
    private static final int minecraftVersionMajor;
    private static final int minecraftVersion;
    private static final int minecraftVersionPatch;
    private static final int minecraftPreReleaseVersion;
    private static final String minecraftVersionString;
    private static final boolean craftBukkit = hasClass("org.bukkit.Bukkit");
    private static final boolean spigot = hasClass("org.spigotmc.SpigotConfig");
    private static final boolean paper = hasClass("com.destroystokyo.paper.PaperConfig");
    private static final boolean folia = hasClass("io.papermc.paper.threadedregions.RegionizedServer");
    private static final boolean adventure = paper || hasClass("net.kyori.adventure.Adventure");
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
    public static boolean hasAdventure() {
        return adventure;
    }
    public static String getVersionString() {
        return minecraftVersionString;
    }
    public static int getMajorVersion() {
        return minecraftVersionMajor;
    }
    public static int getVersion() {
        return minecraftVersion;
    }
    public static int getPatchVersion() {
        return minecraftVersionPatch;
    }
    public static int getPreReleaseVersion() {
        return minecraftPreReleaseVersion;
    }
    public static boolean matchVersion(int major) {
        return minecraftVersionMajor >= major;
    }
    public static boolean matchVersion(int major, int version) {
        return minecraftVersionMajor > major || (minecraftVersionMajor == major && minecraftVersion >= version);
    }
    public static boolean matchVersion(int major, int version, int patch) {
        return minecraftVersionMajor > major || (minecraftVersionMajor == major && (minecraftVersion > version || (minecraftVersion == version && minecraftVersionPatch >= patch)));
    }
    public static boolean allowsPaperPlugin() {
        return paper && ServerUtil.matchVersion(19, 3);
    }
    public static boolean isPaperPlugin(JavaPlugin plugin) {
        if (!allowsPaperPlugin()) {
            return false;
        }
        ClassLoader cl = plugin.getClass().getClassLoader();
        Class<?> paperClClazz;
        try {
            paperClClazz = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader");
        } catch (ClassNotFoundException e) {
            return false;
        }

        return paperClClazz.isAssignableFrom(cl.getClass());
    }
    public static void incompatibleWarning(JavaPlugin plugin, String string, String ver) {
        plugin.getLogger().log(Level.SEVERE, "You are running the server on " + string + ", this version is not compatible and the plugin will be disabled, consider updating to minimum " + ver + ".");
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
        if (!matchVersion(18)) {
           incompatibleWarning(plugin, getVersionString(), "1.18.x");
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
