package me.nobeld.noblewhitelist.discord.commands.basic;

import me.nobeld.noblewhitelist.discord.JDAManager;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.language.CMDDescription;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.command.BaseCommand;
import me.nobeld.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.nobeld.noblewhitelist.discord.commands.CommandManager.REQUIREMENTS_KEY;
import static me.nobeld.noblewhitelist.discord.model.command.BaseCommand.*;
import static me.nobeld.noblewhitelist.discord.util.DiscordUtil.getMessage;
import static me.nobeld.noblewhitelist.discord.util.DiscordUtil.sendMessage;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class BasicModify {
    private final NWLDsData data;
    private final JDAManager manager;
    public BasicModify(NWLDsData data) {
        this.data = data;
        this.manager = data.getJDAManager();
    }
    public List<BaseCommand> getCommands() {
        return List.of(add(), remove(), link());
    }
    // #TODO system to add more accounts to the same user
    private SubCommand add() {
        return new SubCommand(b -> b.literal("add", CMDDescription.selfAdd())
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.selfAdd))
                .handler(c -> {
                    long userid = c.sender().user().getIdLong();
                    Optional<WhitelistEntry> entryF = data.getNWL().whitelistData().getEntry(null, null, userid);
                    if (entryF.isPresent()) {
                        replyMsg(c, "You can not register more accounts to the whitelist.", true);
                        return;
                    }

                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);

                    if (noInputtedData(data, c, name, uuid)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> entry = data.getNWL().whitelistData().getEntry(name, realuuid, userid);

                    if (entry.isEmpty()) {
                        WhitelistEntry d = data.getNWL().whitelistData().registerAndSave(name, realuuid, userid);
                        Map<String, String> m = data.getMessageD().baseHolder(d);

                        manager.setWhitelistedRole(c.sender().guild(), d, m, true);
                        replyMsg(data, c, MessageData.Command.selfAdd, m);
                        sendMessage(manager.getChannel(ConfigData.Channel.selfRegister), getMessage(data, MessageData.Channel.notifySelfAdd, m));
                    } else
                        replyMsg(data, c, MessageData.Error.selfAlready);
                })
        ) {
        };
    }
    // #TODO system to remove multiple accounts for same user
    private SubCommand remove() {
        return new SubCommand(b -> b.literal("remove", CMDDescription.selfRemove())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.selfRemove))
                .handler(c -> {
                    long userid = c.sender().user().getIdLong();
                    Optional<WhitelistEntry> entryF = data.getNWL().whitelistData().getEntry(null, null, userid);
                    if (entryF.isEmpty()) {
                        replyMsg(c, "You don't have any account registered to the whitelist.", true);
                        return;
                    }

                    Optional<WhitelistEntry> entry = data.getNWL().whitelistData().getEntry(null, null, userid);

                    if (entry.isPresent()) {
                        data.getNWL().whitelistData().deleteUser(entry.get());
                        Map<String, String> m = data.getMessageD().baseHolder(entry.get());

                        replyMsg(data, c, MessageData.Command.selfRemove, m);
                        sendMessage(manager.getChannel(ConfigData.Channel.selfRemove), getMessage(data, MessageData.Channel.notifySelfRemove, m));
                        manager.setWhitelistedRole(c.sender().guild(), entry.get(), m, false);
                    } else
                        replyMsg(data, c, MessageData.Error.userNotFound);
                })
        ) {
        };
    }
    private SubCommand link() {
        return new SubCommand(b -> b.literal("link", CMDDescription.selfLink())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.selfLink))
                .handler(c -> {
                    long userid = c.sender().user().getIdLong();
                    Optional<WhitelistEntry> entryF = data.getNWL().whitelistData().getEntry(null, null, userid);
                    if (entryF.isPresent()) {
                        replyMsg(data, c, MessageData.Error.alreadySelfLinked);
                        return;
                    }

                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);

                    if (noInputtedData(data, c, name, uuid)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> entry = data.getNWL().whitelistData().getEntry(name, realuuid, -1);

                    if (entry.isEmpty()) {
                        replyMsg(data, c, MessageData.Error.userNotFound);
                    } else {
                        data.getNWL().whitelistData().linkDiscord(entry.get(), userid);
                        Map<String, String> m = data.getMessageD().baseHolder(entry.get());

                        manager.setWhitelistedRole(c.sender().guild(), entry.get(), m, true);
                        replyMsg(data, c, MessageData.Command.selfLink, m);
                    }
                })
        ) {
        };
    }
}
