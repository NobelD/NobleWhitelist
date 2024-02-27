package me.nobeld.mc.noblewhitelist.config;

import de.leonhard.storage.Yaml;
import me.nobeld.mc.noblewhitelist.NobleWhitelist;
import me.nobeld.mc.noblewhitelist.model.storage.ConfigContainer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class ConfigData {
    private Yaml configFile;
    public void registerConfig() {
        Path filePath = Paths.get(NobleWhitelist.getPlugin().getDataFolder().getPath() + FileManager.separator() + "config.yml");
        configFile = FileManager.registerYaml(filePath, "config.yml");
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
    public void refreshData() {
        toUpperCase(WhitelistCF.checkName);
        toUpperCase(WhitelistCF.checkUUID);
        toUpperCase(WhitelistCF.checkPerm);

        if (get(ServerCF.configVersion) <= 2) {
            configFile().remove("whitelist.count-op-as-bypass");
            set(ServerCF.configVersion, 3);
        }

        if (configFile().get("whitelist.max-uuid-list") != null) {
            configFile().remove("whitelist.max-uuid-list");
            configFile().set(StorageCF.storageType.path(), "yaml");
        }
        if (configFile().getSection("incompatibilities").singleLayerKeySet().isEmpty()) return;
        configFile().getSection("incompatibilities").remove("check-invalid-char");
        configFile().getSection("incompatibilities").remove("to-use");
        configFile().getSection("incompatibilities").remove("to-change");
    }
    public String getPrefix() {
        return configFile().getString("messages.prefix");
    }
    public boolean usePrefix() {
        return configFile().getBoolean("messages.use-prefix");
    }
    private void toUpperCase(ConfigContainer<?> cont) {
        String string = configFile().getString(cont.path());
        string = string.toUpperCase();
        configFile().set(cont.path(), string);
    }
    public <T> T get(ConfigContainer<T> cont) {
        T val = configFile().get(cont.path(), cont.def());
        if (val instanceof String s) {
            if (s.isBlank() || s.isEmpty()) val = null;
        }
        return val;
    }
    public <X extends Enum<X>> X getEnum(ConfigContainer<X> container) {
        X result;
        try {
            result = configFile().getEnum(container.path(), container.def().getDeclaringClass());
        } catch (Exception ignored) {
            NobleWhitelist.log(Level.SEVERE, "Cannot load enum from config, using " + container.def().toString() + " as default.");
            result = container.def();
        }
        return result;
    }
    public <T> void set(ConfigContainer<T> cont, T value) {
        configFile().set(cont.path(), value);
    }
    public static class WhitelistCF {
        public static final ConfigContainer<Boolean> whitelistActive = new ConfigContainer<>("whitelist.enabled", false);
        public static final ConfigContainer<Boolean> skipName = new ConfigContainer<>("whitelist.skip-name-change", false);
        public static final ConfigContainer<CheckType> checkName = new ConfigContainer<>("whitelist.checking.name", CheckType.OPTIONAL);
        public static final ConfigContainer<CheckType> checkUUID = new ConfigContainer<>("whitelist.checking.uuid", CheckType.OPTIONAL);
        public static final ConfigContainer<CheckType> checkPerm = new ConfigContainer<>("whitelist.checking.perm", CheckType.DISABLED);
        public static final ConfigContainer<Boolean> onlyOpPerm = new ConfigContainer<>("whitelist.only-op-as-permission", false);
        public static final ConfigContainer<Integer> permissionMinimum = new ConfigContainer<>("whitelist.permission-minimum-number", -1);
        public static final ConfigContainer<Boolean> autoRegister = new ConfigContainer<>("whitelist.auto-register", false);
        public static final ConfigContainer<Boolean> enforceNameDiffID = new ConfigContainer<>("whitelist.enforce-uuid-if-different-name", false);
    }
    public static class StorageCF {
        public static final ConfigContainer<String> storageType = new ConfigContainer<>("storage.storage-type", "");
        public static final ConfigContainer<String> storageHost = new ConfigContainer<>("storage.host", "");
        public static final ConfigContainer<Integer> storagePort = new ConfigContainer<>("storage.port", 3306);
        public static final ConfigContainer<String> storageDBName = new ConfigContainer<>("storage.database", "");
        public static final ConfigContainer<String> storagePassword = new ConfigContainer<>("storage.password", "");
        public static final ConfigContainer<String> storageUser = new ConfigContainer<>("storage.user", "");
        public static final ConfigContainer<Boolean> closeFail = new ConfigContainer<>("storage.close-if-failed", false);
        public static final ConfigContainer<Boolean> blockFail = new ConfigContainer<>("storage.block-if-failed", false);
    }
    public static class MessagesCF {
        public static final ConfigContainer<String> kickMsg = new ConfigContainer<>("messages.not-whitelisted", "<red>You are not whitelisted on this server.");
        public static final ConfigContainer<String> nameChangePlayer = new ConfigContainer<>("messages.name-change-player", "");
        public static final ConfigContainer<String> nameChangeConsole = new ConfigContainer<>("messages.name-change-console", "");
    }
    public static class ServerCF {
        public static final ConfigContainer<Integer> configVersion = new ConfigContainer<>("version.version", 3);
        public static final ConfigContainer<Boolean> notifyUpdate = new ConfigContainer<>("version.notify-update", true);
    }
    public CheckType checkName() {
        return getEnum(WhitelistCF.checkName);
    }
    public CheckType checkUUID() {
        return getEnum(WhitelistCF.checkUUID);
    }
    public CheckType checkPerm() {
        return getEnum(WhitelistCF.checkPerm);
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
