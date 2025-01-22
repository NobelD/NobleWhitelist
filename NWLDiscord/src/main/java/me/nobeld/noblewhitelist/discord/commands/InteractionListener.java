package me.nobeld.noblewhitelist.discord.commands;

import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class InteractionListener extends ListenerAdapter { // TEMP VIEW OF NEW FEATURE
    public static final String MENU_OPEN_BUTTON_ID = "noblewhitelist:input_modal/open_button";
    public static final String MODAL_MODAL_ID = "noblewhitelist:input_modal/modal_id";
    public static final String MENU_INPUT_ID = "noblewhitelist:input_modal/name_input";
    private final NWLDsData data;

    public InteractionListener(NWLDsData data) {
        this.data = data;
    }

    private void handleReply(IReplyCallback r, MessageCreateData data) {
        r.reply(data).setEphemeral(true).queue();
    }

    private void handleReply(IReplyCallback r, ConfigContainer<?> container) {
        handleReply(r, container, null);
    }

    private void handleReply(IReplyCallback r, ConfigContainer<?> container, Map<String, String> placeholders) {
        MessageCreateData msg = DiscordUtil.getMessage(data, container, placeholders);
        if (msg == null) {
            r.reply(".").setEphemeral(true).queue();
            return;
        }
        r.reply(msg).setEphemeral(data.getMessageD().getMsgSec(container).get("ephemeral", false)).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id = event.getButton().getId();
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (id == null || guild == null || member == null) return;
        if (guild.getIdLong() != data.getConfigD().get(ConfigData.serverID)) {
            handleReply(event, DiscordUtil.getMessage(data, MessageData.Error.invalidGuild));
            return;
        }

        long userid = member.getIdLong();

        if (id.equals(MENU_OPEN_BUTTON_ID)) {
            Channel channel = event.getChannel();
            final ConfigContainer<String> cont = ConfigData.CommandsOpt.selfAdd;
            if (!data.getJDAManager().matchChannel(guild, channel, cont)) {
                handleReply(event, DiscordUtil.getMessage(data, MessageData.Error.incorrectChannel));
                return;
            }
            if (!data.getJDAManager().hasRole(member, cont)) {
                handleReply(event, DiscordUtil.getMessage(data, MessageData.Error.noPermission));
                return;
            }
            Optional<WhitelistEntry> entryF = data.getNWL().whitelistData().getEntry(null, null, userid);
            if (entryF.isPresent()) {
                handleReply(event, MessageData.Error.selfNoMoreAccounts);
                return;
            }
            TextInput input = TextInput.create(MENU_INPUT_ID, "Player Name", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Place your name here")
                    .setMinLength(1)
                    .setMaxLength(100)
                    .build();

            Modal modal = Modal.create(MODAL_MODAL_ID, "Whitelist Menu")
                    .addComponents(ActionRow.of(input))
                    .build();
            event.replyModal(modal).queue();
        }
    }


    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String id = event.getModalId();
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (guild == null || member == null) return;
        long userid = member.getIdLong();
        if (id.equals(MODAL_MODAL_ID)) {
            try {
                Optional<WhitelistEntry> entryF = data.getNWL().whitelistData().getEntry(null, null, userid);
                if (entryF.isPresent()) {
                    handleReply(event, MessageData.Error.selfNoMoreAccounts);
                    return;
                }
                ModalMapping value = event.getInteraction().getValue(MENU_INPUT_ID);
                if (value == null) {
                    handleReply(event, MessageData.Error.invalidInteraction);
                    return;
                }
                String name = value.getAsString();
                Optional<WhitelistEntry> entry = data.getNWL().whitelistData().getEntry(name, null, userid);

                if (entry.isEmpty()) {
                    WhitelistEntry d = data.getNWL().whitelistData().registerAndSave(name, null, userid);
                    Map<String, String> m = data.getMessageD().baseHolder(d);

                    data.getJDAManager().manageRoleHandled(guild, d, m, true);
                    handleReply(event, MessageData.Command.selfAdd, m);
                    DiscordUtil.sendMessage(data.getJDAManager().getChannel(ConfigData.Channel.selfRegister), DiscordUtil.getMessage(data, MessageData.Channel.notifySelfAdd, m));
                } else
                    handleReply(event, MessageData.Error.selfAlready);
            } catch (Exception e) {
                data.logger().log(Level.SEVERE, "An error occurred while processing request modal.", e);
            }
        }
    }
}
