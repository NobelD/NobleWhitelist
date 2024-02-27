package me.nobeld.mc.noblewhitelist.discord.commands.admin;

import me.nobeld.mc.noblewhitelist.NobleWhitelist;
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

public class AdminWhitelist {
    private final NWLDData data;
    public AdminWhitelist(NWLDData data) {
        this.data = data;
    }
    public List<BaseCommand> getCommands() {
        return List.of(on(), off());
    }
    private SubCommand list() {
        return new SubCommand(b -> b.literal("list", Description.of("Get a list of the users whitelisted."))
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminList, ConfigData.CommandsChannel.adminList))
                        return;
                    int page = c.getOrDefault("page", 1);

                    List<WhitelistEntry> list = getPlugin().api().getIndex(page);
                    if (list != null && !list.isEmpty()) {
                        BaseCommand.replyMsg(c, "List of players. Page: " + page, true);
                    } else if (page > 1) BaseCommand.replyMsg(c, "This page is empty: " + page, true);
                    else BaseCommand.replyMsg(c, "The whitelist is empty.", true);
                })
        ) {
        };
    }
    private SubCommand on() {
        return new SubCommand(b -> b.literal("on", Description.of("Enable the minecraft server whitelist."))
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminOn, ConfigData.CommandsChannel.adminOn))
                        return;
                    if (NobleWhitelist.getPlugin().api().whitelist(true)) {
                        replyMsg(data, c, MessageData.Command.wlOn);
                    } else
                        replyMsg(data, c, MessageData.Command.wlAlreadyOn);
                })
        ) {
        };
    }
    private SubCommand off() {
        return new SubCommand(b -> b.literal("off", Description.of("Disable the minecraft server whitelist."))
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminOff, ConfigData.CommandsChannel.adminOff))
                        return;
                    if (NobleWhitelist.getPlugin().api().whitelist(false)) {
                        replyMsg(data, c, MessageData.Command.wlOff);
                    } else
                        replyMsg(data, c, MessageData.Command.wlAlreadyOff);
                })
        ) {
        };
    }
}
