package me.nobeld.minecraft.noblewhitelist.config;

import de.leonhard.storage.Yaml;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.model.ConfigContainer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.minecraft.noblewhitelist.config.FileManager.registerYaml;
import static me.nobeld.minecraft.noblewhitelist.config.FileManager.separator;

public class ConfigFile {
    private static Yaml configFile;
    public static void registerConfig() {
        Path filePath = Paths.get(getPlugin().getDataFolder().getPath() + separator() + "config.yml");
        configFile = registerYaml(filePath, "config.yml");
    }
    public static void reloadConfig() {
        configFile().forceReload();
    }
    public static Yaml configFile() {
        if (configFile == null) {
            registerConfig();
        }
        return configFile;
    }
    public static void refreshData() {
        toUpperCase(checkName);
        toUpperCase(checkUUID);
        toUpperCase(checkPerm);

        if (configFile().get("whitelist.max-uuid-list") != null) {
            configFile().remove("whitelist.max-uuid-list");
            configFile().set(storageType.path(), "yaml");
        }
        if (configFile().getSection("incompatibilities").singleLayerKeySet().isEmpty()) return;
        configFile().getSection("incompatibilities").remove("check-invalid-char");
        configFile().getSection("incompatibilities").remove("to-use");
        configFile().getSection("incompatibilities").remove("to-change");
    }
    private static void toUpperCase(ConfigContainer<?> cont) {
        String string = configFile().getString(cont.path());
        string = string.toUpperCase();
        configFile().set(cont.path(), string);
    }
    public static <T> T getConfig(ConfigContainer<T> cont) {
        T val = configFile().get(cont.path(), cont.def());
        if (val instanceof String s) {
            if (s.isBlank() || s.isEmpty()) val = null;
        }
        return val;
    }
    public static <X extends Enum<X>> X getConfigEnum(ConfigContainer<X> container) {
        X result;
        try {
            result = configFile().getEnum(container.path(), container.def().getDeclaringClass());
        } catch (Exception ignored) {
            NobleWhitelist.log(Level.SEVERE, "Cannot load enum from config, using " + container.def().toString() + " as default.");
            result = container.def();
        }
        return result;
    }
    public static <T> void setConfig(ConfigContainer<T> cont, T value) {
        configFile().set(cont.path(), value);
    }
    public static final ConfigContainer<Boolean> whitelistActive = new ConfigContainer<>("whitelist.enabled", false);
    public static final ConfigContainer<Boolean> skipName = new ConfigContainer<>("whitelist.skip-name-change", false);
    public static final ConfigContainer<CheckType> checkName = new ConfigContainer<>("whitelist.checking.name", CheckType.OPTIONAL);
    public static final ConfigContainer<CheckType> checkUUID = new ConfigContainer<>("whitelist.checking.uuid", CheckType.OPTIONAL);
    public static final ConfigContainer<CheckType> checkPerm = new ConfigContainer<>("whitelist.checking.perm", CheckType.DISABLED);
    public static final ConfigContainer<Boolean> countAsOp = new ConfigContainer<>("whitelist.count-op-as-bypass", false);
    public static final ConfigContainer<Boolean> autoRegister = new ConfigContainer<>("whitelist.auto-register", false);
    public static final ConfigContainer<Boolean> enforceNameDiffID = new ConfigContainer<>("whitelist.enforce-uuid-if-different-name", false);
    public static final ConfigContainer<Boolean> notifyUpdate = new ConfigContainer<>("version.notify-update", false);
    public static final ConfigContainer<String> kickMsg = new ConfigContainer<>("messages.not-whitelisted", "<red>You are not whitelisted on this server.");
    public static final ConfigContainer<String> nameChangePlayer = new ConfigContainer<>("messages.name-change-player", "");
    public static final ConfigContainer<String> nameChangeConsole = new ConfigContainer<>("messages.name-change-console", "");
    public static final ConfigContainer<String> storageType = new ConfigContainer<>("storage.storage-type", "");
    public static final ConfigContainer<String> storageHost = new ConfigContainer<>("storage.host", "");
    public static final ConfigContainer<Integer> storagePort = new ConfigContainer<>("storage.port", 3306);
    public static final ConfigContainer<String> storageDBName = new ConfigContainer<>("storage.database", "");
    public static final ConfigContainer<String> storagePassword = new ConfigContainer<>("storage.password", "");
    public static final ConfigContainer<String> storageUser = new ConfigContainer<>("storage.user", "");
    public static final ConfigContainer<Boolean> storageSecurityClose = new ConfigContainer<>("storage.close-if-failed", false);
    public static CheckType checkName() {
        return getConfigEnum(checkName);
    }
    public static CheckType checkUUID() {
        return getConfigEnum(checkUUID);
    }
    public static CheckType checkPerm() {
        return getConfigEnum(checkPerm);
    }
    public enum CheckType {
        REQUIRED("<#F46C4E>required"),
        OPTIONAL("<#75CDFF>optional"),
        DISABLED("<#969FA5>disabled"),
        UNKNOWN("<#C775FF>unknown");
        private final String msg;
        CheckType(String msg) {
            this.msg = msg;
        }
        public String msg() {
            return msg;
        }
        public boolean isRequired() {
            return this == REQUIRED;
        }
        public boolean isOptional() {
            return this == OPTIONAL;
        }
        public boolean isDisabled() {
            return this == DISABLED || this == UNKNOWN;
        }
    }
}
