package me.nobeld.mc.noblewhitelist.model.command;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.function.Function;

public abstract class SubCommand extends BaseCommand {
    public SubCommand(Function<Command.Builder<CommandSender>, Command.Builder<CommandSender>> builder) {
        super(builder);
    }
    @Override
    public void register(CommandManager<CommandSender> mng, Command.Builder<CommandSender> builder) {
        mng.command(getCommand(builder));
    }
}
