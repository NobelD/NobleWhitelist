package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.command.NWLManageMethod;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class ManageCommand {
    public static List<BaseCommand<CommandSender>> commands(NobleWhitelist plugin) {
        List<BaseCommand<CommandSender>> commands = new ArrayList<>();
        commands.add(new SubCommand<>(b -> b.literal("on").permission("noblewhitelist.admin.on")
                .handler(c -> NWLManageMethod.toggle(plugin, c, plugin.getAdventure()::senderAudience, true))
        ) {
        });
        commands.add(new SubCommand<>(b -> b.literal("off").permission("noblewhitelist.admin.off")
                .handler(c -> NWLManageMethod.toggle(plugin, c, plugin.getAdventure()::senderAudience, false))
        ) {
        });
        commands.add(new Perm(plugin));
        return commands;
    }
    private static class Perm extends OptionCommand<CommandSender> {
        public Perm(NobleWhitelist plugin) {
            super(b -> b.literal("perm").permission("noblewhitelist.admin.permission"), commands(plugin));
        }
        private static List<BaseCommand<CommandSender>> commands(NobleWhitelist plugin) {
            SubCommand<CommandSender> permStatus = new SubCommand<>(b -> b.literal("status")
                    .handler(c -> NWLManageMethod.permStatus(plugin, c, plugin.getAdventure()::senderAudience))
            ) {
            };
            SubCommand<CommandSender> permChange = new SubCommand<>(b -> b.literal("set")
                    .required("minimum", integerParser(-1))
                    .handler(c -> {
                        final int min = c.get("minimum");
                        NWLManageMethod.permSet(plugin, c, plugin.getAdventure()::senderAudience, min);
                    })
            ) {
            };
            return List.of(permStatus, permChange);
        }
    }
}
