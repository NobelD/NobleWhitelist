package me.nobeld.noblewhitelist.discord.commands.admin;

import me.nobeld.noblewhitelist.discord.JDAManager;
import me.nobeld.noblewhitelist.discord.commands.InteractionListener;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.noblewhitelist.discord.util.DiscordUtil;
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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import static me.nobeld.noblewhitelist.discord.commands.CommandManager.REQUIREMENTS_KEY;
import static me.nobeld.noblewhitelist.discord.model.command.BaseCommand.*;
import static org.incendo.cloud.discord.jda5.JDAParser.channelParser;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

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
                .optional("content", stringParser())
                .optional("button", stringParser())
                .optional("type", enumParser(ButtonType.class))
                .optional("emoji", stringParser())
                .optional("channel", channelParser())
                .optional("message", stringParser())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminAdd))
                .handler(c -> {
                    if (invalidInteraction(data, c)) return;
                    GenericCommandInteractionEvent e = c.sender().interactionEvent();
                    assert e != null;

                    final Channel channel = Optional.ofNullable(e.getOption("channel"))
                            .<Channel>map(OptionMapping::getAsChannel).orElse(e.getChannel());
                    final Optional<String> strMsg = Optional.ofNullable(e.getOption("message")).map(OptionMapping::getAsString);
                    if (channel == null && strMsg.isEmpty()) {
                        replyMsg(c, "No location to send the message was found.", true);
                        return;
                    }

                    Emoji emoji = Optional.ofNullable(e.getOption("emoji")).map(o -> {
                        try {
                            return Emoji.fromFormatted(o.getAsString());
                        } catch (Exception ex) {
                            return null;
                        }
                    }).orElse(null);

                    Optional<String> desc = Optional.ofNullable(e.getOption("button")).map(OptionMapping::getAsString);
                    Optional<String> content = Optional.ofNullable(e.getOption("content")).map(OptionMapping::getAsString);

                    if (strMsg.isPresent()) {
                        RestAction<Message> message = DiscordUtil.getMessageFromLink(manager.getJDA(), strMsg.get());
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
                                ActionRow row = ActionRow.of(button.get());
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
