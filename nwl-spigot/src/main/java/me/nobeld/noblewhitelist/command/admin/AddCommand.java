package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.BPlayer;
import me.nobeld.noblewhitelist.command.NWLAddMethod;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static org.incendo.cloud.parser.standard.LongParser.longParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class AddCommand extends OptionCommand<CommandSender> {
    public AddCommand(NobleWhitelist plugin) {
        super(b -> b.literal("add").permission("noblewhitelist.admin.add"), commands(plugin));
    }
    private static List<BaseCommand<CommandSender>> commands(NobleWhitelist plugin) {
        SubCommand<CommandSender> addOnline = new SubCommand<>(b -> b
                .literal("online")
                .handler(c ->
                        NWLAddMethod.online(plugin, c, plugin.getAdventure()::senderAudience, Bukkit.getOnlinePlayers().stream().map(BPlayer::of).toList()))
        );
        SubCommand<CommandSender> addUuid = new SubCommand<>(b -> b
                .literal("uuid")
                .required("uuid", uuidParser())
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    NWLAddMethod.uuid(plugin, c, plugin.getAdventure()::senderAudience, uuid);
                })
        );
        SubCommand<CommandSender> addName = new SubCommand<>(b -> b
                .literal("name")
                .required("name", stringParser())
                .handler(c -> {
                    final String name = c.get("name");
                    NWLAddMethod.name(plugin, c, plugin.getAdventure()::senderAudience, name);
                })
        );
        SubCommand<CommandSender> addFull = new SubCommand<>(b -> b
                .literal("full")
                .required("name", stringParser())
                .required("uuid", uuidParser())
                .required("discordid", longParser())
                .handler(c -> {
                    final String name = c.get("name");
                    final UUID uuid = c.get("uuid");
                    final long id = c.get("discordid");
                    NWLAddMethod.full(plugin, c, plugin.getAdventure()::senderAudience, name, uuid, id);
                })
        );
        SubCommand<CommandSender> addMe = new SubCommand<>(b -> b
                .literal("me")
                .handler(c -> {
                    BPlayer player;
                    if (c.sender() instanceof Player p) {
                        player = BPlayer.of(p);
                        NWLAddMethod.me(plugin, c, plugin.getAdventure()::senderAudience, player);
                    } else {
                        sendMsg(c, MessageData.onlyPlayer(), plugin.getAdventure()::senderAudience);
                    }
                })
        );
        return List.of(addOnline, addUuid, addName, addFull, addMe);
    }
}
