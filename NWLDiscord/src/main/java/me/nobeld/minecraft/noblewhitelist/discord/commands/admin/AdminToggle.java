package me.nobeld.minecraft.noblewhitelist.discord.commands.admin;

import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandOption;
import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import me.nobeld.minecraft.noblewhitelist.util.UUIDUtil;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;

public class AdminToggle implements SubCommand {
    @Override
    public boolean isEphemeral() {
        return false;
    }
    @Override
    public boolean isDefer() {
        return false;
    }
    @Override
    public String getName() {
        return "toggle";
    }
    @Override
    public String getDescription() {
        return "Toggle an user if can join or not.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return List.of(new CommandOption(OptionType.BOOLEAN, "toggle", "Defines if the player can join or not", true),
                new CommandOption(OptionType.STRING, "name", "Name of the player to be toggled", false),
                new CommandOption(OptionType.STRING, "uuid", "Uuid of the player to be toggled", false),
                new CommandOption(OptionType.USER, "user", "User to be toggled.", false));
    }
    @Override
    public void onCommand(InteractResult event) {
        if (event.getManager().notRole(event.getMember(), ConfigData.CommandsRole.adminToggle)) {
            DiscordUtil.replyMessage(event, MessageData.Error.noPermission);
            return;
        }
        if (event.getManager().notChannel(event, ConfigData.CommandsChannel.adminToggle)) {
            DiscordUtil.replyMessage(event, MessageData.Error.incorrectChannel);
            return;
        }

        String name = event.getOption("name").map(OptionMapping::getAsString).orElse(null);
        String uuid = event.getOption("uuid").map(OptionMapping::getAsString).orElse(null);
        long userid = event.getOption("user").map(o -> o.getAsUser().getIdLong()).orElse(-1L);
        boolean toggle = event.getOption("toggle").map(OptionMapping::getAsBoolean).orElse(false);
        if (name == null && uuid == null) {
            DiscordUtil.replyMessage(event, MessageData.Error.insufficientData);
            return;
        }
        UUID realuuid = null;

        if (uuid != null) {
            realuuid = UUIDUtil.parseUUID(uuid);
            if (realuuid == null) {
                DiscordUtil.replyMessage(event, MessageData.Error.invalidUuid);
                return;
            }
        }
        Optional<PlayerWhitelisted> data = getPlugin().api().getWlData().getData(name, realuuid, userid);

        if (data.isPresent()) {
            if (data.get().isWhitelisted() == toggle)
                DiscordUtil.replyMessage(event, MessageData.Error.alreadyToggled, () -> MessageData.baseHolder(data.get()));
            else {
                getPlugin().api().getWlData().toggleJoinUser(data.get(), toggle);
                DiscordUtil.replyMessage(event, MessageData.Command.userToggled, () -> MessageData.baseHolder(data.get()));
            }
        } else
            DiscordUtil.replyMessage(event, MessageData.Error.userNotFound);
    }
}
