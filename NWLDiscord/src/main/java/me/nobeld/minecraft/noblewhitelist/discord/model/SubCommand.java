package me.nobeld.minecraft.noblewhitelist.discord.model;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;
import java.util.stream.Collectors;

public interface SubCommand {
    boolean isEphemeral();
    boolean isDefer();
    String getName();
    String getDescription();
    List<CommandOption> getOptions();
    void onCommand(InteractResult result);
    default SubcommandData build() {
        if (getOptions() != null && !getOptions().isEmpty()) return new SubcommandData(getName(), getDescription()).addOptions(getOptions().stream().map(CommandOption::build).collect(Collectors.toList()));
        else return new SubcommandData(getName(), getDescription());
    }
}
