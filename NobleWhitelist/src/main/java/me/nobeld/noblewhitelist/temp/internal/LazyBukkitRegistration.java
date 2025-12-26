package me.nobeld.noblewhitelist.temp.internal;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.*;
import org.incendo.cloud.bukkit.internal.BukkitHelper;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.incendo.cloud.setting.ManagerSetting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class LazyBukkitRegistration<C> implements CommandRegistrationHandler<C> { // cloud bukkit registrator mirror
    private final Map<CommandComponent<C>, RegisteredCommandData<C>> registeredCommands = new HashMap<>();
    private final Set<String> recognizedAliases = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    private Map<String, org.bukkit.command.Command> bukkitCommands;
    private BukkitCommandManager<C> bukkitCommandManager;
    private CommandMap commandMap;

    protected LazyBukkitRegistration() {
    }

    final void initialize(final @NonNull BukkitCommandManager<C> bukkitCommandManager) throws ReflectiveOperationException {
        final Method getCommandMap = Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap");
        getCommandMap.setAccessible(true);
        this.commandMap = (CommandMap) getCommandMap.invoke(Bukkit.getServer());
        final Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommands.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<String, org.bukkit.command.Command> bukkitCommands =
                (Map<String, org.bukkit.command.Command>) knownCommands.get(this.commandMap);
        this.bukkitCommands = bukkitCommands;
        this.bukkitCommandManager = bukkitCommandManager;
    }

    @Override
    public final boolean registerCommand(final @NonNull Command<C> command) {
        /* We only care about the root command argument */
        final CommandComponent<C> component = command.rootComponent();
        final String label = component.name();
        final String namespacedLabel = BukkitHelper.namespacedLabel(this.bukkitCommandManager, label);

        final List<String> aliases = new ArrayList<>(component.alternativeAliases());

        final LazyBukkitCommand<C> bukkitCommand = new LazyBukkitCommand<>(
                label,
                aliases,
                command,
                component,
                this.bukkitCommandManager
        );

        if (this.bukkitCommandManager.settings().get(ManagerSetting.OVERRIDE_EXISTING_COMMANDS)) {
            this.bukkitCommands.remove(label);
            aliases.forEach(this.bukkitCommands::remove);
        }

        final Set<String> newAliases = new HashSet<>();

        for (final String alias : aliases) {
            final String namespacedAlias = BukkitHelper.namespacedLabel(this.bukkitCommandManager, alias);
            newAliases.add(namespacedAlias);
            if (!this.bukkitCommandOrAliasExists(alias)) {
                newAliases.add(alias);
            }
        }

        if (!this.bukkitCommandExists(label)) {
            newAliases.add(label);
        }
        newAliases.add(namespacedLabel);

        this.commandMap.register(
                label,
                this.bukkitCommandManager.owningPlugin().getName().toLowerCase(Locale.ROOT),
                bukkitCommand
        );

        this.recognizedAliases.addAll(newAliases);
        /* no aliases
        if (this.bukkitCommandManager.splitAliases()) {
            newAliases.forEach(alias -> this.registerInternal(alias, command, bukkitCommand));
        }
         */

        this.registeredCommands.put(component, new RegisteredCommandData<>(bukkitCommand, newAliases));
        return true;
    }

    @Override
    public final void unregisterRootCommand(
            final @NonNull CommandComponent<C> component
    ) {
        final RegisteredCommandData<C> registeredCommand = this.registeredCommands.get(component);
        if (registeredCommand == null) {
            return;
        }
        registeredCommand.bukkit.disable();

        final Set<String> registeredAliases = registeredCommand.recognizedAliases;

        for (final String alias : registeredAliases) {
            this.bukkitCommands.remove(alias);
        }

        /* no aliases
        this.recognizedAliases.removeAll(registeredAliases);
        if (this.bukkitCommandManager.splitAliases()) {
            registeredAliases.forEach(this::unregisterAlias);
        }
         */

        this.registeredCommands.remove(component);

        if (this.bukkitCommandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            // Once the command has been unregistered, we need to refresh the command list for all online players.
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        }
    }

    public boolean isRecognized(final @NonNull String alias) {
        return this.recognizedAliases.contains(alias);
    }

    private boolean bukkitCommandExists(final String commandLabel) {
        final org.bukkit.command.Command existingCommand = this.bukkitCommands.get(commandLabel);
        if (existingCommand == null) {
            return false;
        }
        if (existingCommand instanceof PluginIdentifiableCommand) {
            return existingCommand.getLabel().equals(commandLabel)
                    && !((PluginIdentifiableCommand) existingCommand).getPlugin().getName()
                    .equalsIgnoreCase(this.bukkitCommandManager.owningPlugin().getName());
        }
        return existingCommand.getLabel().equals(commandLabel);
    }

    private boolean bukkitCommandOrAliasExists(final String commandLabel) {
        final org.bukkit.command.Command command = this.bukkitCommands.get(commandLabel);
        if (command instanceof PluginIdentifiableCommand) {
            return !((PluginIdentifiableCommand) command).getPlugin().getName()
                    .equalsIgnoreCase(this.bukkitCommandManager.owningPlugin().getName());
        }
        return command != null;
    }

    private static final class RegisteredCommandData<C> {
        private final LazyBukkitCommand<C> bukkit;
        private final Set<String> recognizedAliases;

        private RegisteredCommandData(
                final LazyBukkitCommand<C> bukkit,
                final Set<String> recognizedAliases
        ) {
            this.bukkit = bukkit;
            final Set<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            treeSet.addAll(recognizedAliases);
            this.recognizedAliases = Collections.unmodifiableSet(treeSet);
        }
    }
}
