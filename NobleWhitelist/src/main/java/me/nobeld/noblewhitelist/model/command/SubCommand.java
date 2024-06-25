package me.nobeld.noblewhitelist.model.command;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;

import java.util.function.Function;

public abstract class SubCommand extends BaseCommand {
    public SubCommand(Function<Command.Builder<CommandSender>, Command.Builder<CommandSender>> builder) {
        super(builder);
    }
}
