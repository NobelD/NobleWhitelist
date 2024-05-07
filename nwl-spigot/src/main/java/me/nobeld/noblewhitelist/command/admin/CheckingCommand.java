package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.command.NWLCheckingMethod;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;
import me.nobeld.noblewhitelist.model.checking.CheckingType;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

import static org.incendo.cloud.parser.standard.EnumParser.enumParser;

public class CheckingCommand extends OptionCommand<CommandSender> {
    public CheckingCommand(NobleWhitelist plugin) {
        super(b -> b.literal("checking").permission("noblewhitelist.admin.checking"), commands(plugin));
    }
    private static List<BaseCommand<CommandSender>> commands(NobleWhitelist plugin) {
        SubCommand<CommandSender> status = new SubCommand<>(b -> b.literal("status")
                .handler(c -> NWLCheckingMethod.sendCheck(plugin, c, plugin.getAdventure()::senderAudience))
        );
        SubCommand<CommandSender> change = new SubCommand<>(b -> b
                .literal("set")
                .required("type", enumParser(CheckingType.class))
                .required("option", enumParser(CheckingOption.class))
                .handler(c -> {
                    CheckingType type = c.get("type");
                    CheckingOption option = c.get("option");
                    NWLCheckingMethod.changeCheck(plugin, c, plugin.getAdventure()::senderAudience, type, option);
                })
        );
        return List.of(status, change);
    }
}
