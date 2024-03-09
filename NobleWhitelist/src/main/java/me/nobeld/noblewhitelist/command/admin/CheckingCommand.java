package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;
import me.nobeld.noblewhitelist.model.checking.CheckingType;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.model.command.SubCommand;

import java.util.List;

import static org.incendo.cloud.parser.standard.EnumParser.enumParser;

public class CheckingCommand extends OptionCommand {
    public CheckingCommand(NobleWhitelist plugin) {
        super(b -> b.literal("checking").permission("noblewhitelist.admin.checking"), commands(plugin));
    }
    private static List<BaseCommand> commands(NobleWhitelist plugin) {
        SubCommand status = new SubCommand(b -> b.literal("status")
                .handler(c -> {
                    sendMsg(c, MessageData.statusNameCheck(plugin.getConfigD().checkName()));
                    sendMsg(c, MessageData.statusUuidCheck(plugin.getConfigD().checkUUID()));
                    sendMsg(c, MessageData.statusPermCheck(plugin.getConfigD().checkPerm()));
                })
        ) {};
        SubCommand change = new SubCommand(b -> b
                .literal("set")
                .required("type", enumParser(CheckingType.class))
                .required("option", enumParser(CheckingOption.class))
                .handler(c -> {
                    CheckingType type = c.get("type");
                    CheckingOption option = c.get("option");

                    if (plugin.getConfigD().getChecking(type) == option) {
                        sendMsg(c, MessageData.checkingAlready(type, option));
                    } else {
                        plugin.getConfigD().setChecking(type, option);
                        sendMsg(c, MessageData.checkingChange(type, option));
                    }
                })
        ) {};
        return List.of(status, change);
    }
}
