package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.language.MessageData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static me.nobeld.noblewhitelist.temp.CustomStringParser.customStringParser;
import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;
import static org.incendo.cloud.parser.standard.LongParser.longParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class ToggleCommand extends OptionCommand {
    public ToggleCommand(NobleWhitelist plugin) {
        super(b -> b.literal("toggle").permission("noblewhitelist.admin.toggle"), commands(plugin));
    }
    private static List<BaseCommand> commands(NobleWhitelist plugin) {
        SubCommand toggleUuid = new SubCommand(b -> b
                .literal("uuid")
                .required("uuid", uuidParser())
                .required("toggle", booleanParser())
                .handler(c -> {
                    final UUID uuid = c.get("uuid");
                    final boolean toggle = c.get("toggle");
                    plugin.whitelistData().getEntry(null, uuid, -1)
                            .ifPresentOrElse(d -> {
                                        if (d.isWhitelisted() == toggle)
                                            sendMsg(c, MessageData.playerToggledAlready(toggle));
                                        else {
                                            plugin.whitelistData().toggleJoin(d, toggle);
                                            sendMsg(c, MessageData.playerToggled(uuid, toggle));
                                        }
                                    }, () -> sendMsg(c, MessageData.playerNotFound(uuid))
                            );
                })
        ) {};
        SubCommand toggleName = new SubCommand(b -> b
                .literal("name")
                .required("name", customStringParser())
                .required("toggle", booleanParser())
                .handler(c -> {
                    final String name = c.get("name");
                    final boolean toggle = c.get("toggle");
                    plugin.whitelistData().getEntry(name, null, -1)
                            .ifPresentOrElse(d -> {
                                        if (d.isWhitelisted() == toggle)
                                            sendMsg(c, MessageData.playerToggledAlready(toggle));
                                        else {
                                            plugin.whitelistData().toggleJoin(d, toggle);
                                            sendMsg(c, MessageData.playerToggled(name, toggle));
                                        }
                                    },
                                    () -> sendMsg(c, MessageData.playerNotFound(name))
                            );
                })
        ) {};
        SubCommand toggleMe = new SubCommand(b -> b
                .literal("me")
                .required("toggle", booleanParser())
                .handler(c -> {
                    final String name;
                    if (c.sender() instanceof Player p) {
                        name = p.getName();
                    } else {
                        sendMsg(c, MessageData.onlyPlayer());
                        return;
                    }
                    final boolean toggle = c.get("toggle");
                    plugin.whitelistData().getEntry(name, null, -1)
                            .ifPresentOrElse(d -> {
                                        if (d.isWhitelisted() == toggle)
                                            sendMsg(c, MessageData.playerToggledAlready(toggle));
                                        else {
                                            plugin.whitelistData().toggleJoin(d, toggle);
                                            sendMsg(c, MessageData.playerToggled(name, toggle));
                                        }
                                    },
                                    () -> sendMsg(c, MessageData.playerNotFound(name))
                            );
                })
        ) {};
        SubCommand toggleDiscord = new SubCommand(b -> b
                .literal("discord")
                .required("id", longParser())
                .required("toggle", booleanParser())
                .handler(c -> {
                    final long id = c.get("discord");
                    final boolean toggle = c.get("toggle");
                    plugin.whitelistData().getEntry(null, null, id)
                            .ifPresentOrElse(d -> {
                                        if (d.isWhitelisted() == toggle)
                                            sendMsg(c, MessageData.playerToggledAlready(toggle));
                                        else {
                                            plugin.whitelistData().toggleJoin(d, toggle);
                                            sendMsg(c, MessageData.playerToggled(id, toggle));
                                        }
                                    },
                                    () -> sendMsg(c, MessageData.playerNotFound(id))
                            );
                })
        ) {};
        return List.of(toggleUuid, toggleName, toggleMe, toggleDiscord);
    }
}
