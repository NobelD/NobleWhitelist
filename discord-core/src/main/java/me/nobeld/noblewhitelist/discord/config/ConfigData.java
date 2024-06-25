package me.nobeld.noblewhitelist.discord.config;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

import static me.nobeld.noblewhitelist.config.FileManager.separator;

public class ConfigData {
    private FlatFile configFile = null;
    private final NWLDsData data;
    private final String path;
    private final String name;
    private final FileManager.FileType type;

    public ConfigData(NWLDsData data, String path, String name, FileManager.FileType type) {
        this.data = data;
        this.path = path;
        this.name = name;
        this.type = type;
        updateData();
    }

    private void updateData() {
        if (get(Version.configVersion) < 2) {
            String role = configFile().get("announce-channel.notify-given-role", "none");
            set(Channel.roleAdd, role);
            set(Channel.roleRemove, role);
            configFile().remove("announce-channel.notify-given-role");
            String adminS = configFile().get(CommandsOpt.selfLink.path() + ".role", "null");
            if (adminS.equalsIgnoreCase("-admin")) {
                configFile().set(CommandsOpt.selfLink.path() + ".role", List.of("admin"));
            }
            set(Version.configVersion, 2);
        }
    }

    private void registerConfig() {
        Path configPath = Paths.get(path + separator() + name);
        configFile = FileManager.registerFile(type, configPath, data.resourceStream(name));
    }

    public FlatFile configFile() {
        if (configFile == null) {
            registerConfig();
        }
        return configFile;
    }

    public <T> void set(ConfigContainer<T> container, T value) {
        try {
            configFile().set(container.path(), value);
        } catch (Exception e) {
            data.logger().log(Level.WARNING, "An error occurred while setting data to the path: '" + container.path() + "'");
            data.logger().log(Level.WARNING, e.getMessage());
        }
    }

    public <T> T get(ConfigContainer<T> container) {
        try {
            T result;
            result = configFile().get(container.path(), container.def());
            if (result instanceof String s) {
                if (s.isBlank() || s.isEmpty()) result = null;
            }
            return result;
        } catch (Exception e) {
            data.logger().log(Level.WARNING, "An error occurred while loading the path: '" + container.path() + "', using default instead: " + container.def());
            data.logger().log(Level.WARNING, e.getMessage());
            return container.def();
        }
    }

    public FlatFileSection getSection(ConfigContainer<?> container) {
        try {
            return configFile().getSection(container.path());
        } catch (Exception e) {
            data.logger().log(Level.WARNING, "An error occurred while loading the path: '" + container.path() + "'");
            data.logger().log(Level.WARNING, e.getMessage());
            return null;
        }
    }

    public List<String> getList(ConfigContainer<String> container) {
        try {
            return configFile().getStringList(container.path());
        } catch (Exception e) {
            data.logger().log(Level.WARNING, "An error occurred while loading the path: '" + container.path() + "'");
            data.logger().log(Level.WARNING, e.getMessage());
            return null;
        }
    }

    //TODO premium suggestion
    public static final ConfigContainer<Boolean> suggestPremium = new ConfigContainer<>("special.suggest-premium-if-possible", false);

    public static class Discord {
        public static final ConfigContainer<Long> serverID = new ConfigContainer<>("discord.server-id", -1L);
        public static final ConfigContainer<String> channelsID = new ConfigContainer<>("channel", "");
        public static final ConfigContainer<Boolean> roleEveryone = new ConfigContainer<>("role.enable-everyone-role", false);
        public static final ConfigContainer<String> roleUserID = new ConfigContainer<>("role.user", "");
        public static final ConfigContainer<String> roleStaffID = new ConfigContainer<>("role.staff", "");
        public static final ConfigContainer<String> roleAdminID = new ConfigContainer<>("role.admin", "");
        public static final ConfigContainer<Long> roleWhitelistedID = new ConfigContainer<>("role.whitelisted", -1L);
        public static final ConfigContainer<String> roleSubWhitelistedID = new ConfigContainer<>("role.sub-whitelisted", "");
        public static final ConfigContainer<Boolean> serverManagePermission = new ConfigContainer<>("special.admin-only-server-manage", true);
        public static final ConfigContainer<Boolean> giveWlRole = new ConfigContainer<>("special.give-role-on-register", false);
        public static final ConfigContainer<Boolean> removeWlRole = new ConfigContainer<>("special.remove-role-on-unregister", false);
    }

    public static class Version {
        public static final ConfigContainer<Boolean> notifyUpdate = new ConfigContainer<>("version.notify-update", true);
        public static final ConfigContainer<Integer> configVersion = new ConfigContainer<>("version.version", 2);
    }

    public static class Channel {
        public static final ConfigContainer<String> startChannel = new ConfigContainer<>("announce-channel.start", "");
        public static final ConfigContainer<String> stopChannel = new ConfigContainer<>("announce-channel.stop", "");
        public static final ConfigContainer<String> selfRegister = new ConfigContainer<>("announce-channel.notify-self-register", "");
        public static final ConfigContainer<String> selfRemove = new ConfigContainer<>("announce-channel.notify-self-remove", "");
        public static final ConfigContainer<String> roleAdd = new ConfigContainer<>("announce-channel.notify-role-add", "");
        public static final ConfigContainer<String> roleRemove = new ConfigContainer<>("announce-channel.notify-role-remove", "");
        public static final ConfigContainer<String> whitelistJoin = new ConfigContainer<>("announce-channel.notify-wl-join", "");
        public static final ConfigContainer<String> whitelistTry = new ConfigContainer<>("announce-channel.notify-wl-try", "");
        public static final ConfigContainer<String> whitelistAuto = new ConfigContainer<>("announce-channel.notify-wl-auto", "");
    }

    public static class CommandsOpt {
        public static final ConfigContainer<String> selfAdd = new ConfigContainer<>("command.basic-add-self", "");
        public static final ConfigContainer<String> selfRemove = new ConfigContainer<>("command.basic-remove-self", "");
        public static final ConfigContainer<String> selfLink = new ConfigContainer<>("command.basic-link-self", "");
        public static final ConfigContainer<String> selfAccounts = new ConfigContainer<>("command.basic-self-accounts", "");
        public static final ConfigContainer<String> adminAdd = new ConfigContainer<>("command.admin-add", "");
        public static final ConfigContainer<String> adminRemove = new ConfigContainer<>("command.admin-remove", "");
        public static final ConfigContainer<String> adminOn = new ConfigContainer<>("command.admin-on", "");
        public static final ConfigContainer<String> adminOff = new ConfigContainer<>("command.admin-off", "");
        public static final ConfigContainer<String> adminList = new ConfigContainer<>("command.admin-list", "");
        public static final ConfigContainer<String> adminFind = new ConfigContainer<>("command.admin-find", "");
        public static final ConfigContainer<String> adminUser = new ConfigContainer<>("command.admin-find-user", "");
        public static final ConfigContainer<String> adminLink = new ConfigContainer<>("command.admin-user-link", "");
        public static final ConfigContainer<String> adminUnLink = new ConfigContainer<>("command.admin-user-unlink", "");
        public static final ConfigContainer<String> adminToggle = new ConfigContainer<>("command.admin-user-toggle", "");
        public static final ConfigContainer<String> adminPermStatus = new ConfigContainer<>("command.admin-perm-status", "");
        public static final ConfigContainer<String> adminPermSet = new ConfigContainer<>("command.admin-perm-set", "");
        public static final ConfigContainer<String> adminCheckStatus = new ConfigContainer<>("command.admin-checking-status", "");
        public static final ConfigContainer<String> adminCheckSet = new ConfigContainer<>("command.admin-checking-set", "");
    }
}
