package me.nobeld.noblewhitelist.command;

import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.context.CommandContext;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static me.nobeld.noblewhitelist.model.command.BaseCommand.sendMsg;

public class NWLRemoveMethod {
    public static <T> void online(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, List<? extends PlayerWrapper> list) {
        if (list.isEmpty()) sendMsg(context, MessageData.serverEmpty(false), mapper);
        else {
            int total = 0;
            for (PlayerWrapper p : list) {
                if (data.whitelistData().deleteUser(p)) total++;
            }
            if (total == 0) sendMsg(context, MessageData.serverActually(false), mapper);
            else sendMsg(context, MessageData.serverAmount(false, total), mapper);
        }
    }
    public static <T> void uuid(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, UUID uuid) {
        if (data.whitelistData().deleteUser(null, uuid))
            sendMsg(context, MessageData.playerRemoved(uuid), mapper);
        else sendMsg(context, MessageData.playerNotFound(uuid), mapper);
    }
    public static <T> void name(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, String name) {
        if (data.whitelistData().deleteUser(name, null))
            sendMsg(context, MessageData.playerRemoved(name), mapper);
        else sendMsg(context, MessageData.playerNotFound(name), mapper);
    }
    public static <T> void me(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, PlayerWrapper player) {
        data.whitelistData().getEntry(player)
                .ifPresentOrElse(d -> {
                            data.whitelistData().deleteUser(player);
                            sendMsg(context, MessageData.playerSelfRemoved(), mapper);
                        }, () -> sendMsg(context, MessageData.playerSelfNotFound(), mapper)
                );
    }
}
