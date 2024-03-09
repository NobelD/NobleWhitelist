package me.nobeld.noblewhitelist.command.admin;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import me.nobeld.noblewhitelist.model.command.OptionCommand;
import me.nobeld.noblewhitelist.model.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.context.CommandContext;

import java.util.ArrayList;
import java.util.List;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class ManageCommand {
    private static void toggleStatus(NobleWhitelist plugin, CommandContext<CommandSender> ctx, boolean activate) {
        boolean actually = plugin.getConfigD().get(ConfigData.WhitelistCF.whitelistActive);

        if (activate == actually) {
            BaseCommand.sendMsg(ctx, MessageData.whitelistAlready(activate));
        } else {
            plugin.getConfigD().set(ConfigData.WhitelistCF.whitelistActive, activate);
            BaseCommand.sendMsg(ctx, MessageData.whitelistChanged(activate));
        }
    }
    public static List<BaseCommand> commands(NobleWhitelist plugin) {
        List<BaseCommand> commands = new ArrayList<>();
        commands.add(new SubCommand(b -> b.literal("on").permission("noblewhitelist.admin.on")
                .handler(c -> toggleStatus(plugin, c, true))
        ) {
        });
        commands.add(new SubCommand(b -> b.literal("off").permission("noblewhitelist.admin.off")
                .handler(c -> toggleStatus(plugin, c, false))
        ) {
        });
        commands.add(new Perm(plugin));
        return commands;
    }
    private static class Perm extends OptionCommand{
        public Perm(NobleWhitelist plugin) {
            super(b -> b.literal("perm").permission("noblewhitelist.admin.permission"), commands(plugin));
        }
        private static List<BaseCommand> commands(NobleWhitelist plugin) {
            SubCommand permStatus = new SubCommand(b -> b.literal("status")
                    .handler(c -> {
                        BaseCommand.sendMsg(c, MessageData.permissionInf1(plugin));
                        BaseCommand.sendMsg(c, MessageData.permissionInf2(plugin));
                    })
            ) {
            };
            SubCommand permChange = new SubCommand(b -> b.literal("set")
                    .required("minimum", integerParser(-1))
                    .handler(c -> {
                        final int min = c.get("minimum");
                        plugin.getConfigD().set(ConfigData.WhitelistCF.permissionMinimum, min);
                        BaseCommand.sendMsg(c, MessageData.permissionChanged(min));
                    })
            ) {
            };
            return List.of(permStatus, permChange);
        }
    }
}
