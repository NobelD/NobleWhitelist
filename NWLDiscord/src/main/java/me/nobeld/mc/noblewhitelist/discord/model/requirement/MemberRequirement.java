package me.nobeld.mc.noblewhitelist.discord.model.requirement;

import me.nobeld.mc.noblewhitelist.discord.JDAManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda5.JDAInteraction;

public class MemberRequirement implements NWLRequirementInterface {
    private final JDAManager manager;
    public MemberRequirement(JDAManager manager) {
        this.manager = manager;
    }
    @Override
    public boolean evaluateRequirement(@NonNull CommandContext<JDAInteraction> commandContext) {
        return manager.parseUser(commandContext.sender().guild(), commandContext.sender().user()) != null;
    }
    @Override
    public @NonNull String errorMessage() {
        return "This command can only be executed by valid members.";
    }
}
