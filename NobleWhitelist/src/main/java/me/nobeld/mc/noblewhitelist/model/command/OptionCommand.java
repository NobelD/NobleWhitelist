package me.nobeld.mc.noblewhitelist.model.command;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.List;
import java.util.function.Function;

public abstract class OptionCommand extends BaseCommand {
    private final List<BaseCommand> subCommand;
    public OptionCommand(Function<Command.Builder<CommandSender>, Command.Builder<CommandSender>> builder, List<BaseCommand> subCommand) {
        super(builder);
        this.subCommand = subCommand;
    }
    @Override
    public void register(CommandManager<CommandSender> mng, Command.Builder<CommandSender> base) {
        Command.Builder<CommandSender> sub = getBaseBuilder(base);

        this.getSubCommand().forEach(cmd -> {
            if (cmd instanceof OptionCommand opt) {
                opt.register(mng, sub);
            } else {
                mng.command(cmd.getCommand(sub));
            }
        });
    }
    public Command.Builder<CommandSender> getBaseBuilder(Command.Builder<CommandSender> base) {
        return this.builder.apply(base);
    }
    public List<BaseCommand> getSubCommand() {
        return subCommand;
    }
    @Override
    public Command.Builder<CommandSender> getCommand(Command.Builder<CommandSender> builder) {
        return null;
    }
}
