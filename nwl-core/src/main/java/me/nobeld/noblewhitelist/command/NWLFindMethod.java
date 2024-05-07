package me.nobeld.noblewhitelist.command;

import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.context.CommandContext;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static me.nobeld.noblewhitelist.model.command.BaseCommand.sendMsg;

public class NWLFindMethod {
    private static <T> void playerAbout(CommandContext<T> context, WhitelistEntry d, Function<T, Audience> mapper) {
        sendMsg(context, MessageData.playerAbout(d), mapper);
        sendMsg(context, MessageData.playerAboutName(d), mapper);
        sendMsg(context, MessageData.playerAboutUuid(d), mapper);
        sendMsg(context, MessageData.playerAboutUser(d), mapper);
        sendMsg(context, MessageData.playerAboutJoin(d), mapper);
    }
    public static <T> void uuid(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, UUID uuid) {
        Optional<WhitelistEntry> d = data.whitelistData().getEntry(null, uuid, -1);

        if (d.isEmpty()) {
            sendMsg(context, MessageData.playerNotFound(uuid), mapper);
        } else {
            playerAbout(context, d.get(), mapper);
        }
    }
    public static <T> void name(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, String name) {
        Optional<WhitelistEntry> d = data.whitelistData().getEntry(name, null, -1);

        if (d.isEmpty()) {
            sendMsg(context, MessageData.playerNotFound(name), mapper);
        } else {
            playerAbout(context, d.get(), mapper);
        }
    }
    public static <T> void discord(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, long id) {
        Optional<WhitelistEntry> d = data.whitelistData().getEntry(null, null, id);

        if (d.isEmpty()) {
            sendMsg(context, MessageData.playerNotFound(id), mapper);
        } else {
            playerAbout(context, d.get(), mapper);
        }
    }
}
