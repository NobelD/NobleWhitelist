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
import java.util.Optional;
import java.util.UUID;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;

public class AdminFindPlayer implements SubCommand {
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
        return "find";
    }
    @Override
    public String getDescription() {
        return "Find a player with the defined options.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return List.of(new CommandOption(OptionType.STRING, "name", "Name to find their accounts", false),
                new CommandOption(OptionType.STRING, "uuid", "Uuid to find their accounts", false));
    }
    @Override
    public void onCommand(InteractResult event) {
        if (event.getManager().notRole(event.getMember(), ConfigData.CommandsRole.adminFind)) {
            DiscordUtil.replyMessage(event, MessageData.Error.noPermission);
            return;
        }
        if (event.getManager().notChannel(event, ConfigData.CommandsChannel.adminFind)) {
            DiscordUtil.replyMessage(event, MessageData.Error.incorrectChannel);
            return;
        }
        String name = event.getOption("name").map(OptionMapping::getAsString).orElse(null);
        String uuid = event.getOption("uuid").map(OptionMapping::getAsString).orElse(null);
        if (name == null && uuid == null) {
            DiscordUtil.replyMessage(event, MessageData.Error.insufficientData);
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

        if (data.isEmpty()) {
            DiscordUtil.replyMessage(event, MessageData.Error.userNotFound);
        } else
            DiscordUtil.replyMessage(event, MessageData.Command.userFind, () -> MessageData.baseHolder(data.get()));


    }
}
