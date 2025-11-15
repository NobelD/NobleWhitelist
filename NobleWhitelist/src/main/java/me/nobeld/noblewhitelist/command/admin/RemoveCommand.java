package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.model.BPlayer;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.language.MessageData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static me.nobeld.noblewhitelist.temp.CustomStringParser.customStringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class RemoveCommand extends OptionCommand {
    public RemoveCommand(NobleWhitelist plugin) {
        super(b -> b.literal("remove").permission("noblewhitelist.admin.remove"), commands(plugin));
    }
    private static List<BaseCommand> commands(NobleWhitelist plugin) {
        SubCommand removeOnline = new SubCommand(b -> b
                .literal("online")
                .handler(c -> {
                    int total = 0;
                    if (Bukkit.getOnlinePlayers().isEmpty()) sendMsg(c, MessageData.serverEmpty(false));
                    else {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (plugin.whitelistData().deleteUser(BPlayer.of(p))) total++;
                        }
                        if (total == 0) sendMsg(c, MessageData.serverActually(false));
                        else sendMsg(c, MessageData.serverAmount(false, total));
                    }
                })) {};
        SubCommand removeUuid = new SubCommand(b -> b
                .literal("uuid")
                .required("uuid", uuidParser())
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    if (plugin.whitelistData().deleteUser(null, uuid))
                        sendMsg(c, MessageData.playerRemoved(uuid));
                    else sendMsg(c, MessageData.playerNotFound(uuid));
                })
        ) {};
        SubCommand removeName = new SubCommand(b -> b
                .literal("name")
                .required("name", customStringParser())
                .handler(c -> {
                    final String name = c.get("name");
                    if (plugin.whitelistData().deleteUser(name, null))
                        sendMsg(c, MessageData.playerRemoved(name));
                    else sendMsg(c, MessageData.playerNotFound(name));
                })
        ) {};
        SubCommand removeMe = new SubCommand(b -> b
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
                            .ifPresentOrElse(d -> {
                                        plugin.whitelistData().deleteUser(BPlayer.of(player));
                                        sendMsg(c, MessageData.playerSelfRemoved());
                                    }, () -> sendMsg(c, MessageData.playerSelfNotFound())
                            );
                })
        ) {};
        return List.of(removeOnline, removeUuid, removeName, removeMe);
    }
}
