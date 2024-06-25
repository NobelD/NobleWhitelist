package me.nobeld.noblewhitelist.model.command;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

import java.util.function.Function;

public abstract class BaseCommand<T> {
    final Function<Command.Builder<T>, Command.Builder<T>> builder;

    public BaseCommand(Function<Command.Builder<T>, Command.Builder<T>> builder) {
        this.builder = builder;
    }

    public static <T> void sendMsg(CommandContext<? extends T> c, Component msg, Function<T, Audience> function) {
        mapper(c.sender(), function).sendMessage(msg);
    }

    public static <T> Audience mapper(T val, Function<T, Audience> function) {
        return function.apply(val);
    }

    public abstract void register(CommandManager<T> mng, Command.Builder<T> builder);

    public Command.Builder<? extends T> getCommand(Command.Builder<T> base) {
        return builder.apply(base);
    }
}
