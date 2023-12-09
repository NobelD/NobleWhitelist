package me.nobeld.minecraft.noblewhitelist.discord.commands.basic;

import me.nobeld.minecraft.noblewhitelist.discord.model.CommandGeneral;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandOption;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandBasic implements CommandGeneral {
    @Nullable
    @Override
    public DefaultMemberPermissions getPermission() {
        return null;
    }
    @Override
    public String getName() {
        return "whitelist";
    }
    @Override
    public String getDescription() {
        return "Basic command for users to self manage the whitelist.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return null;
    }
    @Override
    public List<SubCommand> getSubcommands() {
        return List.of(new BasicAdd(), new BasicRemove(), new BasicAccounts(), new BasicLink());
    }
    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
    }
}
