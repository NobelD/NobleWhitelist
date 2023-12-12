package me.nobeld.minecraft.noblewhitelist.discord.commands.basic;

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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil.*;

public class BasicAdd implements SubCommand {
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
        return "add";
    }
    @Override
    public String getDescription() {
        return "Add your account to the whitelist by the name.";
    }
    @Override
    public List<CommandOption> getOptions() {
        return List.of(new CommandOption(OptionType.STRING, "name", "Name of the player to be added", false),
                new CommandOption(OptionType.STRING, "uuid", "Uuid of the player to be added", false));
    }
    @Override
    public void onCommand(InteractResult event) {
        if (event.getManager().notRole(event.getMember(), ConfigData.CommandsRole.selfAdd)) {
            DiscordUtil.replyMessage(event, MessageData.Error.noPermission);
            return;
        }
        if (event.getManager().notChannel(event, ConfigData.CommandsChannel.selfAdd)) {
            DiscordUtil.replyMessage(event, MessageData.Error.incorrectChannel);
            return;
        }
        long userid = event.getMember().getIdLong();
        Optional<PlayerWhitelisted> data2 = getPlugin().api().getWlData().getData(null, null, userid);
        if (data2.isPresent()) {
            event.reply("You can not register more accounts to the whitelist.").setEphemeral(true).queue();
            return;
        }

        String name = event.getOption("name").map(OptionMapping::getAsString).orElse(null);
        String uuid = event.getOption("uuid").map(OptionMapping::getAsString).orElse(null);
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

        if (data.isEmpty()) {
            PlayerWhitelisted d = getPlugin().api().getWlData().register(name, realuuid, userid);
            Map<String, String> m = MessageData.baseHolder(d);

            replyMessage(event, MessageData.Command.selfAdd, () -> m);
            sendMessage(event.getManager().getChannel(ConfigData.Channel.selfRegister), getMessage(MessageData.Channel.notifySelfAdd, () -> m));
            event.getManager().setWhitelistedRole(event.getGuild(), d, m, true);
        } else
            DiscordUtil.replyMessage(event, MessageData.Error.selfAlready);
    }
}
