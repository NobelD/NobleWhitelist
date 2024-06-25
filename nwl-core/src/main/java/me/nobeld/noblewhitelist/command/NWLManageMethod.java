package me.nobeld.noblewhitelist.command;

import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.command.BaseCommand;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.context.CommandContext;

import java.util.function.Function;

public class NWLManageMethod {
    public static <T> void toggle(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, boolean activate) {
        boolean actually = data.getConfigD().get(ConfigData.WhitelistCF.whitelistActive);

        if (activate == actually) {
            BaseCommand.sendMsg(context, MessageData.whitelistAlready(activate), mapper);
        } else {
            data.getConfigD().set(ConfigData.WhitelistCF.whitelistActive, activate);
            BaseCommand.sendMsg(context, MessageData.whitelistChanged(activate), mapper);
        }
    }

    public static <T> void permStatus(NWLData data, CommandContext<T> context, Function<T, Audience> mapper) {
        BaseCommand.sendMsg(context, MessageData.permissionInf1(data), mapper);
        BaseCommand.sendMsg(context, MessageData.permissionInf2(data), mapper);
    }

    public static <T> void permSet(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, int min) {
        data.getConfigD().set(ConfigData.ByPassCF.permissionMinimum, min);
        BaseCommand.sendMsg(context, MessageData.permissionChanged(min), mapper);
    }
}
