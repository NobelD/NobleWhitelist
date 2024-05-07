package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.BPlayer;
import me.nobeld.noblewhitelist.command.NWLRemoveMethod;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.language.MessageData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class RemoveCommand extends OptionCommand<CommandSender> {
    public RemoveCommand(NobleWhitelist plugin) {
        super(b -> b.literal("remove").permission("noblewhitelist.admin.remove"), commands(plugin));
    }
    private static List<BaseCommand<CommandSender>> commands(NobleWhitelist plugin) {
        SubCommand<CommandSender> removeOnline = new SubCommand<>(b -> b
                .literal("online")
                .handler(c -> NWLRemoveMethod.online(plugin, c, plugin.getAdventure()::senderAudience, Bukkit.getOnlinePlayers().stream().map(BPlayer::of).toList())));
        SubCommand<CommandSender> removeUuid = new SubCommand<>(b -> b
                .literal("uuid")
                .required("uuid", uuidParser())
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    NWLRemoveMethod.uuid(plugin, c, plugin.getAdventure()::senderAudience, uuid);
                })
        );
        SubCommand<CommandSender> removeName = new SubCommand<>(b -> b
                .literal("name")
                .required("name", stringParser())
                .handler(c -> {
                    final String name = c.get("name");
                    NWLRemoveMethod.name(plugin, c, plugin.getAdventure()::senderAudience, name);
                })
        );
        SubCommand<CommandSender> removeMe = new SubCommand<>(b -> b
                .literal("me")
                .handler(c -> {
                    if (c.sender() instanceof Player p) {
                        NWLRemoveMethod.me(plugin, c, plugin.getAdventure()::senderAudience, BPlayer.of(p));
                    } else {
                        sendMsg(c, MessageData.onlyPlayer(), plugin.getAdventure()::senderAudience);
                    }
                })
        );
        return List.of(removeOnline, removeUuid, removeName, removeMe);
    }
}
