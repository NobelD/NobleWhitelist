package me.nobeld.noblewhitelist.discord;

import me.nobeld.noblewhitelist.api.event.AutoWhitelistEvent;
import me.nobeld.noblewhitelist.api.event.WhitelistPassEvent;
import me.nobeld.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.model.BPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class Listener implements org.bukkit.event.Listener {
    private final NWLDiscord data;
    public Listener(NWLDiscord data) {
        this.data = data;
    }
    @EventHandler
    public void onWhitelistEvent(WhitelistPassEvent event) {
        if (event.isCancelled()) return;
        if (!event.isWhitelistEnabled()) return;
        Map<String, String> map = new HashMap<>();
        map.put("name", event.getPlayer().getName());
        map.put("uuid", event.getPlayer().getUniqueId().toString());
        if (event.canPass()) {
            DiscordUtil.sendMessage(data.getJDAManager().getChannel(ConfigData.Channel.whitelistJoin), DiscordUtil.getMessage(data, MessageData.Channel.serverJoin, map));
        } else DiscordUtil.sendMessage(data.getJDAManager().getChannel(ConfigData.Channel.whitelistTry), DiscordUtil.getMessage(data, MessageData.Channel.serverTry, map));
    }
    @EventHandler
    public void onAutoWhitelist(AutoWhitelistEvent event) {
        if (event.isCancelled()) return;
        Map<String, String> map = new HashMap<>();
        map.put("name", event.getPlayer().getName());
        map.put("uuid", event.getPlayer().getUniqueId().toString());
        DiscordUtil.sendMessage(data.getJDAManager().getChannel(ConfigData.Channel.whitelistAuto), DiscordUtil.getMessage(data, MessageData.Channel.serverAuto, map));
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) return;
        Bukkit.getScheduler().runTaskAsynchronously(data, () ->
                data.getUptChecker().sendStatus(BPlayer.of(player).getAsAudience(), data.getConfigD().get(ConfigData.notifyUpdate), true));
    }
}
