package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.command.NWLWhitelistMethod;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;

import java.util.List;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class WhitelistCommand {
    public static List<BaseCommand<CommandSender>> commands(NobleWhitelist plugin) {
        SubCommand<CommandSender> list = new SubCommand<>(b -> b.literal("list")
                .permission("noblewhitelist.admin.list")
                .optional("page", integerParser(1))
                .handler(c -> {
                    NWLWhitelistMethod.list(plugin, c, plugin.getAdventure()::senderAudience);
                })
        ) {
        };
        SubCommand<CommandSender> clearList = new SubCommand<>(b -> b.literal("clearlist")
                .permission("noblewhitelist.admin.list.clear")
                .meta(ConfirmationManager.META_CONFIRMATION_REQUIRED, true)
                .handler(c -> {
                    NWLWhitelistMethod.listClear(plugin, c, plugin.getAdventure()::senderAudience);
                })
        ) {
        };
        SubCommand<CommandSender> reload = new SubCommand<>(b -> b.literal("reload")
                .permission("noblewhitelist.admin.reload")
                .handler(c -> {
                    NWLWhitelistMethod.reload(plugin, c, plugin.getAdventure()::senderAudience);
                })
        ) {
        };
        SubCommand<CommandSender> status = new SubCommand<>(b -> b.literal("status")
                .permission("noblewhitelist.admin.status")
                .handler(c -> {
                    NWLWhitelistMethod.status(plugin, c, plugin.getAdventure()::senderAudience);
                })
        ) {
        };
        return List.of(list, clearList, reload, status);
    }
}
