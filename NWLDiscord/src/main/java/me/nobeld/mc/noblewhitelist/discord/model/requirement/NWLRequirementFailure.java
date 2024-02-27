package me.nobeld.mc.noblewhitelist.discord.model.requirement;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.processors.requirements.RequirementFailureHandler;

public class NWLRequirementFailure implements RequirementFailureHandler<JDAInteraction, NWLRequirementInterface> {
    @Override
    public void handleFailure(@NonNull CommandContext<JDAInteraction> context, @NonNull NWLRequirementInterface requirement) {
        IReplyCallback rc = context.sender().replyCallback();
        if (rc != null) rc.reply(requirement.errorMessage()).setEphemeral(true).queue();
    }
}
