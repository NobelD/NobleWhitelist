package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.command.NWLFindMethod;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.language.MessageData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static org.incendo.cloud.parser.standard.LongParser.longParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class FindCommand extends OptionCommand<CommandSender> {
    public FindCommand(NobleWhitelist plugin) {
        super(b -> b.literal("find").permission("noblewhitelist.admin.find"), commands(plugin));
    }
    private static List<BaseCommand<CommandSender>> commands(NobleWhitelist plugin) {
        SubCommand<CommandSender> findUuid = new SubCommand<>(b -> b
                .literal("uuid")
                .required("uuid", uuidParser())
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    NWLFindMethod.uuid(plugin, c, plugin.getAdventure()::senderAudience, uuid);
                })
        ) {
        };
        SubCommand<CommandSender> findName = new SubCommand<>(b -> b
                .literal("name")
                .required("name", stringParser())
                .handler(c -> {
                    final String name = c.get("name");
                    NWLFindMethod.name(plugin, c, plugin.getAdventure()::senderAudience, name);
                })
        ) {
        };
        SubCommand<CommandSender> findDiscord = new SubCommand<>(b -> b
                .literal("discord")
                .required("id", longParser())
                .handler(c -> {
                    final long id = c.get("discord");
                    NWLFindMethod.discord(plugin, c, plugin.getAdventure()::senderAudience, id);
                })
        ) {
        };
        SubCommand<CommandSender> findMe = new SubCommand<>(b -> b
                .literal("me")
                .handler(c -> {
                    if (c.sender() instanceof Player p) {
                        NWLFindMethod.name(plugin, c, plugin.getAdventure()::senderAudience, p.getName());
                    } else {
                        sendMsg(c, MessageData.onlyPlayer(), plugin.getAdventure()::senderAudience);
                    }
                })
        ) {
        };
        return List.of(findUuid, findName, findDiscord, findMe);
    }
}
