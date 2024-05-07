package me.nobeld.noblewhitelist.config;

import de.leonhard.storage.internal.FlatFile;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;
import me.nobeld.noblewhitelist.model.checking.CheckingType;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class ConfigData {
    private final NWLData data;
    private FlatFile configFile;
    private final String path;
    private final String name;
    private final FileManager.FileType type;
    public ConfigData(NWLData data, String path, String name, FileManager.FileType type) {
        this.data = data;
        this.path = path;
        this.name = name;
        this.type = type;
    }
    public void registerConfig() {
        Path filePath = Paths.get(path + FileManager.separator() + name);
        configFile = FileManager.registerFile(type, filePath, name);
    }
    public void reloadConfig() {
        configFile().forceReload();
    }
    public FlatFile configFile() {
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

            boolean oldClose = configFile().get("storage.close-if-failed", false);
            if (oldClose) configFile().set("storage.action-if-fail", "CLOSE");
            else configFile().set("storage.action-if-fail", "NONE");
            configFile().remove("storage.close-if-failed");

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
    public <T> T get(ConfigContainer<T> container) {
        try {
            T val;
            val = configFile().get(container.path(), container.def());
            if (val instanceof String s) {
                if (s.isBlank() || s.isEmpty()) val = null;
            }
            return val;
        } catch (Exception e) {
            data.logger().log(Level.WARNING, "An error occurred while loading the path: '" + container.path() + "', using default instead: " + container.def(), e);
            return container.def();
        }
    }
    public <X extends Enum<X>> X getEnum(ConfigContainer<X> container) {
        try {
            return configFile().getEnum(container.path(), container.def().getDeclaringClass());
        } catch (Exception e) {
            data.logger().log(Level.SEVERE, "An error occurred while loading the enum from path: '" + container.path() + "using default instead: " + container.def().toString(), e);
            return container.def();
        }
    }
    public <T> void set(ConfigContainer<T> container, T value) {
        try {
            configFile().set(container.path(), value);
        } catch (Exception e) {
            data.logger().log(Level.WARNING, "An error occurred while setting data to the path: '" + container.path() + "'", e);
        }
    }
    public static class WhitelistCF {
        public static final ConfigContainer<Boolean> whitelistActive = new ConfigContainer<>("whitelist.enabled", false);
        public static final ConfigContainer<Boolean> skipName = new ConfigContainer<>("whitelist.skip-name-change", false);
        public static final ConfigContainer<CheckingOption> checkName = new ConfigContainer<>("whitelist.checking.name", CheckingOption.OPTIONAL);
        public static final ConfigContainer<CheckingOption> checkUUID = new ConfigContainer<>("whitelist.checking.uuid", CheckingOption.OPTIONAL);
        public static final ConfigContainer<CheckingOption> checkPerm = new ConfigContainer<>("whitelist.checking.perm", CheckingOption.DISABLED);
        public static final ConfigContainer<Boolean> onlyOpPerm = new ConfigContainer<>("whitelist.only-op-as-permission", false);
        public static final ConfigContainer<Integer> permissionMinimum = new ConfigContainer<>("whitelist.permission-minimum-number", -1);
        public static final ConfigContainer<Boolean> autoRegister = new ConfigContainer<>("whitelist.auto-register", false);
        public static final ConfigContainer<Boolean> enforceNameDiffID = new ConfigContainer<>("whitelist.enforce-uuid-if-different-name", false);
    }
    public static class SkipCF {
        public static final ConfigContainer<Boolean> skipUUID = new ConfigContainer<>("skip.skip-uuid-save", false);
    }
    public static class StorageCF {
        public static final ConfigContainer<String> storageType = new ConfigContainer<>("storage.storage-type", "");
        public static final ConfigContainer<String> storageHost = new ConfigContainer<>("storage.host", "");
        public static final ConfigContainer<Integer> storagePort = new ConfigContainer<>("storage.port", 3306);
        public static final ConfigContainer<String> storageDBName = new ConfigContainer<>("storage.database", "");
        public static final ConfigContainer<String> storagePassword = new ConfigContainer<>("storage.password", "");
        public static final ConfigContainer<String> storageUser = new ConfigContainer<>("storage.user", "");
        public static final ConfigContainer<FailAction> failAction = new ConfigContainer<>("storage.action-if-fail", FailAction.NONE);
        public static final ConfigContainer<String> failCommand = new ConfigContainer<>("storage.command-if-fail", "");
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
    public CheckingOption getChecking(CheckingType type) {
        return switch (type) {
            case NAME -> checkName();
            case UUID -> checkUUID();
            case PERMISSION -> checkPerm();
        };
    }
    public void setChecking(CheckingType type, CheckingOption result) {
        ConfigContainer<?> c = switch (type) {
            case NAME -> WhitelistCF.checkName;
            case UUID -> WhitelistCF.checkUUID;
            case PERMISSION -> WhitelistCF.checkPerm;
        };
        configFile().set(c.path(), result.name());
    }
    public CheckingOption checkName() {
        return getEnum(WhitelistCF.checkName);
    }
    public CheckingOption checkUUID() {
        return getEnum(WhitelistCF.checkUUID);
    }
    public CheckingOption checkPerm() {
        return getEnum(WhitelistCF.checkPerm);
    }
    public enum FailAction {
        NONE,
        CLOSE,
        BLOCK,
        COMMAND
    }
}
