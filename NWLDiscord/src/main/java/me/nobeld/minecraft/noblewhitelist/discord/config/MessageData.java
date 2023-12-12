package me.nobeld.minecraft.noblewhitelist.discord.config;

import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.minecraft.noblewhitelist.model.ConfigContainer;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static me.nobeld.minecraft.noblewhitelist.config.FileManager.separator;
import static me.nobeld.minecraft.noblewhitelist.discord.NWLDiscord.getPlugin;

public class MessageData {
    private static Yaml messagesFile = null;
    private static void registerMessages() {
        Path configPath = Paths.get(getPlugin().getDataFolder().getPath() + separator() + "messages.yml");
        messagesFile = SimplixBuilder.fromPath(configPath)
                .addInputStreamFromResource("messages.yml")
                .setConfigSettings(ConfigSettings.SKIP_COMMENTS)
                .setDataType(DataType.SORTED)
                .createYaml()
                .addDefaultsFromInputStream();
    }
    public static Yaml messageFile() {
        if (messagesFile == null) {
            registerMessages();
        }
        return messagesFile;
    }
    public static Map<String, String> baseHolder(@NotNull PlayerWhitelisted data) {
        Map<String, String> map = new HashMap<>();
        StringBuilder string = new StringBuilder();
        if (data.getOptName().isPresent()) string.append(getMsg(PlaceHolders.optionalName)).append("$$");
        if (data.getOptUUID().isPresent()) string.append("$$").append(getMsg(PlaceHolders.optionalUUID)).append("$$");
        if (data.hasDiscord()) string.append("$$").append(getMsg(PlaceHolders.optionalID));

        String dat = string.toString().replace("$$$$", "\n").replace("$$", "");
        if (dat.isEmpty() || dat.isBlank()) dat = getMsg(PlaceHolders.optionalNone);

        map.put("player-optional", dat);
        map.put("player-data", getMsg(PlaceHolders.playerData));
        map.put("toggle", data.isWhitelisted() ? getMsg(PlaceHolders.toggledTrue) : getMsg(PlaceHolders.toggledFalse));
        map.put("user-mention", data.hasDiscord() ? ("<@" + data.getDiscordID() + ">") : "invalid");

        return data.toMap(map);
    }
    public static String getMsg(ConfigContainer<String> cont) {
        return messageFile().get(cont.path(), cont.def());
    }
    public static FlatFileSection getMsgSec(ConfigContainer<?> cont) {
        return messageFile().getSection(cont.path());
    }
    public static class PlaceHolders {
        public static final ConfigContainer<String> optionalNone = new ConfigContainer<>("placeholders.optional-none", "");
        public static final ConfigContainer<String> optionalName = new ConfigContainer<>("placeholders.optional-name", "");
        public static final ConfigContainer<String> optionalUUID = new ConfigContainer<>("placeholders.optional-uuid", "");
        public static final ConfigContainer<String> optionalID = new ConfigContainer<>("placeholders.optional-id", "");
        public static final ConfigContainer<String> playerData = new ConfigContainer<>("placeholders.player-data", "");
        public static final ConfigContainer<String> toggledTrue = new ConfigContainer<>("placeholders.toggled-true", "");
        public static final ConfigContainer<String> toggledFalse = new ConfigContainer<>("placeholders.toggled-false", "");
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
        public static final ConfigContainer<String> insufficientData = new ConfigContainer<>("discord.error.insufficient-data", "");
        public static final ConfigContainer<String> invalidUuid = new ConfigContainer<>("discord.error.invalidad-uuid", "");
        public static final ConfigContainer<String> selfAlready = new ConfigContainer<>("discord.error.user-self-already", "");
        public static final ConfigContainer<String> userAlready = new ConfigContainer<>("discord.error.user-already", "");
        public static final ConfigContainer<String> userNotFound = new ConfigContainer<>("discord.error.user-not-found", "");
        public static final ConfigContainer<String> selfNoAccounts = new ConfigContainer<>("discord.error.self-no-accounts", "");
        public static final ConfigContainer<String> userNoAccounts = new ConfigContainer<>("discord.error.user-no-accounts", "");
        public static final ConfigContainer<String> alreadySelfLinked = new ConfigContainer<>("discord.error.already-self-linked", "");
        public static final ConfigContainer<String> alreadyUserLinked = new ConfigContainer<>("discord.error.already-user-linked", "");
        public static final ConfigContainer<String> alreadyToggled = new ConfigContainer<>("discord.error.already-toggled", "");
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
        //TODO premium suggestion
        public static final ConfigContainer<String> userRegPremium = new ConfigContainer<>("discord.command.wl-add-premium", "");
        public static final ConfigContainer<String> userSuggestPremium = new ConfigContainer<>("discord.command.wl-suggest-premium", "");
    }
}
