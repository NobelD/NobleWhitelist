package me.nobeld.noblewhitelist.discord.commands.admin;

import me.nobeld.noblewhitelist.discord.language.CMDDescription;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.command.BaseCommand;
import me.nobeld.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

import static me.nobeld.noblewhitelist.discord.commands.CommandManager.REQUIREMENTS_KEY;
import static me.nobeld.noblewhitelist.discord.model.command.BaseCommand.*;
import static me.nobeld.noblewhitelist.discord.util.DiscordUtil.parseMessage;
import static org.incendo.cloud.discord.jda5.JDAParser.userParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class AdminFind {
    private final NWLDsData data;
    public AdminFind(NWLDsData data) {
        this.data = data;
    }
    public List<BaseCommand> getCommands() {
        return List.of(player(), user());
    }
    private SubCommand player() {
        return new SubCommand(b -> b.literal("find", CMDDescription.find())
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminFind))
                .handler(c -> {
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
                    } else
                        replyMsg(data, c, MessageData.Command.userFind, data.getMessageD().baseHolder(entry.get()));
                })
        ) {
        };
    }
    private SubCommand user() {
        return new SubCommand(b -> b.literal("finduser", CMDDescription.findUser())
                .optional("user", userParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminUser))
                .handler(c -> {
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    List<WhitelistEntry> list = new ArrayList<>();
                    Optional<WhitelistEntry> e = data.getNWL().whitelistData().getEntry(null, null, id);
                    e.ifPresent(list::add);

                    if (list.isEmpty()) {
                        replyMsg(data, c, MessageData.Error.userNoAccounts);
                    } else {
                        List<String> entries = new ArrayList<>();
                        for (WhitelistEntry entry : list) {
                            Map<String, String> m = data.getMessageD().baseHolder(entry);
                            entries.add(parseMessage(data.getMessageD().getMsg(MessageData.PlaceHolders.accountEntry), m));
                        }
                        StringBuilder sb = new StringBuilder();
                        for (String entry : entries) {
                            sb.append(entry).append("$$");
                        }
                        Map<String,String> m = Map.of("count_total", String.valueOf(list.size()),
                                "account_entry", sb.toString().replace("$$$$", "\n").replace("$$", ""));

                        replyMsg(data, c, MessageData.Command.userAccounts, m);
                    }
                })
        ) {
        };
    }
}
