package me.nobeld.mc.noblewhitelist.discord.commands.basic;

import me.nobeld.mc.noblewhitelist.discord.JDAManager;
import me.nobeld.mc.noblewhitelist.discord.config.ConfigData;
import me.nobeld.mc.noblewhitelist.discord.config.MessageData;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDData;
import me.nobeld.mc.noblewhitelist.discord.model.command.BaseCommand;
import me.nobeld.mc.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.mc.noblewhitelist.model.PairData;
import me.nobeld.mc.noblewhitelist.model.whitelist.WhitelistEntry;
import org.incendo.cloud.description.Description;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.nobeld.mc.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.mc.noblewhitelist.discord.model.command.BaseCommand.*;
import static me.nobeld.mc.noblewhitelist.discord.util.DiscordUtil.getMessage;
import static me.nobeld.mc.noblewhitelist.discord.util.DiscordUtil.sendMessage;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class BasicModify {
    private final NWLDData data;
    private final JDAManager manager;
    public BasicModify(NWLDData data) {
        this.data = data;
        this.manager = data.getJDAManager();
    }
    public List<BaseCommand> getCommands() {
        return List.of(add(), remove(), link());
    }
    // #TODO system to add more accounts to the same user
    private SubCommand add() {
        return new SubCommand(b -> b.literal("add", Description.of("Add your account to the minecraft server whitelist."))
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.selfAdd, ConfigData.CommandsChannel.selfAdd))
                        return;

                    long userid = c.sender().user().getIdLong();
                    Optional<WhitelistEntry> data2 = getPlugin().api().getWlData().getEntry(null, null, userid);
                    if (data2.isPresent()) {
                        replyMsg(c, "You can not register more accounts to the whitelist.", true);
                        return;
                    }

                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);

                    if (insufficientData(data, c, name, uuid)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> opt = getPlugin().api().getWlData().getEntry(name, realuuid, userid);

                    if (opt.isEmpty()) {
                        WhitelistEntry d = getPlugin().api().getWlData().registerAndSave(name, realuuid, userid);
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
        return new SubCommand(b -> b.literal("remove", Description.of("Remove your account from the minecraft server whitelist."))
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.selfRemove, ConfigData.CommandsChannel.selfRemove))
                        return;

                    long userid = c.sender().user().getIdLong();
                    Optional<WhitelistEntry> data2 = getPlugin().api().getWlData().getEntry(null, null, userid);
                    if (data2.isEmpty()) {
                        replyMsg(c, "You don't have any account registered to the whitelist.", true);
                        return;
                    }

                    Optional<WhitelistEntry> opt = getPlugin().api().getWlData().getEntry(null, null, userid);

                    if (opt.isPresent()) {
                        getPlugin().api().getWlData().deleteUser(opt.get());
                        Map<String, String> m = data.getMessageD().baseHolder(opt.get());

                        replyMsg(data, c, MessageData.Command.selfRemove, m);
                        sendMessage(manager.getChannel(ConfigData.Channel.selfRemove), getMessage(data, MessageData.Channel.notifySelfRemove, m));
                        manager.setWhitelistedRole(c.sender().guild(), opt.get(), m, false);
                    } else
                        replyMsg(data, c, MessageData.Error.userNotFound);
                })
        ) {
        };
    }
    private SubCommand link() {
        return new SubCommand(b -> b.literal("link", Description.of("Link your account to a whitelist entry."))
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.selfLink, ConfigData.CommandsChannel.selfLink))
                        return;

                    long userid = c.sender().user().getIdLong();
                    Optional<WhitelistEntry> data2 = getPlugin().api().getWlData().getEntry(null, null, userid);
                    if (data2.isPresent()) {
                        replyMsg(data, c, MessageData.Error.alreadySelfLinked);
                        return;
                    }

                    String name = c.getOrDefault("name", null);
                    String uuid = c.getOrDefault("uuid", null);

                    if (insufficientData(data, c, name, uuid)) return;
                    UUID realuuid;
                    PairData<Boolean, UUID> pair = invalidUUID(data, c, uuid);
                    if (pair.getFirst()) return;
                    else realuuid = pair.getSecond();

                    Optional<WhitelistEntry> opt = getPlugin().api().getWlData().getEntry(name, realuuid, -1);

                    if (opt.isEmpty()) {
                        replyMsg(data, c, MessageData.Error.userNotFound);
                    } else {
                        getPlugin().api().getWlData().linkDiscord(opt.get(), userid);
                        Map<String, String> m = data.getMessageD().baseHolder(opt.get());

                        manager.setWhitelistedRole(c.sender().guild(), opt.get(), m, true);
                        replyMsg(data, c, MessageData.Command.selfLink, m);
                    }
                })
        ) {
        };
    }
}
