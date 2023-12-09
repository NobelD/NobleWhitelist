package me.nobeld.minecraft.noblewhitelist.discord;

import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.model.ConfigContainer;
import me.nobeld.minecraft.noblewhitelist.discord.commands.NWLCommands;
import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.essentialsx.discord.JDADiscordService;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

import static me.nobeld.minecraft.noblewhitelist.discord.NWLDiscord.log;
import static me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData.*;
import static me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData.Channel.startChannel;
import static me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData.Channel.stopChannel;
import static me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil.getMessage;
import static me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil.sendMessage;

public class JDAManager {
    private final boolean essentials;
    private JDADiscordService service;
    private final NWLDiscord plugin;
    private final String token;
    private JDA bot;
    private final Set<String> channels = new HashSet<>();
    private final List<CommandData> commandsList = new ArrayList<>();
    public JDAManager(NWLDiscord plugin) {
        this.plugin = plugin;
        essentials = plugin.hasEssentials();

        token = configFile().getString("discord.bot-token");
        if ((token == null || token.isBlank() || token.isEmpty()) && !essentials) {
            plugin.getLogger().log(Level.SEVERE, "There is no discord bot defined to use.");
            plugin.getLogger().log(Level.SEVERE, "Some features will be disabled.");
            return;
        }
        loadChannels();
        start();
        bot.addEventListener(new NWLCommands(this));
        addCommands(commandsList);
    }
    public List<CommandData> getCommandsList() {
        return commandsList;
    }
    public void addCommands(List<CommandData> list) {
        bot.updateCommands().addCommands(list).queue();
    }
    private void loadChannels() {
        channels.addAll(getSection(channelsID).singleLayerKeySet());
    }
    private void start() {
        if (essentials) {
            try {
                Field priv = plugin.getEssentials().getClass().getDeclaredField("jda");
                priv.setAccessible(true);
                service = (JDADiscordService) priv.get(plugin.getEssentials());
                bot = service.getJda();
                return;
            } catch (Exception e) {
                log(Level.SEVERE, "Can not load the discord bot from essentials, using default bot.");
                log(Level.SEVERE, e.getMessage());
            }
        }
        if (token == null || token.isEmpty() || token.isBlank()) {
            log(Level.SEVERE, "The bot token is missing, the bot will not be enabled.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        try {
            JDABuilder builder = JDABuilder.createDefault(token);
            bot = builder.build();
            bot.awaitReady();
        } catch (Exception e) {
            log(Level.SEVERE, "An error occurred while enabling the discord bot, be sure the token is valid.");
            log(Level.SEVERE, e.getMessage());
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        if (essentials) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> sendMessage(getChannel(startChannel), getMessage(MessageData.Channel.notifyStart)));
    }
    public void disable() {
        if (bot == null) return;
        if (essentials) return;
        sendMessage(getChannel(stopChannel), getMessage(MessageData.Channel.notifyStop));
    }
    public boolean notChannel(InteractResult event, ConfigContainer<String> cont) {
        List<String> channels = ConfigData.getList(cont);
        if (channels == null || channels.isEmpty()) return true;
        List<TextChannel> permittedChannels = new ArrayList<>();

        for (String c : channels) {
            TextChannel ch = getChannel(c);
            if (ch != null) permittedChannels.add(ch);
        }
        MessageChannelUnion c = event.getBaseEvent().getChannel();
        if (c.getType() != ChannelType.TEXT) return false;
        return !permittedChannels.contains(c.asTextChannel());
    }
    public boolean notRole(Member member, ConfigContainer<String> cont) {
        List<String> roles = ConfigData.getList(cont);
        if (roles == null || roles.isEmpty()) return true;
        List<Role> permittedRoles = new ArrayList<>();

        for (Map.Entry<String, List<Role>> entry : getConfigRoles().entrySet()) {
            for (String r : roles) {
                if (entry.getKey().equalsIgnoreCase(r)) permittedRoles.addAll(entry.getValue());
            }
        }
        return Collections.disjoint(member.getRoles(), permittedRoles);
    }
    private Map<String, List<Role>> getConfigRoles() {
        Map<String, List<Role>> roles = new HashMap<>();
        roles.put("user", getRoleType(roleUserID));
        roles.put("staff", getRoleType(roleStaffID));
        roles.put("admin", getRoleType(roleAdminID));
        return roles;
    }
    private List<Role> getRoleType(ConfigContainer<String> cont) {
        List<String> list = getList(cont);
        if (list == null) return Collections.emptyList();
        return list.stream().map(r -> {
            long id;
            try {
                id = Long.parseLong(r.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
            return bot.getRoleById(id);
        }).toList();
    }
    public TextChannel getChannel(ConfigContainer<String> cont) {
        String channel = get(cont);
        return getChannel(channel);
    }
    public TextChannel getChannel(String channel) {
        if (channel == null || channel.isEmpty() || channel.isBlank() || channel.equalsIgnoreCase("none")) return null;
        if (channel.startsWith("essentials.")) {
            return getEssentialsChannel(channel.replace("essentials.", ""));
        } else {
            return getNWLChannel(channel);
        }
    }
    public TextChannel getNWLChannel(String channel) {
        long id = -1;
        for (String s : channels) {
            if (!s.equalsIgnoreCase(channel)) continue;
            String i = getSection(channelsID).getString(s);
            if (i == null || i.isEmpty() || i.isBlank()) return null;
            try {
                id = Long.parseLong(i.trim());
            } catch (NumberFormatException e) {
                plugin.getLogger().log(Level.SEVERE, "Channel by name '" + s + "' has an invalid channel id.");
            }
            break;
        }
        if (id < 0) return null;
        return bot.getTextChannelById(id);
    }
    private TextChannel getEssentialsChannel(String channel) {
        if (!essentials) {
            plugin.getLogger().log(Level.SEVERE, "The bot tried to use an essentials channel but this option is not enabled.");
            return null;
        }
        return service.getChannel(channel, false);
    }
}
