package me.nobeld.noblewhitelist.discord;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.discord.commands.CommandManager;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.noblewhitelist.model.storage.ConfigContainer;
import me.nobeld.noblewhitelist.util.AdventureUtil;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;
import java.util.logging.Level;

public class JDAManager {
    private final NWLDsData data;
    private final String token;
    private JDA bot;
    private final Set<String> channels = new HashSet<>();
    private CommandManager cmdManager = null;
    public JDAManager(NWLDsData data, ConfigData config) {
        this.data = data;
        token = config.configFile().getString("discord.bot-token");
        if ((token == null || token.isBlank() || token.isEmpty())) {
            data.logger().log(Level.SEVERE, "There is no discord bot defined to use.");
            data.logger().log(Level.SEVERE, "Some features will be disabled.");
            return;
        }
        loadChannels(config);
        start();
    }
    public void enableCommands() {
        cmdManager = new CommandManager(data);
        cmdManager.loadCommands();
        bot.addEventListener(cmdManager.getCloudManager().createListener());
        bot.updateCommands().queue();

        Guild guild = bot.getGuildById(data.getConfigD().get(ConfigData.serverID));
        if (guild == null) {
            NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><yellow>No server was found, commands will not be registered ."));
            return;
        }
        cmdManager.registerCommands(guild);
    }
    public CommandManager getCommandManager() {
        return cmdManager;
    }
    private void loadChannels(ConfigData config) {
        channels.addAll(config.getSection(ConfigData.channelsID).singleLayerKeySet());
    }
    private void start() {
        if (token == null || token.isEmpty() || token.isBlank()) {
            NWLDiscord.log(Level.SEVERE, "The bot token is missing, the bot will not be enabled.");
            data.disable();
            return;
        }
        try {
            NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><yellow>Loading Discord Bot!"));
            JDABuilder builder = JDABuilder.createDefault(token);
            bot = builder.build();
            bot.awaitReady();
            NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Loaded Discord Bot!"));
        } catch (Exception e) {
            NWLDiscord.log(Level.SEVERE, "An error occurred while enabling the discord bot, be sure the token is valid.");
            NWLDiscord.log(Level.SEVERE, e.getMessage());
            data.disable();
            return;
        }
        data.enableMsg(() -> DiscordUtil.sendMessage(getChannel(ConfigData.Channel.startChannel), DiscordUtil.getMessage(data, MessageData.Channel.notifyStart)));
    }
    public void disable() {
        if (bot == null) return;
        DiscordUtil.sendMessage(getChannel(ConfigData.Channel.stopChannel), DiscordUtil.getMessage(data, MessageData.Channel.notifyStop));
    }
    public boolean matchChannel(@Nullable Guild guild, Channel channel, ConfigContainer<String> cont) {
        if (guild == null) return false;
        TextChannel ch = guild.getTextChannelById(channel.getIdLong());
        if (ch == null) return false;
        return matchChannel(ch, cont);
    }
    public boolean matchChannel(TextChannel channel, ConfigContainer<String> cont) {
        List<String> channels = data.getConfigD().getSection(cont).getStringList("channel");
        if (channels == null || channels.isEmpty()) return false;
        List<TextChannel> permittedChannels = new ArrayList<>();

        for (String c : channels) {
            TextChannel ch = getChannel(c);
            if (ch != null) permittedChannels.add(ch);
        }
        return permittedChannels.contains(channel);
    }
    @Nullable
    public Member parseUser(@Nullable Guild guild, User user) {
        if (guild == null || user == null) return null;
        return guild.getMember(user);
    }
    public boolean hasRole(@Nullable Guild guild, User user, ConfigContainer<String> cont) {
        Member member = parseUser(guild, user);
        if (member == null) return false;
        return hasRole(member, cont);
    }
    public boolean hasRole(Member member, ConfigContainer<String> cont) {
        List<String> roles = data.getConfigD().getSection(cont).getStringList("role");
        if (roles == null || roles.isEmpty()) return false;
        if (data.getConfigD().get(ConfigData.roleEveryone) && roles.contains("everyone")) {
            return true;
        }
        List<Role> permittedRoles = new ArrayList<>();

        for (Map.Entry<String, List<Role>> entry : getConfigRoles().entrySet()) {
            for (String r : roles) {
                if (entry.getKey().equalsIgnoreCase(r)) permittedRoles.addAll(entry.getValue());
            }
        }
        return !Collections.disjoint(member.getRoles(), permittedRoles);
    }
    private Map<String, List<Role>> getConfigRoles() {
        Map<String, List<Role>> roles = new HashMap<>();
        roles.put("user", getRoleType(ConfigData.roleUserID));
        roles.put("staff", getRoleType(ConfigData.roleStaffID));
        roles.put("admin", getRoleType(ConfigData.roleAdminID));
        return roles;
    }
    private List<Role> getRoleType(ConfigContainer<String> cont) {
        List<String> list = data.getConfigD().getList(cont);
        if (list == null) return Collections.emptyList();
        return list.stream().map(r -> {
            long id;
            try {
                id = Long.parseLong(r.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
            return bot.getRoleById(id);
        }).filter(Objects::nonNull).toList();
    }
    public void setWhitelistedRole(@Nullable Guild guild, WhitelistEntry data, @Nullable Map<String, String> placeholders, boolean add) {
        if (guild == null || !data.hasDiscord()) return;
        Member member = guild.getMemberById(data.getDiscordID());
        if (member == null) return;
        setWhitelistedRole(guild, member, placeholders, add);
    }
    public void setWhitelistedRole( @NotNull Guild server, @NotNull Member member, @Nullable Map<String, String> placeholders, boolean add) {
        if (add && !data.getConfigD().get(ConfigData.giveWlRole)) return;
        else if (!add && !data.getConfigD().get(ConfigData.removeWlRole)) return;

        Role role = getWhitelistedRole();
        if (role == null || Collections.disjoint(member.getRoles(), Collections.singletonList(role))) return;

        if (placeholders != null) {
            placeholders.put("role-id", String.valueOf(role.getIdLong()));
            placeholders.put("role-mention", role.getAsMention());
        } else
            placeholders = Map.of(
                    "member", String.valueOf(member.getIdLong()),
                    "member-id", String.valueOf(member.getIdLong()),
                    "role-id", String.valueOf(role.getIdLong()),
                    "role-mention", role.getAsMention()
            );

        if (add) {
            server.addRoleToMember(member, role).reason("Added whitelisted role by register or link.").queue();

            for (Role r : getRoleType(ConfigData.roleSubWhitelistedID)) {
                server.addRoleToMember(member, r).reason("Sub whitelist roles.").queue();
            }
            DiscordUtil.sendMessage(getChannel(ConfigData.Channel.roleAdd), DiscordUtil.getMessage(data, MessageData.Channel.notifyRoleAdd, placeholders));

        } else {
            server.removeRoleFromMember(member, role).reason("Removed whitelisted role by unregister or unlink.").queue();

            for (Role r : getRoleType(ConfigData.roleSubWhitelistedID)) {
                server.removeRoleFromMember(member, r).reason("Sub whitelist roles.").queue();
            }
            DiscordUtil.sendMessage(getChannel(ConfigData.Channel.roleRemove), DiscordUtil.getMessage(data, MessageData.Channel.notifyRoleRemove, placeholders));
        }
    }
    @Nullable
    public Role getWhitelistedRole() {
        long id = data.getConfigD().get(ConfigData.roleWhitelistedID);
        if (id < 0) return null;
        return bot.getRoleById(id);
    }
    public TextChannel getChannel(ConfigContainer<String> cont) {
        String channel = data.getConfigD().get(cont);
        return getChannel(channel);
    }
    public TextChannel getChannel(String channel) {
        if (channel == null || channel.isEmpty() || channel.isBlank() || channel.equalsIgnoreCase("none")) return null;
        return getNWLChannel(channel);
    }
    public TextChannel getNWLChannel(String channel) {
        long id = -1;
        for (String s : channels) {
            if (!s.equalsIgnoreCase(channel)) continue;
            String i = data.getConfigD().getSection(ConfigData.channelsID).getString(s);
            if (i == null || i.isEmpty() || i.isBlank()) return null;
            try {
                id = Long.parseLong(i.trim());
            } catch (NumberFormatException e) {
                data.logger().log(Level.SEVERE, "Channel by name '" + s + "' has an invalid channel id.");
            }
            break;
        }
        if (id < 0) return null;
        return bot.getTextChannelById(id);
    }
}
