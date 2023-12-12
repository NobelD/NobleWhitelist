package me.nobeld.minecraft.noblewhitelist.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandTree;
import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.config.ConfigFile;
import me.nobeld.minecraft.noblewhitelist.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.log;
import static me.nobeld.minecraft.noblewhitelist.config.ConfigFile.reloadConfig;
import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.asLegacy;

public class NWlCommand {
    private final NobleWhitelist plugin;
    private BukkitCommandManager<CommandSender> manager;
    private final CommandConfirmationManager<CommandSender> confirmationManager;
    public NWlCommand(NobleWhitelist plugin) {
        this.plugin = plugin;
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build();
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            this.manager = new PaperCommandManager<>(plugin, executionCoordinatorFunction, mapperFunction, mapperFunction);
        } catch (final Exception e) {
            log(Level.SEVERE, "Failed to initialize the command this.manager");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
        this.manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
                FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()));

        if (this.manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.manager.registerBrigadier();
        }
        if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            ((PaperCommandManager<CommandSender>) this.manager).registerAsynchronousCompletions();
        }

        this.confirmationManager = new CommandConfirmationManager<>(10L, TimeUnit.SECONDS, c -> {
            if(c.getCommandContext().getCurrentArgument() != null && c.getCommandContext().getCurrentArgument().getName().equals("clear")) {
                sendMsg(c.getCommandContext(), MessageData.clearSug1());
                sendMsg(c.getCommandContext(), MessageData.clearSug2());
            } else sendMsg(c.getCommandContext(), MessageData.confirmationRequired());
        },
                s -> s.sendMessage(asLegacy(MessageData.confirmationNoMore())));
        this.confirmationManager.registerConfirmationProcessor(this.manager);

        new MinecraftExceptionHandler<CommandSender>()
                .withInvalidSyntaxHandler()
                .withInvalidSenderHandler()
                .withNoPermissionHandler()
                .withArgumentParsingHandler()
                .withCommandExecutionHandler()
                .apply(this.manager, plugin.adventure()::sender);

        this.constructCommands();
    }
    public void sendMsg(CommandContext<CommandSender> ctx, Component msg) {
        plugin.adventure().sender(ctx.getSender()).sendMessage(msg);
    }
    private void toggleStatus(CommandContext<CommandSender> ctx, boolean activate) {
        boolean actually = ConfigFile.getConfig(ConfigFile.whitelistActive);

        if (activate == actually) {
            sendMsg(ctx, MessageData.whitelistAlready(activate));
        } else {
            ConfigFile.setConfig(ConfigFile.whitelistActive, activate);
            sendMsg(ctx, MessageData.whitelistChanged(activate));
        }
    }
    private void constructCommands() {
        final Command.Builder<CommandSender> builder = this.manager.commandBuilder("nwhitelist", "nwl", "noblewl", "nwhitelist")
                .meta(CommandMeta.DESCRIPTION, "Command for the whitelist management")
                .permission("noblewhitelist.admin");

        this.manager.command(builder.literal("confirm")
                .meta(CommandMeta.DESCRIPTION, "Used to confirm an important command")
                .handler(this.confirmationManager.createConfirmationExecutionHandler()));

        final Command.Builder<CommandSender> builderAdd = builder.literal("add");
        this.manager.command(builderAdd
                .literal("online")
                .handler(c -> {
                    int total = 0;
                    if (Bukkit.getOnlinePlayers().isEmpty()) sendMsg(c, MessageData.serverEmpty(true));
                    else {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (plugin.whitelistData().addPlayer(p)) total++;
                        }
                        if (total == 0) sendMsg(c, MessageData.serverActually(true));
                        else sendMsg(c, MessageData.serverAmount(true, total));
                    }
                })
        );

        this.manager.command(builderAdd
                .literal("uuid")
                .argument(UUIDArgument.of("uuid"))
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    plugin.whitelistData().getData(null, uuid, -1)
                            .ifPresentOrElse(d -> sendMsg(c, MessageData.playerAlready(uuid)), () -> {
                                        plugin.whitelistData().register(null, uuid, -1);
                                        sendMsg(c, MessageData.playerAdded(uuid));
                            }
                            );
                })
        );

        this.manager.command(builderAdd
                .literal("name")
                .argument(StringArgument.of("name"))
                .handler(c -> {
                    final String name = c.get("name");
                    plugin.whitelistData().getData(name, null, -1)
                            .ifPresentOrElse(d -> sendMsg(c, MessageData.playerAlready(name)), () -> {
                                plugin.whitelistData().register(name, null, -1);
                                sendMsg(c, MessageData.playerAdded(name));
                            }
                            );
                })
        );

        this.manager.command(builderAdd
                .literal("full")
                .argument(StringArgument.of("name"))
                .argument(UUIDArgument.of("uuid"))
                .argument(LongArgument.of("discordid"))
                .handler(c -> {
                    final String name = c.get("name");
                    final UUID uuid = c.get("uuid");
                    final long id = c.get("discordid");
                    plugin.whitelistData().getData(name, uuid, id)
                            .ifPresentOrElse(d -> sendMsg(c, MessageData.playerAlready()), () -> {
                                        plugin.whitelistData().register(name, uuid, id);
                                        sendMsg(c, MessageData.playerAdded());
                                    }
                            );
                })
        );

        final Command.Builder<CommandSender> builderRemove = builder.literal("remove");
        this.manager.command(builderRemove
                .literal("online")
                .handler(c -> {
                    int total = 0;
                    if (Bukkit.getOnlinePlayers().isEmpty()) sendMsg(c, MessageData.serverEmpty(false));
                    else {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (plugin.whitelistData().deleteUser(p)) total++;
                        }
                        if (total == 0) sendMsg(c, MessageData.serverActually(false));
                        else sendMsg(c, MessageData.serverAmount(false, total));
                    }
                })
        );

        this.manager.command(builderRemove
                .literal("uuid")
                .argument(UUIDArgument.of("uuid"))
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    if (plugin.whitelistData().deleteUser(null, uuid))
                        sendMsg(c, MessageData.playerRemoved(uuid));
                    else sendMsg(c, MessageData.playerNotFound(uuid));
                })
        );

        this.manager.command(builderRemove
                .literal("name")
                .argument(StringArgument.of("name"))
                .handler(c -> {
                    final String name = c.get("name");
                    if (plugin.whitelistData().deleteUser(name, null))
                        sendMsg(c, MessageData.playerRemoved(name));
                    else sendMsg(c, MessageData.playerNotFound(name));
                })
        );

        final Command.Builder<CommandSender> builderToggle = builder.literal("toggle");
        this.manager.command(builderToggle
                .literal("uuid")
                .argument(UUIDArgument.of("uuid"))
                .argument(BooleanArgument.of("toggle"))
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    final boolean toggle = c.get("toggle");
                    plugin.whitelistData().getData(null, uuid, -1)
                            .ifPresentOrElse(d -> {
                                if (d.isWhitelisted() == toggle)
                                    sendMsg(c, MessageData.playerToggledAlready(toggle));
                                else {
                                    plugin.whitelistData().toggleJoinUser(d, toggle);
                                    sendMsg(c, MessageData.playerToggled(uuid, toggle));
                                }
                                }, () -> sendMsg(c, MessageData.playerNotFound(uuid))
                            );
                })
        );

        this.manager.command(builderToggle
                .literal("name")
                .argument(StringArgument.of("name"))
                .argument(BooleanArgument.of("toggle"))
                .handler(c -> {
                    final String name = c.get("name");
                    final boolean toggle = c.get("toggle");
                    plugin.whitelistData().getData(name, null, -1)
                            .ifPresentOrElse(d -> {
                                if (d.isWhitelisted() == toggle)
                                    sendMsg(c, MessageData.playerToggledAlready(toggle));
                                else {
                                    plugin.whitelistData().toggleJoinUser(d, toggle);
                                    sendMsg(c, MessageData.playerToggled(name, toggle));
                                }
                                    },
                                    () -> sendMsg(c, MessageData.playerNotFound(name))
                            );
                })
        );

        this.manager.command(builderToggle
                .literal("discord")
                .argument(LongArgument.of("discord"))
                .argument(BooleanArgument.of("toggle"))
                .handler(c -> {
                    final long id = c.get("discord");
                    final boolean toggle = c.get("toggle");
                    plugin.whitelistData().getData(null, null, id)
                            .ifPresentOrElse(d -> {
                                if (d.isWhitelisted() == toggle)
                                    sendMsg(c, MessageData.playerToggledAlready(toggle));
                                else {
                                    plugin.whitelistData().toggleJoinUser(d, toggle);
                                    sendMsg(c, MessageData.playerToggled(id, toggle));
                                }
                                    },
                                    () -> sendMsg(c, MessageData.playerNotFound(id))
                            );
                })
        );

        this.manager.command(builder.literal("list")
                .argument(IntegerArgument.optional("page"))
                .handler(c -> {
                    int page = c.getOrDefault("page", 1);
                    List<PlayerWhitelisted> list = plugin.getStorageInst().listIndex(page);
                    if (list != null && !list.isEmpty()) {
                        sendMsg(c, MessageData.listPage(page));
                        list.forEach(w -> sendMsg(c, MessageData.listString(w)));
                    } else if (page > 1) sendMsg(c, MessageData.listEmpty(page));
                    else sendMsg(c, MessageData.whitelistEmpty());
                })
        );
        this.manager.command(builder.literal("list")
                .literal("clear")
                .meta(CommandConfirmationManager.META_CONFIRMATION_REQUIRED, true)
                .handler(c -> {
                    if (!plugin.getStorageInst().clear()) sendMsg(c, MessageData.whitelistAlreadyEmpty());
                    else sendMsg(c, MessageData.whitelistCleared());
                })
        );

        this.manager.command(builder.literal("on")
                .handler(c -> toggleStatus(c, true))
        );
        this.manager.command(builder.literal("off")
                .handler(c -> toggleStatus(c, false))
        );
        this.manager.command(builder.literal("reload")
                .handler(c -> {
                    plugin.getStorageInst().reload();
                    reloadConfig();
                    sendMsg(c, MessageData.reload());
                })
        );
        this.manager.command(builder.literal("status")
                .handler(c -> {
                    sendMsg(c, MessageData.statusHeader());
                    sendMsg(c, MessageData.statusVersion(plugin.getUptChecker().version));
                    sendMsg(c, MessageData.statusWhitelistSize(plugin.getStorageInst().getTotal()));
                    sendMsg(c, MessageData.statusWhitelistActive(ConfigFile.getConfig(ConfigFile.whitelistActive)));
                    sendMsg(c, MessageData.statusNameCheck(ConfigFile.checkName()));
                    sendMsg(c, MessageData.statusUuidCheck(ConfigFile.checkUUID()));
                    sendMsg(c, MessageData.statusPermCheck(ConfigFile.checkPerm()));
                    sendMsg(c, MessageData.statusStorageType(plugin));
                })
        );

        final Command.Builder<CommandSender> builderFind = builder.literal("find");
        this.manager.command(builderFind
                .literal("uuid")
                .argument(UUIDArgument.of("uuid"))
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    Optional<PlayerWhitelisted> d = plugin.whitelistData().getData(null, uuid, -1);

                    if (d.isEmpty()) {
                        sendMsg(c, MessageData.playerNotFound(uuid));
                    } else {
                        sendMsg(c, MessageData.playerAbout(d.get()));
                        sendMsg(c, MessageData.playerAboutName(d.get()));
                        sendMsg(c, MessageData.playerAboutUuid(d.get()));
                        sendMsg(c, MessageData.playerAboutUser(d.get()));
                        sendMsg(c, MessageData.playerAboutJoin(d.get()));
                    }
                })
        );
        this.manager.command(builderFind
                .literal("name")
                .argument(StringArgument.of("name"))
                .handler(c -> {
                    final String name = c.get("name");
                    Optional<PlayerWhitelisted> d = plugin.whitelistData().getData(name, null, -1);

                    if (d.isEmpty()) {
                        sendMsg(c, MessageData.playerNotFound(name));
                    } else {
                        sendMsg(c, MessageData.playerAbout(d.get()));
                        sendMsg(c, MessageData.playerAboutName(d.get()));
                        sendMsg(c, MessageData.playerAboutUuid(d.get()));
                        sendMsg(c, MessageData.playerAboutUser(d.get()));
                        sendMsg(c, MessageData.playerAboutJoin(d.get()));
                    }
                })
        );
        this.manager.command(builderFind
                .literal("discord")
                .argument(LongArgument.of("discord"))
                .handler(c -> {
                    final long id = c.get("discord");
                    Optional<PlayerWhitelisted> d = plugin.whitelistData().getData(null, null, id);

                    if (d.isEmpty()) {
                        sendMsg(c, MessageData.playerNotFound(id));
                    } else {
                        sendMsg(c, MessageData.playerAbout(d.get()));
                        sendMsg(c, MessageData.playerAboutName(d.get()));
                        sendMsg(c, MessageData.playerAboutUuid(d.get()));
                        sendMsg(c, MessageData.playerAboutUser(d.get()));
                        sendMsg(c, MessageData.playerAboutJoin(d.get()));
                    }
                })
        );
    }
}
