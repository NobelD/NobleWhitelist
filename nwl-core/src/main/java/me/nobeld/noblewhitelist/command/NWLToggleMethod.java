package me.nobeld.noblewhitelist.command;

import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.context.CommandContext;

import java.util.UUID;
import java.util.function.Function;

import static me.nobeld.noblewhitelist.model.command.BaseCommand.sendMsg;

public class NWLToggleMethod {
    public static <T> void uuid(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, UUID uuid, boolean toggle) {
        data.whitelistData().getEntry(null, uuid, -1)
                .ifPresentOrElse(d -> {
                            if (d.isWhitelisted() == toggle)
                                sendMsg(context, MessageData.playerToggledAlready(toggle), mapper);
                            else {
                                data.whitelistData().toggleJoin(d, toggle);
                                sendMsg(context, MessageData.playerToggled(uuid, toggle), mapper);
                            }
                        }, () -> sendMsg(context, MessageData.playerNotFound(uuid), mapper)
                );
    }
    public static <T> void name(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, String name, boolean toggle) {
        data.whitelistData().getEntry(name, null, -1)
                .ifPresentOrElse(d -> {
                            if (d.isWhitelisted() == toggle)
                                sendMsg(context, MessageData.playerToggledAlready(toggle), mapper);
                            else {
                                data.whitelistData().toggleJoin(d, toggle);
                                sendMsg(context, MessageData.playerToggled(name, toggle), mapper);
                            }
                        },
                        () -> sendMsg(context, MessageData.playerNotFound(name), mapper)
                );
    }
    public static <T> void discord(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, long id, boolean toggle) {
        data.whitelistData().getEntry(null, null, id)
                .ifPresentOrElse(d -> {
                            if (d.isWhitelisted() == toggle)
                                sendMsg(context, MessageData.playerToggledAlready(toggle), mapper);
                            else {
                                data.whitelistData().toggleJoin(d, toggle);
                                sendMsg(context, MessageData.playerToggled(id, toggle), mapper);
                            }
                        },
                        () -> sendMsg(context, MessageData.playerNotFound(id), mapper)
                );
    }
}
