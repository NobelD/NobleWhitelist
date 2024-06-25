package me.nobeld.noblewhitelist.discord.util;

import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
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

public class DiscordUtil {
    public static MessageCreateData createEmbed(MessageEmbed embed) {
        return new MessageCreateBuilder().addEmbeds(embed).build();
    }

    public static String parseMessage(String base, Map<String, String> map) {
        if (map == null || map.isEmpty()) return base;
        StrSubstitutor sub = new StrSubstitutor(map, "<", ">");
        String round = sub.replace(base);
        return sub.replace(round);
    }

    public static void replyMessageConfirm(NWLDsData data, SlashCommandInteractionEvent event, ConfigContainer<?> cont) {
        //TODO premium suggestion
        FlatFileSection sec = data.getMessageD().getMsgSec(cont);
        MessageCreateData msg = getMessage(data, cont, null);
        if (msg == null) return;

        FlatFileSection extra = sec.getSection("extra");
        Button accept = Button.success("nwl_suggest_yes", extra.get("accept", "yes")); //.withEmoji();
        Button denegate = Button.danger("nwl_suggest_no", extra.get("denied", "no"));

        event.reply(msg).setEphemeral(sec.get("ephemeral", false)).addActionRow(accept, denegate).queue();
    }

    /**
     * Create a message from a specified section of a file.
     *
     * @param data         the data to use
     * @param cont         the container of the message location
     * @param placeholders placeholders to use or null for none
     * @return the created message or null if no message was created
     */
    @Nullable
    public static MessageCreateData getMessage(NWLDsData data, ConfigContainer<?> cont, @Nullable Map<String, String> placeholders) {
        FlatFileSection sec = data.getMessageD().getMsgSec(cont);
        MessageCreateData msg;
        switch (sec.get("type", "null").toLowerCase()) {
            case "embed" -> msg = createEmbed(embedFromSection(data, sec, placeholders).build());
            case "mixed" ->
                    msg = messageFromSection(sec, placeholders).addEmbeds(embedFromSection(data, sec, placeholders).build()).build();
            case "message" -> msg = messageFromSection(sec, placeholders).build();
            default -> msg = null;
        }
        if (msg == null || (msg.getContent().isEmpty() && msg.getEmbeds().isEmpty() && msg.getFiles().isEmpty())) {
            data.logger().log(Level.WARNING, "A message has no content to reply (message or embed), an ephemeral dot will be used instead.");
            data.logger().log(Level.WARNING, "Message path: '" + cont.path() + "'");
        } else if (msg.getContent().length() > Message.MAX_CONTENT_LENGTH) {
            data.logger().log(Level.WARNING, "The content of a message (not the embed) exceeds the maximum length.");
            data.logger().log(Level.WARNING, "Message path: '" + cont.path() + "'");
            return MessageCreateBuilder.from(msg).setContent(msg.getContent().substring(0, Message.MAX_CONTENT_LENGTH)).build();
        }
        return msg;
    }

    @Nullable
    public static MessageCreateData getMessage(NWLDsData data, ConfigContainer<?> cont) {
        return getMessage(data, cont, null);
    }

    public static MessageCreateBuilder messageFromSection(FlatFileSection section, @Nullable Map<String, String> placeholders) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.addContent(parseMessage(section.getString("content"), placeholders));

        return builder;
    }

    public static MessageCreateBuilder messageFromSection(FlatFileSection section) {
        return messageFromSection(section, null);
    }

    public static EmbedBuilder embedFromSection(@Nullable NWLDsData data, FlatFileSection section, @Nullable Map<String, String> placeholders) {
        EmbedBuilder builder = new EmbedBuilder();

        FlatFileSection author = section.getSection("author");
        if (!author.singleLayerKeySet().isEmpty()) {
            try {
                if (author.getString("string") != null)
                    builder.setAuthor(parseMessage(author.getString("string"), placeholders), parseMessage(author.getString("url"), placeholders), parseMessage(author.getString("icon"), placeholders));
            } catch (IllegalStateException e) {
                if (data != null)
                    data.logger().log(Level.WARNING, "The author part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }
        FlatFileSection body = section.getSection("body");
        if (!body.singleLayerKeySet().isEmpty()) {
            try {
                String t = body.getString("title");
                if (t != null && (!t.isEmpty() || !t.isBlank()))
                    builder.setTitle(parseMessage(t, placeholders), parseMessage(body.getString("url"), placeholders));

                builder.setDescription(parseMessage(body.get("content", ""), placeholders));
                String col = body.getString("color");
                if (col != null) {
                    if (!col.startsWith("#")) {
                        col = "#" + col;
                    }
                    builder.setColor(Color.decode(col));
                }
            } catch (IllegalStateException | NumberFormatException e) {
                if (data != null)
                    data.logger().log(Level.WARNING, "The body part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }
        FlatFileSection field = section.getSection("field");
        if (!field.singleLayerKeySet().isEmpty()) {
            for (String f : field.singleLayerKeySet()) {
                try {
                    FlatFileSection subfield = field.getSection(f);
                    if (subfield.get("blank", false)) {
                        builder.addBlankField(true);
                    } else
                        builder.addField(parseMessage(subfield.getString("title"), placeholders), parseMessage(subfield.getString("content"), placeholders), subfield.get("inline", false));
                } catch (IllegalStateException e) {
                    if (data != null)
                        data.logger().log(Level.WARNING, "The field '" + f + "' of the message '" + section.getPathPrefix() + "' is not well formatted.");
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
                        builder.setImage(parseMessage(url, placeholders));
                        max++;
                    }
                }
                if (image.getString("thumbnail") != null)
                    builder.setThumbnail(parseMessage(image.getString("thumbnail"), placeholders));
            } catch (IllegalStateException e) {
                if (data != null)
                    data.logger().log(Level.WARNING, "The image part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }
        FlatFileSection footer = section.getSection("footer");
        if (!footer.singleLayerKeySet().isEmpty()) {
            try {
                if (footer.getString("title") != null)
                    builder.setFooter(parseMessage(footer.getString("title"), placeholders), parseMessage(footer.getString("icon"), placeholders));
                if (footer.get("timestamp", -1L) != -1)
                    builder.setTimestamp(Instant.ofEpochMilli(footer.getLong("timestamp")));
            } catch (IllegalStateException e) {
                if (data != null)
                    data.logger().log(Level.WARNING, "The footer part of the message '" + section.getPathPrefix() + "' is not well formatted.");
            }
        }

        return builder;
    }

    public static EmbedBuilder embedFromSection(FlatFileSection section, @Nullable Map<String, String> placeholders) {
        return embedFromSection(null, section, placeholders);
    }

    public static EmbedBuilder embedFromSection(FlatFileSection section) {
        return embedFromSection(null, section, null);
    }

    public static EmbedBuilder embedFromSection(@Nullable NWLDsData data, FlatFileSection section) {
        return embedFromSection(data, section, null);
    }

    public static void sendMessage(TextChannel channel, String content, Emoji... reaction) {
        if (channel == null || content == null) return;
        channel.sendMessage(new MessageCreateBuilder().addContent(content).build()).queue(message -> {
            for (Emoji s : reaction) {
                message.addReaction(s).queue();
            }
        });
    }

    public static void sendMessage(TextChannel channel, @Nullable MessageCreateData content, Emoji... reaction) {
        if (channel == null || content == null) return;
        channel.sendMessage(content).queue(message -> {
            for (Emoji s : reaction) {
                message.addReaction(s).queue();
            }
        });
    }
}
