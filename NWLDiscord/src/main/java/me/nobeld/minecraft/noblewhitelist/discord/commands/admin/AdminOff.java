package me.nobeld.minecraft.noblewhitelist.discord.commands.admin;

import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandOption;
import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil;

import java.util.List;

public class AdminOff implements SubCommand {
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
        return "off";
    }
    @Override
    public String getDescription() {
        return "Deactivate the whitelist.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return null;
    }
    @Override
    public void onCommand(InteractResult event) {
        if (event.getManager().notRole(event.getMember(), ConfigData.CommandsRole.adminOff)) {
            DiscordUtil.replyMessage(event, MessageData.Error.noPermission);
            return;
        }
        if (event.getManager().notChannel(event, ConfigData.CommandsChannel.adminOff)) {
            DiscordUtil.replyMessage(event, MessageData.Error.incorrectChannel);
            return;
        }
        if (NobleWhitelist.getPlugin().api().whitelist(false)) {
            DiscordUtil.replyMessage(event, MessageData.Command.wlOff);
        } else
            DiscordUtil.replyMessage(event, MessageData.Command.wlAlreadyOff);
    }
}
