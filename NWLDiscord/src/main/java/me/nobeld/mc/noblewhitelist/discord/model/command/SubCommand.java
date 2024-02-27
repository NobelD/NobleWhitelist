package me.nobeld.mc.noblewhitelist.discord.model.command;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;

import java.util.function.Function;

public abstract class SubCommand extends BaseCommand {
    public SubCommand(Function<Command.Builder<JDAInteraction>, Command.Builder<JDAInteraction>> builder) {
        super(builder);
    }
    @Override
    public void register(CommandManager<JDAInteraction> mng, Command.Builder<JDAInteraction> builder) {
        mng.command(getCommand(builder));
    }
}
