package me.nobeld.noblewhitelist.discord.model.requirement;

import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.requirement.base.NWLRequirementInterface;
import me.nobeld.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;

public class ChannelRequirement implements NWLRequirementInterface {
    private final NWLDsData data;
    private final ConfigContainer<String> channel;

    public ChannelRequirement(NWLDsData data, ConfigContainer<String> channel) {
        this.data = data;
        this.channel = channel;
    }

    @Override
    public boolean evaluateRequirement(@NonNull CommandContext<JDAInteraction> commandContext) {
        GenericCommandInteractionEvent i = commandContext.sender().interactionEvent();
        return i != null && data.getJDAManager().matchChannel(commandContext.sender().guild(), i.getChannel(), channel);
    }

    @Override
    @Nullable
    public MessageCreateData errorMessage() {
        return DiscordUtil.getMessage(data, MessageData.Error.incorrectChannel);
    }
}
