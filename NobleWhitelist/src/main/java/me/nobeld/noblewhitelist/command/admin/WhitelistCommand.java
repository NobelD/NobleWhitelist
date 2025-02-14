package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;

import java.util.List;

import static me.nobeld.noblewhitelist.model.command.BaseCommand.sendMsg;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class WhitelistCommand {
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
        SubCommand clearList = new SubCommand(b -> b.literal("clearlist")
                .permission("noblewhitelist.admin.list.clear")
                .meta(ConfirmationManager.META_CONFIRMATION_REQUIRED, true)
                .handler(c -> {
                    if (!plugin.getStorage().clear()) sendMsg(c, MessageData.whitelistAlreadyEmpty());
                    else sendMsg(c, MessageData.whitelistCleared());
                })
        ) {
        };
        SubCommand reload = new SubCommand(b -> b.literal("reload")
                .permission("noblewhitelist.admin.reload")
                .handler(c -> {
                    if (plugin.getStorageType().isDatabase())
                        plugin.reloadDataBase();
                    else plugin.getStorage().reload();
                    plugin.getConfigD().reloadConfig();
                    sendMsg(c, MessageData.reload());
                })
        ) {
        };
        SubCommand status = new SubCommand(b -> b.literal("status")
                .permission("noblewhitelist.admin.status")
                .handler(c -> {
                    sendMsg(c, MessageData.statusHeader());
                    sendMsg(c, MessageData.statusVersion(plugin.version()));
                    sendMsg(c, MessageData.statusWhitelistSize(plugin.getStorage().getTotal()));
                    sendMsg(c, MessageData.statusWhitelistActive(plugin.getConfigD().get(ConfigData.WhitelistCF.whitelistActive)));
                    sendMsg(c, MessageData.statusNameCheck(plugin.getConfigD().checkName()));
                    sendMsg(c, MessageData.statusUuidCheck(plugin.getConfigD().checkUUID()));
                    sendMsg(c, MessageData.statusPermCheck(plugin.getConfigD().checkPerm()));
                    sendMsg(c, MessageData.statusStorageType(plugin.getStorageType()));
                })
        ) {
        };
        SubCommand support = new SubCommand(b -> b.literal("support")
                .permission("noblewhitelist.admin.support")
                .handler(c -> plugin.getUptChecker().sendSupport(c.sender()))
        ) {
        };
        return List.of(list, clearList, reload, status, support);
    }
}
