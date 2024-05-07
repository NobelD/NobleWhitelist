package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.command.NWLToggleMethod;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.language.MessageData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;
import static org.incendo.cloud.parser.standard.LongParser.longParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class ToggleCommand extends OptionCommand<CommandSender> {
    public ToggleCommand(NobleWhitelist plugin) {
        super(b -> b.literal("toggle").permission("noblewhitelist.admin.toggle"), commands(plugin));
    }
    private static List<BaseCommand<CommandSender>> commands(NobleWhitelist plugin) {
        SubCommand<CommandSender> toggleUuid = new SubCommand<>(b -> b
                .literal("uuid")
                .required("uuid", uuidParser())
                .required("toggle", booleanParser())
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    final boolean toggle = c.get("toggle");
                    NWLToggleMethod.uuid(plugin, c, plugin.getAdventure()::senderAudience, uuid, toggle);
                })
        ) {};
        SubCommand<CommandSender> toggleName = new SubCommand<>(b -> b
                .literal("name")
                .required("name", stringParser())
                .required("toggle", booleanParser())
                .handler(c -> {
                    final String name = c.get("name");
                    final boolean toggle = c.get("toggle");
                    NWLToggleMethod.name(plugin, c, plugin.getAdventure()::senderAudience, name, toggle);
                })
        ) {};
        SubCommand<CommandSender> toggleMe = new SubCommand<>(b -> b
                .literal("me")
                .required("toggle", booleanParser())
                .handler(c -> {
                    if (c.sender() instanceof Player p) {
                        final boolean toggle = c.get("toggle");
                        NWLToggleMethod.name(plugin, c, plugin.getAdventure()::senderAudience, p.getName(), toggle);
                    } else {
                        sendMsg(c, MessageData.onlyPlayer(), plugin.getAdventure()::senderAudience);
                    }
                })
        ) {};
        SubCommand<CommandSender> toggleDiscord = new SubCommand<>(b -> b
                .literal("discord")
                .required("id", longParser())
                .required("toggle", booleanParser())
                .handler(c -> {
                    final long id = c.get("discord");
                    final boolean toggle = c.get("toggle");
                    NWLToggleMethod.discord(plugin, c, plugin.getAdventure()::senderAudience, id, toggle);
                })
        ) {};
        return List.of(toggleUuid, toggleName, toggleMe, toggleDiscord);
    }
}
