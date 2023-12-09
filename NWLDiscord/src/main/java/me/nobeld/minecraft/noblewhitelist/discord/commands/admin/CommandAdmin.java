package me.nobeld.minecraft.noblewhitelist.discord.commands.admin;

import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandGeneral;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandOption;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandAdmin implements CommandGeneral {
    @Nullable
    @Override
    public DefaultMemberPermissions getPermission() {
        if (ConfigData.get(ConfigData.serverManagePermission)) return DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER);
        else return null;
    }
    @Override
    public String getName() {
        return "wladmin";
    }
    @Override
    public String getDescription() {
        return "Admin commands for the whitelist management.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return null;
    }
    @Override
    public List<SubCommand> getSubcommands() {
        return List.of(new AdminAdd(), new AdminRemove(),
                new AdminFindPlayer(), new AdminFindUser(),
                new AdminLink(), new AdminUnLink(),
                new AdminOn(), new AdminOff(),
                new AdminToggle());
    }
    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
    }
}
