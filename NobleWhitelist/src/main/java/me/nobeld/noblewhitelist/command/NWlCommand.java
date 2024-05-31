package me.nobeld.noblewhitelist.command;

import com.google.common.cache.CacheBuilder;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.command.admin.*;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.processors.cache.GuavaCache;
import org.incendo.cloud.processors.confirmation.ConfirmationConfiguration;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NWlCommand {
    private final NobleWhitelist plugin;
    private final LegacyPaperCommandManager<CommandSender> manager;
    private MinecraftHelp<CommandSender> minecraftHelp;
    private final ConfirmationManager<CommandSender> confirmationManager;
    public NWlCommand(NobleWhitelist plugin) {
        this.plugin = plugin;
        manager = new LegacyPaperCommandManager<>(
                plugin,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );
        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier();
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }

        MinecraftExceptionHandler.create(NobleWhitelist.adv().adventure()::sender)
                .defaultInvalidSyntaxHandler()
                .defaultInvalidSenderHandler()
                .defaultNoPermissionHandler()
                .defaultArgumentParsingHandler()
                .defaultCommandExecutionHandler()
                .registerTo(manager);

        this.minecraftHelp = MinecraftHelp.<CommandSender>builder()
                .commandManager(manager)
                .audienceProvider(NobleWhitelist.adv().adventure()::sender)
                .commandPrefix("/nwl help")
                .build();

        ConfirmationConfiguration<CommandSender> configuration = ConfirmationConfiguration.<CommandSender>builder()
                .cache(GuavaCache.of(CacheBuilder.newBuilder().build()))
                .noPendingCommandNotifier(c -> NobleWhitelist.adv().adventure().sender(c).sendMessage(MessageData.confirmationNoMore()))
                .confirmationRequiredNotifier((s, c) -> {
                    if (c.command().rootComponent().name().equals("clear")) {
                        sendMsg(s, MessageData.clearSug1());
                        sendMsg(s, MessageData.clearSug2());
                    } else sendMsg(s, MessageData.confirmationRequired());
                })
        .build();

        confirmationManager = ConfirmationManager.confirmationManager(configuration);
        manager.registerCommandPostProcessor(confirmationManager.createPostprocessor());
        start();
    }
    public @NotNull MinecraftHelp<CommandSender> minecraftHelp() {
        return this.minecraftHelp;
    }
    public void minecraftHelp(final @NotNull MinecraftHelp<CommandSender> minecraftHelp) {
        this.minecraftHelp = minecraftHelp;
    }
    public void sendMsg(CommandContext<CommandSender> ctx, Component msg) {
        NobleWhitelist.adv().adventure().sender(ctx.sender()).sendMessage(msg);
    }
    public void sendMsg(CommandSender sender, Component msg) {
        NobleWhitelist.adv().adventure().sender(sender).sendMessage(msg);
    }
    private void start() {
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
