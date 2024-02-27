package me.nobeld.mc.noblewhitelist.discord.commands.admin;

import me.nobeld.mc.noblewhitelist.discord.JDAManager;
import me.nobeld.mc.noblewhitelist.discord.config.ConfigData;
import me.nobeld.mc.noblewhitelist.discord.config.MessageData;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDData;
import me.nobeld.mc.noblewhitelist.discord.model.command.BaseCommand;
import me.nobeld.mc.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.mc.noblewhitelist.model.PairData;
import me.nobeld.mc.noblewhitelist.model.whitelist.WhitelistEntry;
import net.dv8tion.jda.api.entities.User;
import org.incendo.cloud.description.Description;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.nobeld.mc.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.mc.noblewhitelist.discord.model.command.BaseCommand.*;
import static org.incendo.cloud.discord.jda5.JDAParser.userParser;
import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class AdminModify {
    private final NWLDData data;
    private final JDAManager manager;
    public AdminModify(NWLDData data) {
        this.data = data;
        this.manager = data.getJDAManager();
    }
    public List<BaseCommand> getCommands() {
        return List.of(add(), remove(), link(), unlink(), toggle());
    }
    private SubCommand add() {
        return new SubCommand(b -> b.literal("add", Description.of("Add an user to the minecraft whitelist."))
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminAdd, ConfigData.CommandsChannel.adminAdd))
                        return;
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    if (insufficientData(data, c, name, uuid)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> opt = getPlugin().api().getWlData().getEntry(name, realuuid, id);

                    if (opt.isEmpty()) {
                        WhitelistEntry d = getPlugin().api().getWlData().registerAndSave(name, realuuid, id);
                        Map<String, String> m = data.getMessageD().baseHolder(d);

                        manager.setWhitelistedRole(c.sender().guild(), d, m, true);
                        replyMsg(data, c, MessageData.Command.userAdd, m);
                    } else
                        replyMsg(data, c, MessageData.Error.userAlready);
                })
        ) {
        };
    }
    private SubCommand remove() {
        return new SubCommand(b -> b.literal("remove", Description.of("Remove an user from the minecraft server whitelist."))
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminRemove, ConfigData.CommandsChannel.adminRemove))
                        return;
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    if (insufficientData(data, c, name, uuid)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> entry = getPlugin().api().getWlData().getEntry(name, realuuid, id);

                    if (entry.isPresent()) {
                        getPlugin().api().getWlData().deleteUser(entry.get());
                        Map<String, String> m = data.getMessageD().baseHolder(entry.get());

                        manager.setWhitelistedRole(c.sender().guild(), entry.get(), m, false);
                        replyMsg(data, c, MessageData.Command.userRemove, m);
                    } else
                        replyMsg(data, c, MessageData.Error.userNotFound);
                })
        ) {

        };
    }
    private SubCommand link() {
        return new SubCommand(b -> b.literal("link", Description.of("Link some data to a whitelisted user."))
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminLink, ConfigData.CommandsChannel.adminLink))
                        return;
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    if (insufficientData(data, c, name, uuid)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> entry = getPlugin().api().getWlData().getEntry(name, realuuid, -1);

                    if (entry.isPresent()) {
                        getPlugin().api().getWlData().linkDiscord(entry.get(), id);
                        Map<String, String> m = data.getMessageD().baseHolder(entry.get());

                        replyMsg(data, c, MessageData.Command.userLink, m);
                        manager.setWhitelistedRole(c.sender().guild(), entry.get(), m, true);
                    } else
                        replyMsg(data, c, MessageData.Error.userNotFound);
                })
        ) {
        };
    }
    private SubCommand unlink() {
        return new SubCommand(b -> b.literal("unlink", Description.of("Unlink some data of a whitelisted user."))
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminUnLink, ConfigData.CommandsChannel.adminUnLink))
                        return;
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    if (insufficientData(data, c, name, uuid, id)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> opt = getPlugin().api().getWlData().getEntry(name, realuuid, id);

                    if (opt.isPresent()) {
                        getPlugin().api().getWlData().linkDiscord(opt.get(), id);
                        Map<String, String> m = data.getMessageD().baseHolder(opt.get());

                        replyMsg(data, c, MessageData.Command.userUnLink, m);
                        manager.setWhitelistedRole(c.sender().guild(), opt.get(), m, false);
                    } else
                        replyMsg(data, c, MessageData.Error.userNotFound);
                })
        ) {
        };
    }
    private SubCommand toggle() {
        return new SubCommand(b -> b.literal("toggle", Description.of("Toggle if a whitelisted user can join or not."))
                .required("toggle", booleanParser())
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminToggle, ConfigData.CommandsChannel.adminToggle))
                        return;
                    boolean toggle = c.get("toggle");
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    if (insufficientData(data, c, name, uuid, id)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> opt = getPlugin().api().getWlData().getEntry(name, realuuid, id);

                    if (opt.isPresent()) {
                        if (opt.get().isWhitelisted() == toggle)
                            replyMsg(data, c, MessageData.Error.alreadyToggled, data.getMessageD().baseHolder(opt.get()));
                        else {
                            getPlugin().api().getWlData().toggleJoin(opt.get(), toggle);
                            replyMsg(data, c, MessageData.Command.userToggled, data.getMessageD().baseHolder(opt.get()));
                        }
                    } else
                        replyMsg(data, c, MessageData.Error.userNotFound);
                })
        ) {
        };
    }
}
