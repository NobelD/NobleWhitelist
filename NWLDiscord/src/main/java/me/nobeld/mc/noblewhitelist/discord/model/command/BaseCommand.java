package me.nobeld.mc.noblewhitelist.discord.model.command;

import me.nobeld.mc.noblewhitelist.discord.config.MessageData;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDData;
import me.nobeld.mc.noblewhitelist.model.storage.ConfigContainer;
import me.nobeld.mc.noblewhitelist.model.PairData;
import me.nobeld.mc.noblewhitelist.util.UUIDUtil;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static me.nobeld.mc.noblewhitelist.discord.util.DiscordUtil.getMessage;

public abstract class BaseCommand {
    final Function<Command.Builder<JDAInteraction>, Command.Builder<JDAInteraction>> builder;
    public BaseCommand(Function<Command.Builder<JDAInteraction>, Command.Builder<JDAInteraction>> builder) {
        this.builder = builder;
    }
    public static void replyMsg(CommandContext<JDAInteraction> c, String msg, boolean ephemeral) {
        IReplyCallback r = c.sender().replyCallback();
        if (r == null) return;
        r.reply(msg).setEphemeral(ephemeral).queue();
    }
    public static void replyMsg(NWLDData data, CommandContext<JDAInteraction> c, ConfigContainer<?> cont, @Nullable Map<String, String> placeholders) {
        IReplyCallback r = c.sender().replyCallback();
        if (r == null) return;
        r.reply(getMessage(data, cont, placeholders)).setEphemeral(data.getMessageD().getMsgSec(cont).get("ephemeral", false)).queue();
    }
    public static void replyMsg(NWLDData data, CommandContext<JDAInteraction> c, ConfigContainer<?> cont) {
        replyMsg(data, c, cont, null);
    }
    public static boolean invalidInteraction(NWLDData data, CommandContext<JDAInteraction> c, ConfigContainer<String> role, ConfigContainer<String> channel) {
        if (data.getJDAManager().notRole(c.sender().guild(), c.sender().user(), role)) {
            replyMsg(data, c, MessageData.Error.noPermission);
            return true;
        }
        GenericCommandInteractionEvent i = c.sender().interactionEvent();
        if (i == null || data.getJDAManager().notChannel(c.sender().guild(), i.getChannel(), channel)) {
            replyMsg(data, c, MessageData.Error.incorrectChannel);
            return true;
        }
        return false;
    }
    public static boolean insufficientData(NWLDData data, CommandContext<JDAInteraction> c, Object val1, Object val2) {
        if (val1 == null && val2 == null) {
            replyMsg(data, c, MessageData.Error.insufficientData);
            return true;
        }
        return false;
    }
    public static boolean insufficientData(NWLDData data, CommandContext<JDAInteraction> c, Object val1, Object val2, long val3) {
        if (val1 == null && val2 == null && val3 < 0) {
            replyMsg(data, c, MessageData.Error.insufficientData);
            return true;
        }
        return false;
    }
    public static PairData<Boolean, UUID> invalidUUID(NWLDData data, CommandContext<JDAInteraction> c, @Nullable String uuid) {
        if (uuid == null) {
            return PairData.of(false, null);
        }
        UUID realuuid = UUIDUtil.parseUUID(uuid);
        if (realuuid == null) {
            replyMsg(data, c, MessageData.Error.invalidUuid);
            return PairData.of(true, null);
        }
        return PairData.of(false, realuuid);
    }
    public abstract void register(CommandManager<JDAInteraction> mng, Command.Builder<JDAInteraction> builder);
    public Command.Builder<JDAInteraction> getCommand(Command.Builder<JDAInteraction> base) {
        return builder.apply(base);
    }
}
