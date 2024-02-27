package me.nobeld.mc.noblewhitelist.discord.commands.basic;

import me.nobeld.mc.noblewhitelist.discord.config.ConfigData;
import me.nobeld.mc.noblewhitelist.discord.config.MessageData;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDData;
import me.nobeld.mc.noblewhitelist.discord.model.command.BaseCommand;
import me.nobeld.mc.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.mc.noblewhitelist.model.whitelist.WhitelistEntry;
import org.incendo.cloud.description.Description;

import java.util.List;

import static me.nobeld.mc.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.mc.noblewhitelist.discord.model.command.BaseCommand.invalidInteraction;
import static me.nobeld.mc.noblewhitelist.discord.model.command.BaseCommand.replyMsg;

public class BasicAccounts {
    private final NWLDData data;
    public BasicAccounts(NWLDData data) {
        this.data = data;
    }
    public List<BaseCommand> getCommands() {
        return List.of(accounts());
    }
    private SubCommand accounts() {
        return new SubCommand(b -> b.literal("accounts", Description.of("Get the accounts who are you linked from the minecraft server whitelist"))
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.selfAccounts, ConfigData.CommandsChannel.selfAccounts))
                        return;

                    long userid = c.sender().user().getIdLong();

                    WhitelistEntry entry = getPlugin().getStorage().loadPlayer(userid);
                    if (entry == null) {
                        replyMsg(data, c, MessageData.Error.selfNoAccounts);
                    } else
                        replyMsg(data, c, MessageData.Command.selfAccounts, data.getMessageD().baseHolder(entry));
                })
        ) {
        };
    }
}
