package me.nobeld.noblewhitelist.discord.config;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.discord.NWLDiscord;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static me.nobeld.noblewhitelist.config.FileManager.separator;

public class MessageData {
    private FlatFile messagesFile = null;
    private final NWLDsData data;
    private final String path;
    private final String name;
    private final FileManager.FileType type;
    public MessageData(NWLDsData data, String path, String name, FileManager.FileType type) {
        this.data = data;
        this.path = path;
        this.name = name;
        this.type = type;
    }
    private void registerMessages() {
        Path configPath = Paths.get(path + separator() + name);
        messagesFile = FileManager.registerFile(type, configPath, null, data.resourceStream(name), ConfigSettings.SKIP_COMMENTS, DataType.SORTED);
    }
    public FlatFile messageFile() {
        if (messagesFile == null) {
            registerMessages();
        }
        return messagesFile;
    }
    public Map<String, String> baseHolder(@NotNull WhitelistEntry data) {
        Map<String, String> map = new HashMap<>();
        StringBuilder string = new StringBuilder();
        if (data.getOptName().isPresent()) string.append(getMsg(PlaceHolders.optionalName)).append("$$");
        if (data.getOptUUID().isPresent()) string.append("$$").append(getMsg(PlaceHolders.optionalUUID)).append("$$");
        if (data.hasDiscord()) string.append("$$").append(getMsg(PlaceHolders.optionalID));

        String dat = string.toString().replace("$$$$", "\n").replace("$$", "");
        if (dat.isEmpty() || dat.isBlank()) dat = getMsg(PlaceHolders.optionalNone);

        map.put("player_optional", dat);
        map.put("player_data", getMsg(PlaceHolders.playerData));
        map.put("toggle", data.isWhitelisted() ? getMsg(PlaceHolders.toggledTrue) : getMsg(PlaceHolders.toggledFalse));
        map.put("user_mention", data.hasDiscord() ? ("<@" + data.getDiscordID() + ">") : "invalid");

        return data.toMap(map);
    }
    public String getMsg(ConfigContainer<String> container) {
        try {
            return messageFile().get(container.path(), container.def());
        } catch (Exception e) {
            NWLDiscord.log(Level.WARNING, "An error occurred while loading the path: '" + container.path() + "', using default instead: " + container.def());
            NWLDiscord.log(Level.WARNING, e.getMessage());
            return container.def();
        }
    }
    public FlatFileSection getMsgSec(ConfigContainer<?> container) {
        try {
            return messageFile().getSection(container.path());
        } catch (Exception e) {
            NWLDiscord.log(Level.WARNING, "An error occurred while loading the path: '" + container.path() + "'");
            NWLDiscord.log(Level.WARNING, e.getMessage());
            return null;
        }
    }
    public static class PlaceHolders {
        public static final ConfigContainer<String> optionalNone = new ConfigContainer<>("placeholders.optional-none", "");
        public static final ConfigContainer<String> optionalName = new ConfigContainer<>("placeholders.optional-name", "");
        public static final ConfigContainer<String> optionalUUID = new ConfigContainer<>("placeholders.optional-uuid", "");
        public static final ConfigContainer<String> optionalID = new ConfigContainer<>("placeholders.optional-id", "");
        public static final ConfigContainer<String> playerData = new ConfigContainer<>("placeholders.player-data", "");
        public static final ConfigContainer<String> toggledTrue = new ConfigContainer<>("placeholders.toggled-true", "");
        public static final ConfigContainer<String> toggledFalse = new ConfigContainer<>("placeholders.toggled-false", "");
        public static final ConfigContainer<String> enabled = new ConfigContainer<>("placeholders.status-enabled", "");
        public static final ConfigContainer<String> disabled = new ConfigContainer<>("placeholders.status-disabled", "");
        public static final ConfigContainer<String> accountEntry = new ConfigContainer<>("placeholders.account-entry", "");
        public static final ConfigContainer<String> listEntry = new ConfigContainer<>("placeholders.list-entry", "");
        public static final ConfigContainer<String> checkingDisabled = new ConfigContainer<>("placeholders.checking-none", "");
        public static final ConfigContainer<String> checkingOptional = new ConfigContainer<>("placeholders.checking-optional", "");
        public static final ConfigContainer<String> checkingRequired = new ConfigContainer<>("placeholders.checking-required", "");
    }
    public static class Channel {
        public static final ConfigContainer<String> notifyStart = new ConfigContainer<>("discord.channel.start", "");
        public static final ConfigContainer<String> notifyStop = new ConfigContainer<>("discord.channel.stop", "");
        public static final ConfigContainer<String> notifySelfAdd = new ConfigContainer<>("discord.channel.notify-self-register", "");
        public static final ConfigContainer<String> notifySelfRemove = new ConfigContainer<>("discord.channel.notify-self-remove", "");
        public static final ConfigContainer<String> notifyRoleAdd = new ConfigContainer<>("discord.channel.notify-role-add", "");
        public static final ConfigContainer<String> notifyRoleRemove = new ConfigContainer<>("discord.channel.notify-role-remove", "");
        public static final ConfigContainer<String> serverJoin = new ConfigContainer<>("discord.channel.notify-wl-join", "");
        public static final ConfigContainer<String> serverTry = new ConfigContainer<>("discord.channel.notify-wl-try", "");
        public static final ConfigContainer<String> serverAuto = new ConfigContainer<>("discord.channel.notify-wl-auto", "");
    }
    public static class Error {
        public static final ConfigContainer<String> noPermission = new ConfigContainer<>("discord.error.no-permission", "");
        public static final ConfigContainer<String> incorrectChannel = new ConfigContainer<>("discord.error.invalid-channel", "");
        public static final ConfigContainer<String> invalidGuild = new ConfigContainer<>("discord.error.invalid-guild", "");
        public static final ConfigContainer<String> invalidMember = new ConfigContainer<>("discord.error.invalid-member", "");
        public static final ConfigContainer<String> noInputtedData = new ConfigContainer<>("discord.error.no-inputted-data", "");
        public static final ConfigContainer<String> insufficientData = new ConfigContainer<>("discord.error.insufficient-data", "");
        public static final ConfigContainer<String> invalidUuid = new ConfigContainer<>("discord.error.invalid-uuid", "");
        public static final ConfigContainer<String> selfAlready = new ConfigContainer<>("discord.error.user-self-already", "");
        public static final ConfigContainer<String> userAlready = new ConfigContainer<>("discord.error.user-already", "");
        public static final ConfigContainer<String> userNotFound = new ConfigContainer<>("discord.error.user-not-found", "");
        public static final ConfigContainer<String> selfNoAccounts = new ConfigContainer<>("discord.error.self-no-accounts", "");
        public static final ConfigContainer<String> userNoAccounts = new ConfigContainer<>("discord.error.user-no-accounts", "");
        public static final ConfigContainer<String> alreadySelfLinked = new ConfigContainer<>("discord.error.already-self-linked", "");
        public static final ConfigContainer<String> alreadyUserLinked = new ConfigContainer<>("discord.error.already-user-linked", "");
        public static final ConfigContainer<String> alreadyToggled = new ConfigContainer<>("discord.error.already-toggled", "");
        public static final ConfigContainer<String> whitelistEmpty = new ConfigContainer<>("discord.error.whitelist-empty", "");
        public static final ConfigContainer<String> whitelistPageEmpty = new ConfigContainer<>("discord.error.whitelist-page-empty", "");
    }
    public static class Command {
        public static final ConfigContainer<String> selfAccounts = new ConfigContainer<>("discord.command.self-accounts", "");
        public static final ConfigContainer<String> userAccounts = new ConfigContainer<>("discord.command.user-accounts", "");
        public static final ConfigContainer<String> userAdd = new ConfigContainer<>("discord.command.add-user", "");
        public static final ConfigContainer<String> selfAdd = new ConfigContainer<>("discord.command.self-add", "");
        public static final ConfigContainer<String> userRemove = new ConfigContainer<>("discord.command.remove-user", "");
        public static final ConfigContainer<String> selfRemove = new ConfigContainer<>("discord.command.self-remove", "");
        public static final ConfigContainer<String> wlOff = new ConfigContainer<>("discord.command.whitelist-off", "");
        public static final ConfigContainer<String> wlAlreadyOff = new ConfigContainer<>("discord.command.already-off", "");
        public static final ConfigContainer<String> wlOn = new ConfigContainer<>("discord.command.whitelist-on", "");
        public static final ConfigContainer<String> wlAlreadyOn = new ConfigContainer<>("discord.command.already-on", "");
        public static final ConfigContainer<String> userFind = new ConfigContainer<>("discord.command.find-user", "");
        public static final ConfigContainer<String> selfLink = new ConfigContainer<>("discord.command.self-link", "");
        public static final ConfigContainer<String> userLink = new ConfigContainer<>("discord.command.user-link", "");
        public static final ConfigContainer<String> userUnLink = new ConfigContainer<>("discord.command.user-unlink", "");
        public static final ConfigContainer<String> userToggled = new ConfigContainer<>("discord.command.user-toggled", "");
        public static final ConfigContainer<String> permStatus = new ConfigContainer<>("discord.command.perm-status", "");
        public static final ConfigContainer<String> permSet = new ConfigContainer<>("discord.command.perm-set", "");
        public static final ConfigContainer<String> listPage = new ConfigContainer<>("discord.command.list-page", "");
        public static final ConfigContainer<String> checkStatus = new ConfigContainer<>("discord.command.checking-status", "");
        public static final ConfigContainer<String> checkSet = new ConfigContainer<>("discord.command.checking-set", "");
        //TODO premium suggestion
        public static final ConfigContainer<String> userRegPremium = new ConfigContainer<>("discord.command.wl-add-premium", "");
        public static final ConfigContainer<String> userSuggestPremium = new ConfigContainer<>("discord.command.wl-suggest-premium", "");
    }
}
