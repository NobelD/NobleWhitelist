package me.nobeld.noblewhitelist.discord.model.requirement;

import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.requirement.base.NWLRequirementInterface;
import me.nobeld.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;

public class RoleRequirement implements NWLRequirementInterface {
    private final NWLDsData data;
    private final ConfigContainer<String> role;
    public RoleRequirement(NWLDsData data, ConfigContainer<String> role) {
        this.data = data;
        this.role = role;
    }
    @Override
    public boolean evaluateRequirement(@NonNull CommandContext<JDAInteraction> commandContext) {
        return data.getJDAManager().hasRole(commandContext.sender().guild(), commandContext.sender().user(), role);
    }
    @Override
    @Nullable
    public MessageCreateData errorMessage() {
        return DiscordUtil.getMessage(data, MessageData.Error.noPermission);
    }
}
