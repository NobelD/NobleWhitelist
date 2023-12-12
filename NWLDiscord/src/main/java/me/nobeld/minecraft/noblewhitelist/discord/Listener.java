package me.nobeld.minecraft.noblewhitelist.discord;

import me.nobeld.minecraft.noblewhitelist.api.AutoWhitelistEvent;
import me.nobeld.minecraft.noblewhitelist.api.WhitelistPassEvent;
import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.util.ServerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil.getMessage;
import static me.nobeld.minecraft.noblewhitelist.discord.util.DiscordUtil.sendMessage;

public class Listener implements org.bukkit.event.Listener {
    private final NWLDiscord plugin;
    public Listener(NWLDiscord plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onWhitelistEvent(WhitelistPassEvent event) {
        if (event.isCancelled()) return;
        if (!event.isWhitelistEnabled()) return;
        Map<String, String> map = new HashMap<>();
        map.put("name", event.getPlayer().getName());
        map.put("uuid", event.getPlayer().getUniqueId().toString());
        if (event.canPass()) {
            sendMessage(plugin.getJDAManager().getChannel(ConfigData.Channel.whitelistJoin), getMessage(MessageData.Channel.serverJoin, () -> map));
        } else sendMessage(plugin.getJDAManager().getChannel(ConfigData.Channel.whitelistTry), getMessage(MessageData.Channel.serverTry, () -> map));
    }
    @EventHandler
    public void onAutoWhitelist(AutoWhitelistEvent event) {
        if (event.isCancelled()) return;
        sendMessage(plugin.getJDAManager().getChannel(ConfigData.Channel.whitelistAuto), getMessage(MessageData.Channel.serverAuto, () -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", event.getPlayer().getName());
            map.put("uuid", event.getPlayer().getUniqueId().toString());
            return map;
        }));
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp() || !plugin.getUptChecker().canUpdate(ConfigData.get(ConfigData.notifyUpdate), true)) return;

        getPlugin().playerMsg(player).sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>There is a new version available for the <gold>Discord Integration: <#C775FF>" + plugin.getUptChecker().getLatest(), null));
        getPlugin().playerMsg(player).sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>Download it at: <#75CDFF>https://www.github.com/NobelD/NobleWhitelist/releases", null));
    }
}
