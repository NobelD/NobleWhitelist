package me.nobeld.noblewhitelist.discord.model.requirement.base;

import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.processors.requirements.Requirement;

public interface NWLRequirementInterface extends Requirement<JDAInteraction, NWLRequirementInterface> {
    @Nullable MessageCreateData errorMessage();
}
