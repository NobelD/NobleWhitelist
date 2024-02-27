package me.nobeld.mc.noblewhitelist.discord.model.requirement;

import me.nobeld.mc.noblewhitelist.discord.config.ConfigData;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDData;
import net.dv8tion.jda.api.entities.Guild;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;

public class GuildRequirement implements NWLRequirementInterface {
    private final NWLDData data;
    public GuildRequirement(NWLDData data) {
        this.data = data;
    }
    @Override
    public boolean evaluateRequirement(@NonNull CommandContext<JDAInteraction> commandContext) {
        Guild guild = commandContext.sender().guild();
        return guild != null && guild.getIdLong() == data.getConfigD().get(ConfigData.serverID);
    }
    @Override
    public @NonNull String errorMessage() {
        return "This command was executed on an different or invalid guild.";
    }
}
