package me.nobeld.noblewhitelist.discord.model.requirement;

import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.requirement.base.NWLRequirementInterface;
import me.nobeld.noblewhitelist.discord.util.DiscordUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;

public class GuildRequirement implements NWLRequirementInterface {
    private final NWLDsData data;
    public GuildRequirement(NWLDsData data) {
        this.data = data;
    }
    @Override
    public boolean evaluateRequirement(@NonNull CommandContext<JDAInteraction> commandContext) {
        Guild guild = commandContext.sender().guild();
        return guild != null && guild.getIdLong() == data.getConfigD().get(ConfigData.serverID);
    }
    @Override
    @Nullable
    public MessageCreateData errorMessage() {
        return DiscordUtil.getMessage(data, MessageData.Error.invalidGuild);
    }
}
