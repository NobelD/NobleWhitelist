package me.nobeld.minecraft.noblewhitelist.discord.model;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public interface CommandGeneral {
    @Nullable
    DefaultMemberPermissions getPermission();
    String getName();
    String getDescription();
    @Nullable
    List<CommandOption> getOptions();
    @Nullable
    List<SubCommand> getSubcommands();
    void onCommand(SlashCommandInteractionEvent event);
    default SlashCommandData build() {
        SlashCommandData data = Commands.slash(getName(), getDescription());
        if (getSubcommands() != null && !getSubcommands().isEmpty()) data.addSubcommands((getSubcommands().stream().map(SubCommand::build).collect(Collectors.toList())));
        if (getOptions() != null && !getOptions().isEmpty()) data.addOptions(getOptions().stream().map(CommandOption::build).collect(Collectors.toList()));
        return data;
    }
    default boolean notSubcommands() {
        return getSubcommands() == null || getSubcommands().isEmpty();
    }
}
