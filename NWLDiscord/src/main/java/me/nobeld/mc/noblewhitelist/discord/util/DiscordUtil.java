package me.nobeld.mc.noblewhitelist.discord.util;

import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDData;
import me.nobeld.mc.noblewhitelist.model.storage.ConfigContainer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;

import static me.nobeld.mc.noblewhitelist.discord.NWLDiscord.log;

public class DiscordUtil {
    public static MessageCreateData createEmbed(MessageEmbed embed) {
        return new MessageCreateBuilder().addEmbeds(embed).build();
    }
    private static String parse(String base, Map<String, String> map) {
        if (map == null || map.isEmpty()) return base;
        StrSubstitutor sub = new StrSubstitutor(map, "<", ">");
        String round = sub.replace(base);
        return sub.replace(round);
    }
    public static void replyMessageConfirm(NWLDData data, SlashCommandInteractionEvent event, ConfigContainer<?> cont) {
        //TODO premium suggestion
        FlatFileSection sec = data.getMessageD().getMsgSec(cont);
        MessageCreateData msg;
        switch (sec.get("type", "message").toLowerCase()) {
            case "embed" -> msg = MessageCreateData.fromEmbeds(embedFromSection(sec).build());
            case "mixed" -> msg = messageFromSection(sec).addEmbeds(embedFromSection(sec).build()).build();
            default -> msg = messageFromSection(sec).build();
        }
        FlatFileSection extra = sec.getSection("extra");
        Button accept = Button.success("nwl_suggest_yes", extra.get("accept", "yes"));
        Button denegate = Button.danger("nwl_suggest_no", extra.get("denied", "no"));

        event.reply(msg).setEphemeral(sec.get("ephemeral", false)).addActionRow(accept, denegate).queue();
    }
    public static MessageCreateData getMessage(NWLDData data, ConfigContainer<?> cont, @Nullable Map<String, String> placeholders) {
        FlatFileSection sec = data.getMessageD().getMsgSec(cont);
        MessageCreateData msg;
        switch (sec.get("type", "message").toLowerCase()) {
            case "embed" -> msg = MessageCreateData.fromEmbeds(embedFromSection(sec, placeholders).build());
            case "mixed" -> msg = messageFromSection(sec, placeholders).addEmbeds(embedFromSection(sec, placeholders).build()).build();
            default -> msg = messageFromSection(sec, placeholders).build();
        }
        return msg;
    }
    public static MessageCreateData getMessage(NWLDData data, ConfigContainer<?> cont) {
        return getMessage(data, cont, null);
    }
    public static MessageCreateBuilder messageFromSection(FlatFileSection section, @Nullable Map<String, String> placeholders) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(parse(section.getString("content"), placeholders));

        return builder;
    }
    public static MessageCreateBuilder messageFromSection(FlatFileSection section) {
        return messageFromSection(section,null);
    }
    public static EmbedBuilder embedFromSection(FlatFileSection section, @Nullable Map<String, String> placeholders) {
        EmbedBuilder builder = new EmbedBuilder();

        FlatFileSection author = section.getSection("author");
        if (!author.singleLayerKeySet().isEmpty()) {
            try {
                if (author.getString("string") != null)
                    builder.setAuthor(parse(author.getString("string"), placeholders), parse(author.getString("url"), placeholders), parse(author.getString("icon"), placeholders));
            } catch (IllegalStateException e) {
                log(Level.WARNING, "The author part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }
        FlatFileSection body = section.getSection("body");
        if (!body.singleLayerKeySet().isEmpty()) {
            try {
                String t = body.getString("title");
                if (t != null && (!t.isEmpty() || !t.isBlank())) builder.setTitle(parse(t, placeholders), parse(body.getString("url"), placeholders));

                builder.setDescription(parse(body.get("content", ""), placeholders));
                String col = body.getString("color");
                if (col != null) {
                    if (!col.startsWith("#")) {
                        col = "#" + col;
                    }
                    builder.setColor(Color.decode(col));
                }
            } catch (IllegalStateException | NumberFormatException e) {
                log(Level.WARNING, "The body part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }
        FlatFileSection field = section.getSection("field");
        if (!field.singleLayerKeySet().isEmpty()) {
            for (String f : field.singleLayerKeySet()) {
                try {
                    FlatFileSection subfield = field.getSection(f);
                    if (subfield.get("blank", false)) {
                        builder.addBlankField(true);
                    } else builder.addField(parse(subfield.getString("title"), placeholders), parse(subfield.getString("content"), placeholders), subfield.get("inline", false));
                } catch (IllegalStateException e) {
                    log(Level.WARNING, "The field '" + f + "' of the message '" + section.getPathPrefix() + "' is not well formatted.");
                }
            }
        }
        FlatFileSection image = section.getSection("image");
        if (!image.singleLayerKeySet().isEmpty()) {
            try {
                if (image.getStringList("url") != null) {
                    int max = 0;
                    for (String url : image.getStringList("url")) {
                        if (max >= 4) break;
                        builder.setImage(parse(url, placeholders));
                        max++;
                    }
                }
                if (image.getString("thumbnail") != null) builder.setThumbnail(parse(image.getString("thumbnail"), placeholders));
            } catch (IllegalStateException e) {
                log(Level.WARNING, "The image part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }
        FlatFileSection footer = section.getSection("footer");
        if (!footer.singleLayerKeySet().isEmpty()) {
            try {
                if (footer.getString("title") != null) builder.setFooter(parse(footer.getString("title"), placeholders), parse(footer.getString("icon"), placeholders));
                if (footer.get("timestamp", -1L) != -1) builder.setTimestamp(Instant.ofEpochMilli(footer.getLong("timestamp")));
            } catch (IllegalStateException e) {
                log(Level.WARNING, "The footer part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }

        return builder;
    }
    public static EmbedBuilder embedFromSection(FlatFileSection section) {
        return embedFromSection(section, null);
    }
    public static void sendMessage(TextChannel channel, String content, Emoji... reaction) {
        if (channel == null || content == null) return;
        channel.sendMessage(new MessageCreateBuilder().addContent(content).build()).queue(message -> {
            for (Emoji s : reaction) {
                message.addReaction(s).queue();
            }
        });
    }
    public static void sendMessage(TextChannel channel, MessageCreateData content, Emoji... reaction) {
        if (channel == null || content == null) return;
        channel.sendMessage(content).queue(message -> {
            for (Emoji s : reaction) {
                message.addReaction(s).queue();
            }
        });
    }
}
