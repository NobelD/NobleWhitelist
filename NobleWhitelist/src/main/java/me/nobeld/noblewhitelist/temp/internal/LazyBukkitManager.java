package me.nobeld.noblewhitelist.temp.internal;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;

public class LazyBukkitManager<C> extends BukkitCommandManager<C> {
    protected LazyBukkitManager(@NonNull Plugin owningPlugin,
                                @NonNull ExecutionCoordinator<C> commandExecutionCoordinator,
                                @NonNull SenderMapper<CommandSender, C> senderMapper) throws InitializationException {
        super(owningPlugin, commandExecutionCoordinator, senderMapper);
        commandRegistrationHandler(new LazyBukkitRegistration<>());
        try {
            ((LazyBukkitRegistration) commandRegistrationHandler()).initialize(this);
        } catch (ReflectiveOperationException e) {
            throw new InitializationException("Failed to initialize command registration handler", e);
        }
    }

    public static @NonNull LazyBukkitManager<@NonNull CommandSender> createNative(
            final @NonNull Plugin owningPlugin,
            final @NonNull ExecutionCoordinator<CommandSender> commandExecutionCoordinator
    ) throws InitializationException {
        return new LazyBukkitManager<>(
                owningPlugin,
                commandExecutionCoordinator,
                SenderMapper.identity()
        );
    }

}
