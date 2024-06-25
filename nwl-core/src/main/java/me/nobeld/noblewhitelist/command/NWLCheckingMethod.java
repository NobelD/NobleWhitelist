package me.nobeld.noblewhitelist.command;

import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;
import me.nobeld.noblewhitelist.model.checking.CheckingType;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.context.CommandContext;

import java.util.function.Function;

import static me.nobeld.noblewhitelist.model.command.BaseCommand.sendMsg;

public class NWLCheckingMethod {
    public static <T> void sendCheck(NWLData data, CommandContext<T> context, Function<T, Audience> mapper) {
        sendMsg(context, MessageData.statusNameCheck(data.getConfigD().checkName()), mapper);
        sendMsg(context, MessageData.statusUuidCheck(data.getConfigD().checkUUID()), mapper);
        sendMsg(context, MessageData.statusPermCheck(data.getConfigD().checkPerm()), mapper);
    }

    public static <T> void changeCheck(NWLData data, CommandContext<T> context, Function<T, Audience> mapper, CheckingType type, CheckingOption option) {
        if (data.getConfigD().getChecking(type) == option) {
            sendMsg(context, MessageData.checkingAlready(type, option), mapper);
        } else {
            data.getConfigD().setChecking(type, option);
            sendMsg(context, MessageData.checkingChange(type, option), mapper);
        }
    }
}
