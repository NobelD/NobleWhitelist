package me.nobeld.noblewhitelist.discord.model.requirement;

import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.requirement.base.NWLRequirementInterface;
import me.nobeld.noblewhitelist.discord.util.DiscordUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;

public class MemberRequirement implements NWLRequirementInterface {
    private final NWLDsData data;

    public MemberRequirement(NWLDsData data) {
        this.data = data;
    }

    @Override
    public boolean evaluateRequirement(@NonNull CommandContext<JDAInteraction> commandContext) {
        return data.getJDAManager().parseUser(commandContext.sender().guild(), commandContext.sender().user()) != null;
    }

    @Override
    @Nullable
    public MessageCreateData errorMessage() {
        return DiscordUtil.getMessage(data, MessageData.Error.invalidMember);
    }
}
