package me.nobeld.noblewhitelist.discord.commands.admin;

import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.noblewhitelist.discord.JDAManager;
import me.nobeld.noblewhitelist.discord.commands.InteractionListener;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.discord.jda5.JDAParser;
import org.incendo.cloud.discord.slash.DiscordChoices;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import static me.nobeld.noblewhitelist.discord.commands.CommandManager.REQUIREMENTS_KEY;
import static me.nobeld.noblewhitelist.discord.model.command.BaseCommand.*;
import static me.nobeld.noblewhitelist.discord.util.DiscordUtil.isNullEmpty;

public class AdminSend {
    private final NWLDsData data;
    private final JDAManager manager;

    public AdminSend(NWLDsData data) {
        this.data = data;
        this.manager = data.getJDAManager();
    }

    public enum ButtonType {
        PRIMARY(ButtonStyle.PRIMARY),
        SUCCESS(ButtonStyle.SUCCESS),
        SECONDARY(ButtonStyle.SECONDARY),
        DANGER(ButtonStyle.DANGER);
        private final ButtonStyle style;
        ButtonType(ButtonStyle style) {
            this.style = style;
        }

        public ButtonStyle getStyle() {
            return style;
        }
    }

    public SubCommand command() {
        return new SubCommand(b -> b.literal("send", Description.of("Experimental feature, little customizable."))  
                .optional("path", StringParser.greedyStringParser())
                .optional("content", StringParser.greedyStringParser())
                .optional("button", StringParser.greedyStringParser())
                .optional("type", StringParser.greedyStringParser(),
                          DiscordChoices.strings(Arrays.stream(ButtonType.values())
                                                    .map(v -> v.name().toLowerCase()).toList()))
                .optional("emoji", StringParser.greedyStringParser())
                .optional("channel", JDAParser.channelParser())
                .optional("edit", StringParser.greedyStringParser())
                .optional("disabled", BooleanParser.booleanParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminAdd))
                .handler(c -> {
                    if (invalidInteraction(data, c)) return;
                    GenericCommandInteractionEvent e = c.sender().interactionEvent();
                    assert e != null;

                    final Channel channel = Optional.ofNullable(e.getOption("channel"))
                            .<Channel>map(OptionMapping::getAsChannel).orElse(e.getChannel());
                    final Optional<String> strEdit = Optional.ofNullable(e.getOption("edit")).map(OptionMapping::getAsString);
                    if ((channel == null || channel.getType() != ChannelType.TEXT) && strEdit.isEmpty()) {
                        replyMsg(c, "No location to send the message was found.", true);
                        return;
                    }
                    final Optional<String> path = Optional.ofNullable(e.getOption("path")).map(OptionMapping::getAsString);
                    final Optional<Boolean> disabled = Optional.ofNullable(e.getOption("disabled")).map(OptionMapping::getAsBoolean);

                    if (path.isPresent()) {
                        ConfigContainer<String> view = new ConfigContainer<>(path.get(), "");
                        MessageCreateData msg = DiscordUtil.getMessage(data, view);
                        if (msg == null) {
                            replyMsg(c, "No message was found to use.", true);
                            return;
                        }
                        FlatFileSection section = data.getMessageD().getMsgSec(view);
                        String button = section.getRaw("extras.button", "");
                        String emoji = section.getRaw("extras.emoji", "");
                        String type = section.getRaw("extras.type", "");
                        boolean shouldEdit = strEdit.isPresent();
                        if (isNullEmpty(button)) {
                            button = shouldEdit ? null : "Whitelist";
                        }
                        final ActionRow row;
                        if (button != null) {
                            Button bt = Button.of(
                                    Optional.ofNullable(type).map(o -> ButtonType.valueOf(o.toUpperCase()))
                                            .orElse(ButtonType.SECONDARY).getStyle(),
                                    InteractionListener.MENU_OPEN_BUTTON_ID,
                                    button,
                                    Optional.ofNullable(emoji).map(DiscordUtil::toEmoji).orElse(null)
                                                 );
                            if (disabled.filter(o -> o).isPresent()) {
                                bt = bt.asDisabled();
                            }
                            row = ActionRow.of(bt);
                        } else {
                            row = null;
                        }
                        if (shouldEdit) {
                            RestAction<Message> message = DiscordUtil.getMessageFromLink(manager.getJDA(), strEdit.get());
                            if (message == null) {
                                replyMsg(c, "No message was found to edit.", true);
                                return;
                            }
                            message.queue(m -> {
                                if (m.getAuthor() != manager.getJDA().getSelfUser()) {
                                    replyMsg(c, "Cannot edit messages of another user!", true);
                                    return;
                                }
                                m.editMessage(MessageEditData.fromCreateData(msg)).queue();
                                if (row != null) {
                                    m.editMessageComponents(row).queue();
                                }
                                replyMsg(c, "Message was edited", true);
                            });
                        } else {
                            TextChannel ch = (TextChannel) channel;
                            ch.sendMessage(msg).addComponents(row).queue(v -> replyMsg(c, "Message was send", true));
                        }
                        return;
                    }

                    Emoji emoji = Optional.ofNullable(e.getOption("emoji")).map(OptionMapping::getAsString)
                            .map(DiscordUtil::toEmoji).orElse(null);

                    Optional<String> desc = Optional.ofNullable(e.getOption("button")).map(OptionMapping::getAsString);
                    Optional<String> content = Optional.ofNullable(e.getOption("content")).map(OptionMapping::getAsString);

                    if (strEdit.isPresent()) {
                        RestAction<Message> message = DiscordUtil.getMessageFromLink(manager.getJDA(), strEdit.get());
                        if (message == null) {
                            replyMsg(c, "No message was found to edit", true);
                            return;
                        }
                        Optional<MessageEditData> action = content.map(MessageEditData::fromContent);
                        Optional<Button> button = desc.map(s ->
                            Button.of(Optional.ofNullable(e.getOption("type")).map(o -> ButtonType.valueOf(o.getAsString().toUpperCase()))
                                              .orElse(ButtonType.SECONDARY).getStyle(), InteractionListener.MENU_OPEN_BUTTON_ID, s, emoji));
                        message.queue(m -> {
                            if (m.getAuthor() != manager.getJDA().getSelfUser()) {
                                replyMsg(c, "Cannot edit messages of another user!", true);
                                return;
                            }
                            Consumer<Object> suc = m1 -> replyMsg(c, "The message was edited.", true);
                            Consumer<Throwable> er = e1 -> {
                                replyMsg(c, "Could not edit message, check console for more details.", true);
                                data.logger().log(Level.WARNING, "Message could not be edited: " + m.getJumpUrl(), e);
                            };
                            if (button.isPresent()) {
                                Button bt = button.get();
                                if (disabled.filter(o -> o).isPresent()) {
                                    bt = bt.asDisabled();
                                }
                                ActionRow row = ActionRow.of(bt);
                                if (action.isEmpty()) {
                                    m.editMessageComponents(row).queue(suc, er);
                                } else {
                                    m.editMessage(action.get()).and(m.editMessageComponents(row)).queue(suc, er);
                                }
                            } else {
                                action.ifPresent(me -> m.editMessage(me).queue(suc, er));
                            }
                        });
                    } else {
                        if (channel.getType() != ChannelType.TEXT) {
                            replyMsg(c, "Invalid channel", true);
                            return;
                        }
                        TextChannel ch = (TextChannel) channel;
                        String d = content.orElse("Whitelist");
                        Button button = Button.of(Optional.ofNullable(e.getOption("type"))
                                            .map(o -> ButtonType.valueOf(o.getAsString().toUpperCase()))
                                            .orElse(ButtonType.SECONDARY).getStyle(), InteractionListener.MENU_OPEN_BUTTON_ID, desc.orElse("Register to whitelist"), emoji);

                        MessageCreateData data = new MessageCreateBuilder().addContent(d).addComponents(ActionRow.of(button)).build();
                        ch.sendMessage(data).queue();
                        replyMsg(c, "Message was send", true);
                    }
                })
        );
    }
}
