package me.nobeld.minecraft.noblewhitelist.discord.commands.admin;

import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandOption;
import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import me.nobeld.minecraft.noblewhitelist.util.UUIDUtil;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;

public class AdminUnLink implements SubCommand {
    @Override
    public boolean isEphemeral() {
        return false;
    }
    @Override
    public boolean isDefer() {
        return false;
    }
    @Override
    public String getName() {
        return "unlink";
    }
    @Override
    public String getDescription() {
        return "Unlink an account from their discord user.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return List.of(new CommandOption(OptionType.USER, "user", "User to be linked the account", false),
                new CommandOption(OptionType.STRING, "name", "Name of the player to be added", false),
                new CommandOption(OptionType.STRING, "uuid", "Uuid of the player to be added", false));
    }
    @Override
    public void onCommand(InteractResult event) {
        if (event.getManager().notRole(event.getMember(), ConfigData.CommandsRole.adminUnLink)) {
            DiscordUtil.replyMessage(event, MessageData.Error.noPermission);
            return;
        }
        if (event.getManager().notChannel(event, ConfigData.CommandsChannel.adminUnLink)) {
            DiscordUtil.replyMessage(event, MessageData.Error.incorrectChannel);
            return;
        }

        String name = event.getOption("name").map(OptionMapping::getAsString).orElse(null);
        String uuid = event.getOption("uuid").map(OptionMapping::getAsString).orElse(null);
        long userid = event.getOption("user").map(o -> o.getAsUser().getIdLong()).orElse(-1L);


        if (name == null && uuid == null) {
            if (userid >= 0) {
                Optional<PlayerWhitelisted> data2 = getPlugin().api().getWlData().getData(null, null, userid);
                if (data2.isPresent()) {
                    getPlugin().api().getWlData().linkUser(data2.get(), -1);
                    DiscordUtil.replyMessage(event, MessageData.Command.userUnLink, () -> MessageData.baseHolder(data2.get()));

                } else DiscordUtil.replyMessage(event, MessageData.Error.userNotFound);

            } else DiscordUtil.replyMessage(event, MessageData.Error.insufficientData);
            return;
        }
        UUID realuuid = null;

        if (uuid != null) {
            realuuid = UUIDUtil.parseUUID(uuid);
            if (realuuid == null) {
                DiscordUtil.replyMessage(event, MessageData.Error.invalidUuid);
                return;
            }
        }

        Optional<PlayerWhitelisted> data = getPlugin().api().getWlData().getData(name, realuuid, -1);

        if (data.isPresent()) {
            getPlugin().api().getWlData().linkUser(data.get(), -1);
            Map<String, String> m = MessageData.baseHolder(data.get());

            DiscordUtil.replyMessage(event, MessageData.Command.userUnLink, () -> m);
            event.getManager().setWhitelistedRole(event.getGuild(), data.get(), m, false);
        } else
            DiscordUtil.replyMessage(event, MessageData.Error.userNotFound);
    }
}
