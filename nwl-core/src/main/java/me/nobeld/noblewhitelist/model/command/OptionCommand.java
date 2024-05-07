package me.nobeld.noblewhitelist.model.command;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.List;
import java.util.function.Function;

public abstract class OptionCommand<T> extends BaseCommand<T> {
    private final List<BaseCommand<T>> subCommand;
    public OptionCommand(Function<Command.Builder<T>, Command.Builder<T>> builder, List<BaseCommand<T>> subCommand) {
        super(builder);
        this.subCommand = subCommand;
    }
    @Override
    public void register(CommandManager<T> mng, Command.Builder<T> base) {
        Command.Builder<T> sub = getBaseBuilder(base);

        this.getSubCommand().forEach(cmd -> {
            if (cmd instanceof OptionCommand<T> opt) {
                opt.register(mng, sub);
            } else {
                mng.command(cmd.getCommand(sub));
            }
        });
    }
    public Command.Builder<T> getBaseBuilder(Command.Builder<T> base) {
        return this.builder.apply(base);
    }
    public List<BaseCommand<T>> getSubCommand() {
        return subCommand;
    }
    @Override
    public Command.Builder<T> getCommand(Command.Builder<T> builder) {
        return null;
    }
}
