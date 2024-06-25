package me.nobeld.noblewhitelist.model.command;

import me.nobeld.noblewhitelist.NobleWhitelist;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

import java.util.function.Function;

public class BaseCommand {
    final Function<Command.Builder<CommandSender>, Command.Builder<CommandSender>> builder;
    public BaseCommand(Function<Command.Builder<CommandSender>, Command.Builder<CommandSender>> builder) {
        this.builder = builder;
    }
    public static void sendMsg(CommandContext<? extends CommandSender> c, Component msg) {
        CommandSender sender = c.sender();
        NobleWhitelist.adv().adventure().sender(sender).sendMessage(msg);
    }
    public void register(CommandManager<CommandSender> mng, Command.Builder<CommandSender> builder) {
        mng.command(getCommand(builder));
    }
    public Command.Builder<? extends CommandSender> getCommand(Command.Builder<CommandSender> base) {
        return builder.apply(base);
    }
}
