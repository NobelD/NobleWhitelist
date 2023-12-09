package me.nobeld.minecraft.noblewhitelist.discord.util;

import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.model.ConfigContainer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

import java.awt.*;
import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;

import static me.nobeld.minecraft.noblewhitelist.discord.NWLDiscord.log;
import static me.nobeld.minecraft.noblewhitelist.discord.config.MessageData.getMsgSec;

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
    public static void replyMessageConfirm(InteractResult event, ConfigContainer<?> cont) {
        //TODO premium suggestion
        FlatFileSection sec = getMsgSec(cont);
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
    public static void replyMessage(InteractResult event, ConfigContainer<?> cont, Supplier<Map<String, String>> supplier) {
        event.reply(getMessage(cont, supplier)).setEphemeral(getMsgSec(cont).get("ephemeral", false)).queue();
    }
    public static void replyMessage(InteractResult event, ConfigContainer<?> cont) {
        replyMessage(event, cont, () -> null);
    }
    public static MessageCreateData getMessage(ConfigContainer<?> cont, Supplier<Map<String, String>> supplier) {
        FlatFileSection sec = getMsgSec(cont);
        MessageCreateData msg;
        switch (sec.get("type", "message").toLowerCase()) {
            case "embed" -> msg = MessageCreateData.fromEmbeds(embedFromSection(sec, supplier).build());
            case "mixed" -> msg = messageFromSection(sec, supplier).addEmbeds(embedFromSection(sec, supplier).build()).build();
            default -> msg = messageFromSection(sec, supplier).build();
        }
        return msg;
    }
    public static MessageCreateData getMessage(ConfigContainer<?> cont) {
        return getMessage(cont, () -> null);
    }
    public static MessageCreateBuilder messageFromSection(FlatFileSection section, Supplier<Map<String, String>> supplier) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(parse(section.getString("content"), supplier.get()));

        return builder;
    }
    public static MessageCreateBuilder messageFromSection(FlatFileSection section) {
        return messageFromSection(section, () -> null);
    }
    public static EmbedBuilder embedFromSection(FlatFileSection section, Supplier<Map<String, String>> supplier) {
        EmbedBuilder builder = new EmbedBuilder();
        Map<String, String> all = supplier.get();

        FlatFileSection author = section.getSection("author");
        if (!author.singleLayerKeySet().isEmpty()) {
            try {
                if (author.getString("string") != null)
                    builder.setAuthor(parse(author.getString("string"), all), parse(author.getString("url"), all), parse(author.getString("icon"), all));
            } catch (IllegalStateException e) {
                log(Level.WARNING, "The author part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }
        FlatFileSection body = section.getSection("body");
        if (!body.singleLayerKeySet().isEmpty()) {
            try {
                String t = body.getString("title");
                if (t != null && (!t.isEmpty() || !t.isBlank())) builder.setTitle(parse(t, all), parse(body.getString("url"), all));

                builder.setDescription(parse(body.get("content", ""), all));
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
                    } else builder.addField(parse(subfield.getString("title"), all), parse(subfield.getString("content"), all), subfield.get("inline", false));
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
                        builder.setImage(parse(url, all));
                        max++;
                    }
                }
                if (image.getString("thumbnail") != null) builder.setThumbnail(parse(image.getString("thumbnail"), all));
            } catch (IllegalStateException e) {
                log(Level.WARNING, "The image part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }
        FlatFileSection footer = section.getSection("footer");
        if (!footer.singleLayerKeySet().isEmpty()) {
            try {
                if (footer.getString("title") != null) builder.setFooter(parse(footer.getString("title"), all), parse(footer.getString("icon"), all));
                if (footer.get("timestamp", -1L) != -1) builder.setTimestamp(Instant.ofEpochMilli(footer.getLong("timestamp")));
            } catch (IllegalStateException e) {
                log(Level.WARNING, "The footer part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }

        return builder;
    }
    public static EmbedBuilder embedFromSection(FlatFileSection section) {
        return embedFromSection(section, () -> null);
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
