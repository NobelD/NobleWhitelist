package me.nobeld.noblewhitelist.discord.model.command;

import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.requirement.ChannelRequirement;
import me.nobeld.noblewhitelist.discord.model.requirement.RoleRequirement;
import me.nobeld.noblewhitelist.discord.model.requirement.base.NWLRequirementInterface;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.util.UUIDUtil;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.processors.requirements.Requirements;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static me.nobeld.noblewhitelist.discord.util.DiscordUtil.getMessage;

public abstract class BaseCommand {
    final Function<Command.Builder<JDAInteraction>, Command.Builder<JDAInteraction>> builder;
    public BaseCommand(Function<Command.Builder<JDAInteraction>, Command.Builder<JDAInteraction>> builder) {
        this.builder = builder;
    }
    public static Requirements<JDAInteraction, NWLRequirementInterface> getRequirements(NWLDsData data, ConfigContainer<String> container) {
        return Requirements.of(
                data.getJDAManager().getCommandManager().getGuildRequirement(),
                data.getJDAManager().getCommandManager().getMemberRe(),
                new RoleRequirement(data, container),
                new ChannelRequirement(data, container)
        );
    }
    public static void replyMsg(CommandContext<JDAInteraction> c, String msg, boolean ephemeral) {
        IReplyCallback r = c.sender().replyCallback();
        if (r == null) return;
        r.reply(msg).setEphemeral(ephemeral).queue();
    }
    public static void replyMsg(NWLDsData data, CommandContext<JDAInteraction> c, ConfigContainer<?> cont, @Nullable Map<String, String> placeholders) {
        IReplyCallback r = c.sender().replyCallback();
        if (r == null) return;
        MessageCreateData msg = getMessage(data, cont, placeholders);
        if (msg == null) {
            r.reply(".").setEphemeral(true).queue();
            return;
        }
        r.reply(msg).setEphemeral(data.getMessageD().getMsgSec(cont).get("ephemeral", false)).queue();
    }
    public static void replyMsg(NWLDsData data, CommandContext<JDAInteraction> c, ConfigContainer<?> cont) {
        replyMsg(data, c, cont, null);
    }
    public static boolean noInputtedData(NWLDsData data, CommandContext<JDAInteraction> c, String val1, Object val2) {
        if (val1 == null && val2 == null) {
            replyMsg(data, c, MessageData.Error.noInputtedData);
            return true;
        }
        return false;
    }
    public static boolean noInputtedData(NWLDsData data, CommandContext<JDAInteraction> c, String val1, Object val2, long val3) {
        if (val1 == null && val2 == null && val3 < 0) {
            replyMsg(data, c, MessageData.Error.noInputtedData);
            return true;
        }
        return false;
    }
    public static boolean insufficientData(NWLDsData data, CommandContext<JDAInteraction> c, String val1, String val2, long val3) {
        int total = 0;
        if (val1 != null) total++;
        if (val2 != null) total++;
        if (val3 >= 0) total++;

        if (total < 2) {
            replyMsg(data, c, MessageData.Error.insufficientData);
            return true;
        }
        return false;
    }
    public static PairData<Boolean, UUID> invalidUUID(NWLDsData data, CommandContext<JDAInteraction> c, @Nullable String uuid) {
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
