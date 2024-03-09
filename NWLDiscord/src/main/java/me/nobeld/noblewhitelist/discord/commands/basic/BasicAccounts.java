package me.nobeld.noblewhitelist.discord.commands.basic;

import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.language.CMDDescription;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.command.BaseCommand;
import me.nobeld.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.nobeld.noblewhitelist.discord.commands.CommandManager.REQUIREMENTS_KEY;
import static me.nobeld.noblewhitelist.discord.model.command.BaseCommand.*;
import static me.nobeld.noblewhitelist.discord.util.DiscordUtil.parseMessage;

public class BasicAccounts {
    private final NWLDsData data;
    public BasicAccounts(NWLDsData data) {
        this.data = data;
    }
    public List<BaseCommand> getCommands() {
        return List.of(accounts());
    }
    private SubCommand accounts() {
        return new SubCommand(b -> b.literal("accounts", CMDDescription.selfAccounts())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.selfAccounts))
                .handler(c -> {
                    long userid = c.sender().user().getIdLong();

                    List<WhitelistEntry> list = new ArrayList<>();
                    WhitelistEntry w = data.getNWL().getStorage().loadPlayer(userid);
                    if (w != null) list.add(w);
                    if (list.isEmpty()) {
                        replyMsg(data, c, MessageData.Error.selfNoAccounts);
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

                        replyMsg(data, c, MessageData.Command.selfAccounts, m);
                    }
                })
        ) {
        };
    }
}
