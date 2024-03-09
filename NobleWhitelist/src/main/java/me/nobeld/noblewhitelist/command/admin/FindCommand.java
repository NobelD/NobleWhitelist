package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.context.CommandContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.incendo.cloud.parser.standard.LongParser.longParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class FindCommand extends OptionCommand {
    public FindCommand(NobleWhitelist plugin) {
        super(b -> b.literal("find").permission("noblewhitelist.admin.find"), commands(plugin));
    }
    private static void playerAbout(CommandContext<CommandSender> c, WhitelistEntry d) {
        sendMsg(c, MessageData.playerAbout(d));
        sendMsg(c, MessageData.playerAboutName(d));
        sendMsg(c, MessageData.playerAboutUuid(d));
        sendMsg(c, MessageData.playerAboutUser(d));
        sendMsg(c, MessageData.playerAboutJoin(d));
    }
    private static List<BaseCommand> commands(NobleWhitelist plugin) {
        SubCommand findUuid = new SubCommand(b -> b
                .literal("uuid")
                .required("uuid", uuidParser())
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    Optional<WhitelistEntry> d = plugin.whitelistData().getEntry(null, uuid, -1);

                    if (d.isEmpty()) {
                        sendMsg(c, MessageData.playerNotFound(uuid));
                    } else {
                        playerAbout(c, d.get());
                    }
                })
        ) {
        };
        SubCommand findName = new SubCommand(b -> b
                .literal("name")
                .required("name", stringParser())
                .handler(c -> {
                    final String name = c.get("name");
                    Optional<WhitelistEntry> d = plugin.whitelistData().getEntry(name, null, -1);

                    if (d.isEmpty()) {
                        sendMsg(c, MessageData.playerNotFound(name));
                    } else {
                        playerAbout(c, d.get());
                    }
                })
        ) {
        };
        SubCommand findDiscord = new SubCommand(b -> b
                .literal("discord")
                .required("id", longParser())
                .handler(c -> {
                    final long id = c.get("discord");
                    Optional<WhitelistEntry> d = plugin.whitelistData().getEntry(null, null, id);

                    if (d.isEmpty()) {
                        sendMsg(c, MessageData.playerNotFound(id));
                    } else {
                        playerAbout(c, d.get());
                    }
                })
        ) {
        };
        SubCommand findMe = new SubCommand(b -> b
                .literal("me")
                .handler(c -> {
                    final String name;
                    if (c.sender() instanceof Player p) {
                        name = p.getName();
                    } else {
                        sendMsg(c, MessageData.onlyPlayer());
                        return;
                    }
                    Optional<WhitelistEntry> d = plugin.whitelistData().getEntry(name, null, -1);

                    if (d.isEmpty()) {
                        sendMsg(c, MessageData.playerNotFound(name));
                    } else {
                        playerAbout(c, d.get());
                    }
                })
        ) {
        };
        return List.of(findUuid, findName, findDiscord, findMe);
    }
}
