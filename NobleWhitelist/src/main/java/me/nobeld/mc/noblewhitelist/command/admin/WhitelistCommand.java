package me.nobeld.mc.noblewhitelist.command.admin;

import me.nobeld.mc.noblewhitelist.NobleWhitelist;
import me.nobeld.mc.noblewhitelist.config.ConfigData;
import me.nobeld.mc.noblewhitelist.config.MessageData;
import me.nobeld.mc.noblewhitelist.model.command.BaseCommand;
import me.nobeld.mc.noblewhitelist.model.command.SubCommand;
import me.nobeld.mc.noblewhitelist.model.whitelist.WhitelistEntry;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;

import java.util.List;

import static me.nobeld.mc.noblewhitelist.model.command.BaseCommand.sendMsg;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class WhitelistCommand {
    private static void toggleStatus(NobleWhitelist plugin, CommandContext<CommandSender> ctx, boolean activate) {
        boolean actually = plugin.getConfigD().get(ConfigData.WhitelistCF.whitelistActive);

        if (activate == actually) {
            sendMsg(ctx, MessageData.whitelistAlready(activate));
        } else {
            plugin.getConfigD().set(ConfigData.WhitelistCF.whitelistActive, activate);
            sendMsg(ctx, MessageData.whitelistChanged(activate));
        }
    }
    public static List<BaseCommand> commands(NobleWhitelist plugin) {
        SubCommand list = new SubCommand(b -> b.literal("list")
                .permission("noblewhitelist.admin.list")
                .optional("page", integerParser(1))
                .handler(c -> {
                    int page = c.getOrDefault("page", 1);
                    List<WhitelistEntry> l = plugin.getStorage().listIndex(page);
                    if (l != null && !l.isEmpty()) {
                        sendMsg(c, MessageData.listPage(page));
                        l.forEach(w -> sendMsg(c, MessageData.listString(w)));
                    } else if (page > 1) sendMsg(c, MessageData.listEmpty(page));
                    else sendMsg(c, MessageData.whitelistEmpty());
                })
        ) {
        };
        SubCommand listClear = new SubCommand(b -> b.literal("list").permission("noblewhitelist.admin.list.clear")
                .literal("clear")
                .meta(ConfirmationManager.META_CONFIRMATION_REQUIRED, true)
                .handler(c -> {
                    if (!plugin.getStorage().clear()) sendMsg(c, MessageData.whitelistAlreadyEmpty());
                    else sendMsg(c, MessageData.whitelistCleared());
                })
        ) {
        };
        SubCommand on = new SubCommand(b -> b.literal("on").permission("noblewhitelist.admin.on")
                .handler(c -> toggleStatus(plugin, c, true))
        ) {
        };
        SubCommand off = new SubCommand(b -> b.literal("off").permission("noblewhitelist.admin.off")
                .handler(c -> toggleStatus(plugin, c, false))
        ) {
        };
        SubCommand reload = new SubCommand(b -> b.literal("reload").permission("noblewhitelist.admin.reload")
                .handler(c -> {
                    plugin.getStorage().reload();
                    plugin.getConfigD().reloadConfig();
                    sendMsg(c, MessageData.reload());
                })
        ) {
        };
        SubCommand status = new SubCommand(b -> b.literal("status").permission("noblewhitelist.admin.status")
                .handler(c -> {
                    sendMsg(c, MessageData.statusHeader());
                    sendMsg(c, MessageData.statusVersion(plugin.getUptChecker().version));
                    sendMsg(c, MessageData.statusWhitelistSize(plugin.getStorage().getTotal()));
                    sendMsg(c, MessageData.statusWhitelistActive(plugin.getConfigD().get(ConfigData.WhitelistCF.whitelistActive)));
                    sendMsg(c, MessageData.statusNameCheck(plugin.getConfigD().checkName()));
                    sendMsg(c, MessageData.statusUuidCheck(plugin.getConfigD().checkUUID()));
                    sendMsg(c, MessageData.statusPermCheck(plugin.getConfigD().checkPerm()));
                    sendMsg(c, MessageData.statusStorageType(plugin.getStorageType()));
                })
        ) {
        };
        return List.of(list, listClear, on, off, reload, status);
    }
}
