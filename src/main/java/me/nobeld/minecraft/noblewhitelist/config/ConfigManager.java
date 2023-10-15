package me.nobeld.minecraft.noblewhitelist.config;

import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
    private final NobleWhitelist plugin;
    private static FileData fileData;
    private static MessageData messageData;
    private static Yaml configFile;
    private static Yaml whitelistFile;
    public ConfigManager(NobleWhitelist plugin) {
        this.plugin = plugin;
        fileData = new FileData(plugin);
        messageData = new MessageData(plugin);
        registerConfig();
        registerWhitelist();
    }
    public void registerConfig() {
        Path filePath = Paths.get(plugin.getDataFolder().getPath() + "/config.yml");
        configFile = SimplixBuilder.fromPath(filePath)
                .addInputStreamFromResource("config.yml")
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setDataType(DataType.SORTED)
                .createYaml()
                .addDefaultsFromInputStream();
        configFile.forceReload();
    }
    public void registerWhitelist() {
        Path filePath = Paths.get(plugin.getDataFolder().getPath() + "/whitelist.yml");
        whitelistFile = SimplixBuilder.fromPath(filePath)
                .addInputStreamFromResource("whitelist.yml")
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setDataType(DataType.UNSORTED)
                .createYaml()
                .addDefaultsFromInputStream();
        whitelistFile.forceReload();
    }
    public void reloadConfig() {
        configFile().forceReload();
    }
    public Yaml configFile() {
        if (configFile == null) {
            registerConfig();
        }
        return configFile;
    }
    public Yaml whitelistFile() {
        if (whitelistFile == null) {
            registerWhitelist();
        }
        return whitelistFile;
    }
    public FileData fileData() {
        return fileData;
    }
    public MessageData messageData() {
        return messageData;
    }
    public static String getPrefix() {
        return configFile.getString("messages.prefix");
    }
    public static boolean usePrefix() {
        return configFile.getBoolean("messages.use-prefix");
    }
}
