package me.nobeld.mc.noblewhitelist.discord.model.requirement;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.processors.requirements.Requirement;

public interface NWLRequirementInterface extends Requirement<JDAInteraction, NWLRequirementInterface> {
    @NonNull String errorMessage();
}
