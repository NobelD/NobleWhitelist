package me.nobeld.noblewhitelist.config;

import de.leonhard.storage.internal.FlatFile;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;
import me.nobeld.noblewhitelist.model.checking.CheckingType;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConfigData {
    private FlatFile configFile;
    private final String path;
    private final String name;
    private final FileManager.FileType type;
    public ConfigData(String path, String name, FileManager.FileType type) {
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

        List<String> remove = new ArrayList<>();

        if (get(ServerCF.configVersion) <= 2) {
            remove.add("whitelist.count-op-as-bypass");

            boolean oldClose = configFile().get("storage.close-if-failed", false);
            if (oldClose) configFile().set("storage.action-if-fail", "CLOSE");
            else configFile().set("storage.action-if-fail", "NONE");
            remove.add("storage.close-if-failed");

            set(ServerCF.configVersion, 3);
        }

        if (configFile().contains("whitelist.max-uuid-list")) {
            remove.add("whitelist.max-uuid-list");
            configFile().set(StorageCF.storageType.path(), "yaml");
        }
        if (configFile().get("incompatibilities") != null) {
            remove.add("incompatibilities.check-invalid-char");
            remove.add("incompatibilities.to-use");
            remove.add("incompatibilities.to-change");
        }
        if (!remove.isEmpty())
            configFile().removeAll(remove.toArray(new String[0]));
    }
    public String getPrefix() {
        return configFile().getString("messages.prefix");
    }
    public boolean usePrefix() {
        return configFile().getBoolean("messages.use-prefix");
    }
    public <T> T get(ConfigContainer<T> container) {
        try {
            return configFile().get(container.path(), container.def());
        } catch (Throwable e) {
            if (e instanceof Exception ex) {
                NobleWhitelist.log(Level.WARNING, "An error occurred while loading the path: '" + container.path() + "', using default instead: " + container.def(), ex);
            } else {
                NobleWhitelist.log(Level.WARNING, "Error '" + e.getMessage() +"' for path: '" + container.path() + "'");
            }
            return container.def();
        }
    }
    public <X extends Enum<X>> X getEnumUpper(ConfigContainer<X> container) {
        try {
            String str = configFile().getString(container.path());
            return Enum.valueOf(container.def().getDeclaringClass(), str.toUpperCase());
        } catch (Throwable e) {
            if (e instanceof Exception ex) {
                NobleWhitelist.log(Level.SEVERE, "An error occurred while loading the enum from path: '" + container.path() + "using default instead: " + container.def().toString(), ex);
            } else {
                NobleWhitelist.log(Level.WARNING, "Error '" + e.getMessage() +"' for path: '" + container.path() + "'");
            }
            return container.def();
        }
    }
    public <X extends Enum<X>> X getEnum(ConfigContainer<X> container) {
        try {
            return configFile().getEnum(container.path(), container.def().getDeclaringClass());
        } catch (Throwable e) {
            if (e instanceof Exception ex) {
                NobleWhitelist.log(Level.SEVERE, "An error occurred while loading the enum from path: '" + container.path() + "using default instead: " + container.def().toString(), ex);
            } else {
                NobleWhitelist.log(Level.WARNING, "Error '" + e.getMessage() +"' for path: '" + container.path() + "'");
            }
            return container.def();
        }
    }
    public <T> void set(ConfigContainer<T> container, T value) {
        try {
            configFile().set(container.path(), value);
        } catch (Throwable e) {
            if (e instanceof Exception ex) {
                NobleWhitelist.log(Level.WARNING, "An error occurred while setting data to the path: '" + container.path() + "'", ex);
            } else {
                NobleWhitelist.log(Level.WARNING, "Error '" + e.getMessage() +"' for path: '" + container.path() + "'");
            }
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
        public static final ConfigContainer<Boolean> useCustomPermission = new ConfigContainer<>("whitelist.use-custom-permission", false);
        public static final ConfigContainer<String> customPermission = new ConfigContainer<>("whitelist.custom-permission", "");
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
        public static final ConfigContainer<Integer> storageTimeout = new ConfigContainer<>("storage.connection-timeout", 30);
        public static final ConfigContainer<Integer> storageLifetime = new ConfigContainer<>("storage.max-lifetime", 30);
        public static final ConfigContainer<Boolean> storageUseSSL = new ConfigContainer<>("storage.use-ssl", false);
        public static final ConfigContainer<Boolean> storagePublicKeyRetrieval = new ConfigContainer<>("storage.allow-public-key-retrieval", false);
        public static final ConfigContainer<String> storagePublicKeyFile = new ConfigContainer<>("storage.server-rsa-public-key-file", "");
        public static final ConfigContainer<String> storageSSLMode = new ConfigContainer<>("storage.ssl-mode", "required");
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
        return getEnumUpper(WhitelistCF.checkName);
    }
    public CheckingOption checkUUID() {
        return getEnumUpper(WhitelistCF.checkUUID);
    }
    public CheckingOption checkPerm() {
        return getEnumUpper(WhitelistCF.checkPerm);
    }
    public enum FailAction {
        NONE,
        CLOSE,
        BLOCK,
        COMMAND
    }
}
