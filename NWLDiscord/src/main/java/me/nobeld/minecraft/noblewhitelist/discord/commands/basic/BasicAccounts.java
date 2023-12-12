package me.nobeld.minecraft.noblewhitelist.discord.commands.basic;

import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandOption;
import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;

import java.util.List;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;

public class BasicAccounts implements SubCommand {
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
        return "accounts";
    }
    @Override
    public String getDescription() {
        return "Get the accounts who are you linked from the whitelist.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return null;
    }
    @Override
    public void onCommand(InteractResult event) {
        if (event.getManager().notRole(event.getMember(), ConfigData.CommandsRole.selfAccounts)) {
            DiscordUtil.replyMessage(event, MessageData.Error.noPermission);
            return;
        }
        if (event.getManager().notChannel(event, ConfigData.CommandsChannel.selfAccounts)) {
            DiscordUtil.replyMessage(event, MessageData.Error.incorrectChannel);
            return;
        }

        long userid = event.getMember().getIdLong();

        PlayerWhitelisted data = getPlugin().getStorageInst().loadPlayer(userid);
        if (data == null) {
            DiscordUtil.replyMessage(event, MessageData.Error.selfNoAccounts);
        } else
            DiscordUtil.replyMessage(event, MessageData.Command.selfAccounts, () -> MessageData.baseHolder(data));
    }
}
