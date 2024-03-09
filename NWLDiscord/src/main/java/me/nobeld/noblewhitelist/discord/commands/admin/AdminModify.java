package me.nobeld.noblewhitelist.discord.commands.admin;

import me.nobeld.noblewhitelist.discord.JDAManager;
import me.nobeld.noblewhitelist.discord.commands.CommandManager;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.language.CMDDescription;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.command.BaseCommand;
import me.nobeld.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.nobeld.noblewhitelist.discord.commands.CommandManager.REQUIREMENTS_KEY;
import static me.nobeld.noblewhitelist.discord.model.command.BaseCommand.*;
import static org.incendo.cloud.discord.jda5.JDAParser.userParser;
import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.parser.standard.UUIDParser.uuidParser;

public class AdminModify {
    private final NWLDsData data;
    private final JDAManager manager;
    public AdminModify(NWLDsData data) {
        this.data = data;
        this.manager = data.getJDAManager();
    }
    public List<BaseCommand> getCommands() {
        return List.of(add(), remove(), link(), unlink(), toggle());
    }
    private SubCommand add() {
        return new SubCommand(b -> b.literal("add", CMDDescription.addUser())
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminAdd))
                .handler(c -> {
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    if (noInputtedData(data, c, name, uuid)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> entry = data.getNWL().whitelistData().getEntry(name, realuuid, id);

                    if (entry.isEmpty()) {
                        WhitelistEntry d = data.getNWL().whitelistData().registerAndSave(name, realuuid, id);
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
        return new SubCommand(b -> b.literal("remove", CMDDescription.removeUser())
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminRemove))
                .handler(c -> {
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    if (noInputtedData(data, c, name, uuid)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> entry = data.getNWL().whitelistData().getEntry(name, realuuid, id);

                    if (entry.isPresent()) {
                        data.getNWL().whitelistData().deleteUser(entry.get());
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
        return new SubCommand(b -> b.literal("link", CMDDescription.linkUser())
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminLink))
                .handler(c -> {
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    Optional<WhitelistEntry> entryD = data.getNWL().whitelistData().getEntry(null, null, id);
                    if (entryD.isPresent()) {
                        replyMsg(data, c, MessageData.Error.alreadyUserLinked);
                        return;
                    }

                    if (insufficientData(data, c, name, uuid, id)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> entry = data.getNWL().whitelistData().getEntry(name, realuuid, -1);

                    if (entry.isPresent()) {
                        data.getNWL().whitelistData().linkDiscord(entry.get(), id);
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
        return new SubCommand(b -> b.literal("unlink", CMDDescription.unlinkUser())
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminUnLink))
                .handler(c -> {
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    if (insufficientData(data, c, name, uuid, id)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> entry = data.getNWL().whitelistData().getEntry(name, realuuid, id);

                    if (entry.isPresent()) {
                        data.getNWL().whitelistData().linkDiscord(entry.get(), id);
                        Map<String, String> m = data.getMessageD().baseHolder(entry.get());

                        replyMsg(data, c, MessageData.Command.userUnLink, m);
                        manager.setWhitelistedRole(c.sender().guild(), entry.get(), m, false);
                    } else
                        replyMsg(data, c, MessageData.Error.userNotFound);
                })
        ) {
        };
    }
    private SubCommand toggle() {
        return new SubCommand(b -> b.literal("toggle", CMDDescription.toggleUser())
                .required("toggle", booleanParser())
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .optional("user", userParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminToggle))
                .handler(c -> {
                    boolean toggle = c.get("toggle");
                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    if (noInputtedData(data, c, name, uuid, id)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> entry = data.getNWL().whitelistData().getEntry(name, realuuid, id);

                    if (entry.isPresent()) {
                        if (entry.get().isWhitelisted() == toggle)
                            replyMsg(data, c, MessageData.Error.alreadyToggled, data.getMessageD().baseHolder(entry.get()));
                        else {
                            data.getNWL().whitelistData().toggleJoin(entry.get(), toggle);
                            replyMsg(data, c, MessageData.Command.userToggled, data.getMessageD().baseHolder(entry.get()));
                        }
                    } else
                        replyMsg(data, c, MessageData.Error.userNotFound);
                })
        ) {
        };
    }
}
