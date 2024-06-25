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

public class NWLAddMethod {
    public static <T> void online(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, List<? extends PlayerWrapper> players) {
        int total = 0;
        if (players.isEmpty()) sendMsg(context, MessageData.serverEmpty(true), mapper);
        else {
            for (PlayerWrapper p : players) {
                if (data.whitelistData().savePlayer(p)) total++;
            }
            if (total == 0) sendMsg(context, MessageData.serverActually(true), mapper);
            else sendMsg(context, MessageData.serverAmount(true, total), mapper);
        }
    }

    public static <T> void uuid(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, UUID uuid) {
        data.whitelistData().getEntry(null, uuid, -1)
                .ifPresentOrElse(
                        d -> sendMsg(context, MessageData.playerAlready(uuid), mapper),
                        () -> {
                            data.whitelistData().registerAndSave(null, uuid, -1);
                            sendMsg(context, MessageData.playerAdded(uuid), mapper);
                        }
                                );
    }

    public static <T> void name(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, String name) {
        data.whitelistData().getEntry(name, null, -1)
                .ifPresentOrElse(
                        d -> sendMsg(context, MessageData.playerAlready(name), mapper),
                        () -> {
                            data.whitelistData().registerAndSave(name, null, -1);
                            sendMsg(context, MessageData.playerAdded(name), mapper);
                        }
                                );
    }

    public static <T> void full(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, String name, UUID uuid, long id) {
        data.whitelistData().getEntry(name, uuid, id)
                .ifPresentOrElse(
                        d -> sendMsg(context, MessageData.playerAlready(), mapper),
                        () -> {
                            data.whitelistData().registerAndSave(name, uuid, id);
                            sendMsg(context, MessageData.playerAdded(), mapper);
                        }
                                );
    }

    public static <T> void me(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, PlayerWrapper player) {
        data.whitelistData().getEntry(player)
                .ifPresentOrElse(
                        d -> sendMsg(context, MessageData.playerSelfAlready(), mapper),
                        () -> {
                            data.whitelistData().savePlayer(player);
                            sendMsg(context, MessageData.playerSelfAdded(), mapper);
                        }
                                );
    }
}
