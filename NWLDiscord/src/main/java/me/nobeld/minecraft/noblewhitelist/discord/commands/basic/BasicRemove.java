package me.nobeld.minecraft.noblewhitelist.discord.commands.basic;

import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandOption;
import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil.*;

public class BasicRemove implements SubCommand {
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
        return "remove";
    }
    @Override
    public String getDescription() {
        return "Remove your account from the whitelist by the uuid.";
    }
    @Override
    public List<CommandOption> getOptions() {
        //return List.of(new CommandOption(OptionType.STRING, "name", "Name of the player to be removed", false));
        return null;
    }
    @Override
    public void onCommand(InteractResult event) {
        if (event.getManager().notRole(event.getMember(), ConfigData.CommandsRole.selfRemove)) {
            DiscordUtil.replyMessage(event, MessageData.Error.noPermission);
            return;
        }
        if (event.getManager().notChannel(event, ConfigData.CommandsChannel.selfRemove)) {
            DiscordUtil.replyMessage(event, MessageData.Error.incorrectChannel);
            return;
        }
        long userid = event.getMember().getIdLong();
        Optional<PlayerWhitelisted> data2 = getPlugin().api().getWlData().getData(null, null, userid);
        if (data2.isEmpty()) {
            event.reply("You don't have any account registered to the whitelist.").setEphemeral(true).queue();
            return;
        }

        Optional<PlayerWhitelisted> data = getPlugin().api().getWlData().getData(null, null, userid);

        if (data.isPresent()) {
            getPlugin().api().getWlData().deleteUser(data.get());
            Map<String, String> m = MessageData.baseHolder(data.get());
            replyMessage(event, MessageData.Command.selfRemove, () -> m);
            sendMessage(event.getManager().getChannel(ConfigData.Channel.selfRemove), getMessage(MessageData.Channel.notifySelfRemove, () -> m));
        } else
            DiscordUtil.replyMessage(event, MessageData.Error.userNotFound);
    }
}
