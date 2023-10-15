package me.nobeld.minecraft.noblewhitelist.config;

import de.leonhard.storage.Yaml;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;

public class FileData {
    private final NobleWhitelist plugin;
    public FileData(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    public Object get(String key) {
        return whitelistFile().get("whitelist." + key);
    }
    public String getString(String key) {
        return whitelistFile().getString("whitelist." + key);
    }
    public void remove(String key) {
        whitelistFile().remove("whitelist." + key);
    }
    public void set(String key, Object value) {
        whitelistFile().set("whitelist." + key, value);
    }
    public void setConfig(String key, Object value) {
        configFile().set("whitelist." + key, value);
    }
    public boolean notifyUpdate() {
        return configFile().getBoolean("version.notify-update");
    }
    public String nameChangeConsole() {
        return configFile().getString("messages.name-change-console");
    }
    public String nameChangePlayer() {
        return configFile().getString("messages.name-change-player");
    }
    public String kickMsg() {
        return configFile().getString("messages.not-whitelisted");
    }
    public boolean whitelistActive() {
        return configFile().getBoolean("whitelist.enabled");
    }
    public boolean skipName() {
        return configFile().getBoolean("whitelist.skip-name-change");
    }
    public int checkName() {
        return checkType(configFile().getString("whitelist.checking.name"));
    }
    public int checkUUID() {
        return checkType(configFile().getString("whitelist.checking.uuid"));
    }
    public int checkPerm() {
        return checkType(configFile().getString("whitelist.checking.perm"));
    }
    private int checkType(String text) {
        if (text.equalsIgnoreCase("disabled")) return 0;
        if (text.equalsIgnoreCase("required")) return 2;
        return 1;
    }
    public String checkNameString() {
        return checkToString(checkName());
    }
    public String checkUUIDString() {
        return checkToString(checkUUID());
    }
    public String checkPermString() {
        return checkToString(checkPerm());
    }
    private String checkToString(int check) {
        if (check == 2) return "<#F46C4E>required";
        if (check == 1) return "<#75CDFF>optional";
        if (check == 0) return "<#969FA5>disabled";
        return "<#C775FF>unknown";
    }
    public boolean autoRegister() {
        return configFile().getBoolean("whitelist.auto-register");
    }
    private Yaml configFile() {
        return plugin.configManager().configFile();
    }
    private Yaml whitelistFile() {
        return plugin.configManager().whitelistFile();
    }
}
