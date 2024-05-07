package me.nobeld.noblewhitelist.discord.model.requirement.base;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.processors.requirements.RequirementFailureHandler;

public class NWLRequirementFailure implements RequirementFailureHandler<JDAInteraction, NWLRequirementInterface> {
    @Override
    public void handleFailure(@NonNull CommandContext<JDAInteraction> context, @NonNull NWLRequirementInterface requirement) {
        IReplyCallback rc = context.sender().replyCallback();
        if (rc != null) {
            MessageCreateData msg = requirement.errorMessage();
            if (msg == null) rc.reply(".").setEphemeral(true).queue();
            else rc.reply(msg).setEphemeral(true).queue();
        }
    }
}
