package me.nobeld.noblewhitelist.model.command;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.function.Function;

public class SubCommand<T> extends BaseCommand<T> {
    public SubCommand(Function<Command.Builder<T>, Command.Builder<T>> builder) {
        super(builder);
    }

    @Override
    public void register(CommandManager<T> mng, Command.Builder<T> builder) {
        mng.command(getCommand(builder));
    }
}
