package me.nobeld.mc.noblewhitelist.discord.commands.admin;

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
import java.util.Optional;
import java.util.UUID;

import static me.nobeld.mc.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.mc.noblewhitelist.discord.model.command.BaseCommand.*;
import static org.incendo.cloud.discord.jda5.JDAParser.userParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class AdminFind {
    private final NWLDData data;
    public AdminFind(NWLDData data) {
        this.data = data;
    }
    public List<BaseCommand> getCommands() {
        return List.of(player(), user());
    }
    private SubCommand player() {
        return new SubCommand(b -> b.literal("find", Description.of("Find an user whitelisted by some data."))
                .optional("name", stringParser())
                .optional("uuid", stringParser())
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminFind, ConfigData.CommandsChannel.adminFind))
                        return;
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
                    } else
                        replyMsg(data, c, MessageData.Command.userFind, data.getMessageD().baseHolder(opt.get()));
                })
        ) {
        };
    }
    private SubCommand user() {
        return new SubCommand(b -> b.literal("finduser", Description.of("Find an user whitelisted by their discord user."))
                .optional("user", userParser())
                .handler(c -> {
                    if (invalidInteraction(data, c, ConfigData.CommandsRole.adminUser, ConfigData.CommandsChannel.adminUser))
                        return;
                    User user = c.getOrDefault("user", null);
                    long id = user != null ? user.getIdLong() : -1;

                    Optional<WhitelistEntry> opt = getPlugin().api().getWlData().getEntry(null, null, id);
                    if (opt.isEmpty()) {
                        replyMsg(data, c, MessageData.Error.userNoAccounts);
                    } else
                        replyMsg(data, c, MessageData.Command.userAccounts, data.getMessageD().baseHolder(opt.get()));
                })
        ) {
        };
    }
}
