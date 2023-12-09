package me.nobeld.minecraft.noblewhitelist.discord.commands.admin;

import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandOption;
import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.Optional;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;

public class AdminFindUser implements SubCommand {
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
        return "user";
    }
    @Override
    public String getDescription() {
        return "Get the accounts registered from an user.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return List.of(new CommandOption(OptionType.USER, "user", "User to find their accounts", true));
    }
    @Override
    public void onCommand(InteractResult event) {
        if (event.getManager().notRole(event.getMember(), ConfigData.CommandsRole.adminUser)) {
            DiscordUtil.replyMessage(event, MessageData.Error.noPermission);
            return;
        }
        if (event.getManager().notChannel(event, ConfigData.CommandsChannel.adminUser)) {
            DiscordUtil.replyMessage(event, MessageData.Error.incorrectChannel);
            return;
        }
        long userid = event.getOption("user").map(o -> o.getAsUser().getIdLong()).orElse(-1L);

        Optional<PlayerWhitelisted> data = getPlugin().api().getWlData().getData(null, null, userid);
        if (data.isEmpty()) {
            DiscordUtil.replyMessage(event, MessageData.Error.userNoAccounts);
        } else
            DiscordUtil.replyMessage(event, MessageData.Command.userAccounts, () -> MessageData.baseHolder(data.get()));
    }
}
