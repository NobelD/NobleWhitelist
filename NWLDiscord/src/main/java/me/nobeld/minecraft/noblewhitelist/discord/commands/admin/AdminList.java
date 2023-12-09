package me.nobeld.minecraft.noblewhitelist.discord.commands.admin;

import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandOption;
import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;

public class AdminList implements SubCommand {
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
        return "list";
    }
    @Override
    public String getDescription() {
        return "Get a list of players from the whitelist.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return List.of(new CommandOption(OptionType.INTEGER, "page", "Page of the list.", false));
    }
    @Override
    public void onCommand(InteractResult event) {
        if (event.getManager().notRole(event.getMember(), ConfigData.CommandsRole.adminList)) {
            DiscordUtil.replyMessage(event, MessageData.Error.noPermission);
            return;
        }
        if (event.getManager().notChannel(event, ConfigData.CommandsChannel.adminList)) {
            DiscordUtil.replyMessage(event, MessageData.Error.incorrectChannel);
            return;
        }
        int page = event.getOption("page").map(OptionMapping::getAsInt).orElse(1);

        List<PlayerWhitelisted> list = getPlugin().api().getIndex(page);
        if (list != null && !list.isEmpty()) {
            event.reply("List of players. Page: " + page).setEphemeral(true).queue();
        } else if (page > 1) event.reply("This page is empty: " + page).setEphemeral(true).queue();
        else event.reply("The whitelist is empty.").setEphemeral(true).queue();
    }
}
