package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.model.BPlayer;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static org.incendo.cloud.parser.standard.LongParser.longParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class AddCommand extends OptionCommand {
    public AddCommand(NobleWhitelist plugin) {
        super(b -> b.literal("add").permission("noblewhitelist.admin.add"), commands(plugin));
    }
    private static List<BaseCommand> commands(NobleWhitelist plugin) {
        SubCommand addOnline = new SubCommand(b -> b
                .literal("online")
                .handler(c -> {
                    int total = 0;
                    if (Bukkit.getOnlinePlayers().isEmpty()) sendMsg(c, MessageData.serverEmpty(true));
                    else {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (plugin.whitelistData().savePlayer(BPlayer.of(p))) total++;
                        }
                        if (total == 0) sendMsg(c, MessageData.serverActually(true));
                        else sendMsg(c, MessageData.serverAmount(true, total));
                    }
                })
        ) {
        };
        SubCommand addUuid = new SubCommand(b -> b
                .literal("uuid")
                .required("uuid", uuidParser())
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    plugin.whitelistData().getEntry(null, uuid, -1)
                            .ifPresentOrElse(d -> sendMsg(c, MessageData.playerAlready(uuid)),
                                    () -> {
                                        plugin.whitelistData().registerAndSave(null, uuid, -1);
                                        sendMsg(c, MessageData.playerAdded(uuid));
                                    }
                            );
                })
        ) {
        };
        SubCommand addName = new SubCommand(b -> b
                .literal("name")
                .required("name", stringParser())
                .handler(c -> {
                    final String name = c.get("name");
                    plugin.whitelistData().getEntry(name, null, -1)
                            .ifPresentOrElse(d -> sendMsg(c, MessageData.playerAlready(name)),
                                    () -> {
                                        plugin.whitelistData().registerAndSave(name, null, -1);
                                        sendMsg(c, MessageData.playerAdded(name));
                                    }
                            );
                })
        ) {
        };
        SubCommand addFull = new SubCommand(b -> b
                .literal("full")
                .required("name", stringParser())
                .required("uuid", uuidParser())
                .required("discordid", longParser())
                .handler(c -> {
                    final String name = c.get("name");
                    final UUID uuid = c.get("uuid");
                    final long id = c.get("discordid");
                    plugin.whitelistData().getEntry(name, uuid, id)
                            .ifPresentOrElse(d -> sendMsg(c, MessageData.playerAlready()),
                                    () -> {
                                        plugin.whitelistData().registerAndSave(name, uuid, id);
                                        sendMsg(c, MessageData.playerAdded());
                                    }
                            );
                })
        ) {
        };
        SubCommand addMe = new SubCommand(b -> b
                .literal("me")
                .handler(c -> {
                    Player player;
                    if (c.sender() instanceof Player p) {
                        player = p;
                    } else {
                        sendMsg(c, MessageData.onlyPlayer());
                        return;
                    }
                    plugin.whitelistData().getEntry(BPlayer.of(player))
                            .ifPresentOrElse(d -> sendMsg(c, MessageData.playerSelfAlready()),
                                    () -> {
                                        plugin.whitelistData().savePlayer(BPlayer.of(player));
                                        sendMsg(c, MessageData.playerSelfAdded());
                                    }
                            );
                })
        ) {
        };
        return List.of(addOnline, addUuid, addName, addFull, addMe);
    }
}
