package me.nobeld.minecraft.noblewhitelist.discord.model;

import me.nobeld.minecraft.noblewhitelist.discord.JDAManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Optional;

import static me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil.createEmbed;

public class InteractResult {
    private final SlashCommandInteractionEvent event;
    private final boolean ephemeral;
    private final SubCommand subCommand;
    private final Member member;
    private final JDAManager manager;
    public InteractResult(JDAManager manager, Member member, SubCommand sub, SlashCommandInteractionEvent event) {
        this.member = member;
        this.subCommand = sub;
        this.event = event;
        this.manager = manager;
        ephemeral = sub.isEphemeral();
        if (sub.isDefer()) event.deferReply().setEphemeral(ephemeral).queue();
    }
    public JDAManager getManager() {
        return manager;
    }
    public SlashCommandInteractionEvent getBaseEvent() {
        return event;
    }
    public Member getMember() {
        return member;
    }
    public Guild getGuild() {
        return event.getGuild();
    }
    public Optional<OptionMapping> getOption(String name) {
        return Optional.ofNullable(event.getOption(name));
    }
    public ReplyCallbackAction reply(String message) {
        return event.reply(message).setEphemeral(ephemeral);
    }
    public ReplyCallbackAction reply(MessageCreateData message) {
        return event.reply(message).setEphemeral(ephemeral);
    }
    public ReplyCallbackAction replyEmbed(MessageEmbed embed, Emoji... reaction) {
        return event.reply(createEmbed(embed)).setEphemeral(ephemeral);
    }
}
