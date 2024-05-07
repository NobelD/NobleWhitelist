package me.nobeld.noblewhitelist.command;

import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.context.CommandContext;

import java.util.List;
import java.util.function.Function;

import static me.nobeld.noblewhitelist.model.command.BaseCommand.sendMsg;

public class NWLWhitelistMethod {
    public static <T> void list(NWLData data, CommandContext<T> context, Function<T, Audience> mapper) {
        int page = context.getOrDefault("page", 1);
        List<WhitelistEntry> l = data.getStorage().listIndex(page);
        if (l != null && !l.isEmpty()) {
            sendMsg(context, MessageData.listPage(page), mapper);
            l.forEach(w -> sendMsg(context, MessageData.listString(w), mapper));
        } else if (page > 1) sendMsg(context, MessageData.listEmpty(page), mapper);
        else sendMsg(context, MessageData.whitelistEmpty(), mapper);
    }

    public static <T> void listClear(NWLData data, CommandContext<T> context, Function<T, Audience> mapper) {
        if (!data.getStorage().clear()) sendMsg(context, MessageData.whitelistAlreadyEmpty(), mapper);
        else sendMsg(context, MessageData.whitelistCleared(), mapper);
    }

    public static <T> void reload(NWLData data, CommandContext<T> context, Function<T, Audience> mapper) {
        if (data.getStorageType().isDatabase())
            data.reloadDataBase();
        else data.getStorage().reload();
        data.getConfigD().reloadConfig();
        sendMsg(context, MessageData.reload(), mapper);
    }

    public static <T> void status(NWLData data, CommandContext<T> context, Function<T, Audience> mapper) {
        sendMsg(context, MessageData.statusHeader(), mapper);
        sendMsg(context, MessageData.statusVersion(data.getUptChecker().version), mapper);
        sendMsg(context, MessageData.statusWhitelistSize(data.getStorage().getTotal()), mapper);
        sendMsg(context, MessageData.statusWhitelistActive(data.getConfigD().get(ConfigData.WhitelistCF.whitelistActive)), mapper);
        sendMsg(context, MessageData.statusNameCheck(data.getConfigD().checkName()), mapper);
        sendMsg(context, MessageData.statusUuidCheck(data.getConfigD().checkUUID()), mapper);
        sendMsg(context, MessageData.statusPermCheck(data.getConfigD().checkPerm()), mapper);
        sendMsg(context, MessageData.statusStorageType(data.getStorageType()), mapper);
    }
}
