package me.nobeld.minecraft.noblewhitelist.discord.config;

import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.minecraft.noblewhitelist.model.ConfigContainer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static me.nobeld.minecraft.noblewhitelist.config.FileManager.separator;
import static me.nobeld.minecraft.noblewhitelist.discord.NWLDiscord.getPlugin;

public class ConfigData {
    private static Yaml configFile = null;
    private static void registerConfig() {
        Path configPath = Paths.get(getPlugin().getDataFolder().getPath() + separator() + "config.yml");
        configFile = SimplixBuilder.fromPath(configPath)
                .addInputStreamFromResource("config.yml")
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setDataType(DataType.SORTED)
                .createYaml()
                .addDefaultsFromInputStream();
    }
    public static Yaml configFile() {
        if (configFile == null) {
            registerConfig();
        }
        return configFile;
    }
    public static <T> T get(ConfigContainer<T> cont) {
        T result = configFile().get(cont.path(), cont.def());
        if (result instanceof String s) {
            if (s.isBlank() || s.isEmpty()) result = null;
        }
        return result;
    }
    public static FlatFileSection getSection(ConfigContainer<?> container) {
        FlatFileSection result;
        try {
            result = configFile().getSection(container.path());
        } catch (Exception ignored) {
            result = null;
        }
        return result;
    }
    public static List<String> getList(ConfigContainer<String> container) {
        List<String> result;
        try {
            result = configFile().getStringList(container.path());
        } catch (Exception ignored) {
            result = null;
        }
        return result;
    }
    public static final ConfigContainer<Boolean> essentialsIntegration = new ConfigContainer<>("discord.essentials.essentials-integration", false);
    public static final ConfigContainer<Long> serverID = new ConfigContainer<>("discord.server-id", -1L);
    public static final ConfigContainer<String> channelsID = new ConfigContainer<>("channel", "");
    public static final ConfigContainer<String> roleUserID = new ConfigContainer<>("role.user", "");
    public static final ConfigContainer<String> roleStaffID = new ConfigContainer<>("role.staff", "");
    public static final ConfigContainer<String> roleAdminID = new ConfigContainer<>("role.admin", "");
    public static final ConfigContainer<Boolean> notifyUpdate = new ConfigContainer<>("version.notify-update", true);
    public static final ConfigContainer<Boolean> serverManagePermission = new ConfigContainer<>("special.admin-only-server-manage", true);
    //TODO premium suggestion
    public static final ConfigContainer<Boolean> suggestPremium = new ConfigContainer<>("special.suggest-premium-if-possible", false);
    public static class Channel {
        public static final ConfigContainer<String> startChannel = new ConfigContainer<>("announce-channel.start", "");
        public static final ConfigContainer<String> stopChannel = new ConfigContainer<>("announce-channel.stop", "");
        public static final ConfigContainer<String> selfRegister = new ConfigContainer<>("announce-channel.notify-self-register", "");
        public static final ConfigContainer<String> selfRemove = new ConfigContainer<>("announce-channel.notify-self-remove", "");
        public static final ConfigContainer<String> whitelistJoin = new ConfigContainer<>("announce-channel.notify-wl-join", "");
        public static final ConfigContainer<String> whitelistTry = new ConfigContainer<>("announce-channel.notify-wl-try", "");
        public static final ConfigContainer<String> whitelistAuto = new ConfigContainer<>("announce-channel.notify-wl-auto", "");
    }
    public static class CommandsRole {
        public static final ConfigContainer<String> selfAdd = new ConfigContainer<>("command.basic-add-self.role", "");
        public static final ConfigContainer<String> selfRemove = new ConfigContainer<>("command.basic-remove-self.role", "");
        public static final ConfigContainer<String> selfLink = new ConfigContainer<>("command.basic-link-self.role", "");
        public static final ConfigContainer<String> selfAccounts = new ConfigContainer<>("command.basic-self-accounts.role", "");
        public static final ConfigContainer<String> adminAdd = new ConfigContainer<>("command.admin-add.role", "");
        public static final ConfigContainer<String> adminRemove = new ConfigContainer<>("command.admin-remove.role", "");
        public static final ConfigContainer<String> adminOn = new ConfigContainer<>("command.admin-on.role", "");
        public static final ConfigContainer<String> adminOff = new ConfigContainer<>("command.admin-off.role", "");
        public static final ConfigContainer<String> adminList = new ConfigContainer<>("command.admin-list.role", "");
        public static final ConfigContainer<String> adminFind = new ConfigContainer<>("command.admin-find.role", "");
        public static final ConfigContainer<String> adminUser = new ConfigContainer<>("command.admin-find-user.role", "");
        public static final ConfigContainer<String> adminLink = new ConfigContainer<>("command.admin-user-link.role", "");
        public static final ConfigContainer<String> adminUnLink = new ConfigContainer<>("command.admin-user-unlink.role", "");
        public static final ConfigContainer<String> adminToggle = new ConfigContainer<>("command.admin-user-toggle.role", "");
    }
    public static class CommandsChannel {
        public static final ConfigContainer<String> selfAdd = new ConfigContainer<>("command.basic-add-self.channel", "");
        public static final ConfigContainer<String> selfRemove = new ConfigContainer<>("command.basic-remove-self.channel", "");
        public static final ConfigContainer<String> selfLink = new ConfigContainer<>("command.basic-link-self.channel", "");
        public static final ConfigContainer<String> selfAccounts = new ConfigContainer<>("command.basic-self-accounts.channel", "");
        public static final ConfigContainer<String> adminAdd = new ConfigContainer<>("command.admin-add.channel", "");
        public static final ConfigContainer<String> adminRemove = new ConfigContainer<>("command.admin-remove.channel", "");
        public static final ConfigContainer<String> adminOn = new ConfigContainer<>("command.admin-on.channel", "");
        public static final ConfigContainer<String> adminOff = new ConfigContainer<>("command.admin-off.channel", "");
        public static final ConfigContainer<String> adminList = new ConfigContainer<>("command.admin-list.channel", "");
        public static final ConfigContainer<String> adminFind = new ConfigContainer<>("command.admin-find.channel", "");
        public static final ConfigContainer<String> adminUser = new ConfigContainer<>("command.admin-find-user.channel", "");
        public static final ConfigContainer<String> adminLink = new ConfigContainer<>("command.admin-user-link.channel", "");
        public static final ConfigContainer<String> adminUnLink = new ConfigContainer<>("command.admin-user-unlink.channel", "");
        public static final ConfigContainer<String> adminToggle = new ConfigContainer<>("command.admin-user-toggle.channel", "");
    }
}
