package me.nobeld.noblewhitelist.command;

import com.google.common.cache.CacheBuilder;
import io.leangen.geantyref.TypeToken;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.command.admin.*;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.temp.CustomStringParser;
import me.nobeld.noblewhitelist.temp.internal.LazyBukkitManager;
import me.nobeld.noblewhitelist.util.AdventureUtil;
import me.nobeld.noblewhitelist.util.ServerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.brigadier.CloudBrigadierManager;
import org.incendo.cloud.brigadier.argument.BrigadierMappingBuilder;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.processors.cache.GuavaCache;
import org.incendo.cloud.processors.confirmation.ConfirmationConfiguration;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

public class NWlCommand {
    private final NobleWhitelist plugin;
    private CommandManager<CommandSender> manager;
    private MinecraftHelp<CommandSender> minecraftHelp;
    private ConfirmationManager<CommandSender> confirmationManager;
    public NWlCommand(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    public boolean isCapable() {
        return ServerUtil.hasPaper() || !ServerUtil.matchVersion(19);
    }
    public void start() {
        LegacyPaperCommandManager<CommandSender> paper;
        if (isCapable()) {
            manager = paper = LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
        } else {
            plugin.getAdventure().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><yellow>Unable to load brigadier, using lazy fallback instead, commands may not work as expected."));
            paper = null;
            manager = LazyBukkitManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
        }
        if (paper != null) {
            try {
                paper.registerBrigadier();
            } catch (Throwable e) {
                String re;
                if (paper.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
                    re = "Cannot load the native brigadier even when is allowed.";
                } else {
                    re = "Cannot load the brigadier manager.";
                }
                throw new RuntimeException(re, e);
            }
            if (paper.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                paper.registerAsynchronousCompletions();
            }
        }

        MinecraftExceptionHandler.create(NobleWhitelist.adv()::senderAudience)
                .defaultInvalidSyntaxHandler()
                .defaultInvalidSenderHandler()
                .defaultNoPermissionHandler()
                .defaultArgumentParsingHandler()
                .defaultCommandExecutionHandler()
                .registerTo(manager);

        this.minecraftHelp = MinecraftHelp.<CommandSender>builder()
                .commandManager(manager)
                .audienceProvider(NobleWhitelist.adv()::senderAudience)
                .commandPrefix("/nwl help")
                .build();

        ConfirmationConfiguration<CommandSender> configuration = ConfirmationConfiguration.<CommandSender>builder()
                .cache(GuavaCache.of(CacheBuilder.newBuilder().build()))
                .noPendingCommandNotifier(c -> NobleWhitelist.adv().senderAudience(c).sendMessage(MessageData.confirmationNoMore()))
                .confirmationRequiredNotifier((s, c) -> {
                    if (c.command().rootComponent().name().equals("clear")) {
                        sendMsg(s, MessageData.clearSug1());
                        sendMsg(s, MessageData.clearSug2());
                    } else sendMsg(s, MessageData.confirmationRequired());
                })
                .build();

        confirmationManager = ConfirmationManager.confirmationManager(configuration);
        manager.registerCommandPostProcessor(confirmationManager.createPostprocessor());
        if (paper != null)reflect(paper.brigadierManager());
        register();
    }
    // TODO temporal, use internals for custom brigadier parser instead
    @SuppressWarnings({ "unchecked", "DataFlowIssue" })
    private void reflect(CloudBrigadierManager<CommandSender, ?> manager) {
        try {
            Object val = manager.mappings()
                    .mapping(StringParser.class)
                    .mapper()
                    .apply(new StringParser<>(StringParser.StringMode.QUOTED));
            Method method = BrigadierMappingBuilder.class.getMethod("to", Function.class);
            method.setAccessible(true);
            manager.registerMapping(
                    new TypeToken<CustomStringParser<CommandSender>>() {
                    }, b -> {
                        try {
                            Function<?, ?> function = e -> val;
                            method.invoke(b.cloudSuggestions(), function);
                        } catch (Exception e) {
                            plugin.getLogger().log(Level.WARNING, "Unable to use custom brigadier mapping", e);
                        }
                    }
            );
        } catch (Throwable e) {
            plugin.getLogger().log(Level.WARNING, "Unable to use custom brigadier mapping, using normal mapping instead...", e);
        }
    }
    public @NotNull MinecraftHelp<CommandSender> minecraftHelp() {
        return this.minecraftHelp;
    }
    public void minecraftHelp(final @NotNull MinecraftHelp<CommandSender> minecraftHelp) {
        this.minecraftHelp = minecraftHelp;
    }
    public void sendMsg(CommandContext<CommandSender> ctx, Component msg) {
        NobleWhitelist.adv().senderAudience(ctx.sender()).sendMessage(msg);
    }
    public void sendMsg(CommandSender sender, Component msg) {
        NobleWhitelist.adv().senderAudience(sender).sendMessage(msg);
    }
    private void register() {
        final Command.Builder<CommandSender> builder = this.manager
                .commandBuilder("nwhitelist", Description.of("Command for the whitelist management"), "nwl", "noblewl", "nwhitelist")
                .permission("noblewhitelist.admin");

        this.manager.command(builder.literal("confirm", Description.of("Used to confirm an important command"))
                .permission("noblewhitelist.admin.confirm")
                .handler(this.confirmationManager.createExecutionHandler()));

        List<BaseCommand> commands = new ArrayList<>();
        commands.add(new AddCommand(plugin));
        commands.add(new RemoveCommand(plugin));
        commands.add(new ToggleCommand(plugin));
        commands.add(new FindCommand(plugin));
        commands.add(new CheckingCommand(plugin));
        commands.addAll(ManageCommand.commands(plugin));
        commands.addAll(WhitelistCommand.commands(plugin));

        commands.forEach(c -> c.register(manager, builder));
    }
}
