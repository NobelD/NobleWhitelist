package me.nobeld.mc.noblewhitelist.discord;

import me.nobeld.mc.noblewhitelist.api.event.AutoWhitelistEvent;
import me.nobeld.mc.noblewhitelist.api.event.WhitelistPassEvent;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDData;
import me.nobeld.mc.noblewhitelist.discord.util.DiscordUtil;
import me.nobeld.mc.noblewhitelist.discord.config.ConfigData;
import me.nobeld.mc.noblewhitelist.discord.config.MessageData;
import me.nobeld.mc.noblewhitelist.model.BPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

import static me.nobeld.mc.noblewhitelist.discord.util.DiscordUtil.sendMessage;

public class Listener implements org.bukkit.event.Listener {
    private final NWLDData data;
    public Listener(NWLDData data) {
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
            sendMessage(data.getJDAManager().getChannel(ConfigData.Channel.whitelistJoin), DiscordUtil.getMessage(data, MessageData.Channel.serverJoin, map));
        } else sendMessage(data.getJDAManager().getChannel(ConfigData.Channel.whitelistTry), DiscordUtil.getMessage(data, MessageData.Channel.serverTry, map));
    }
    @EventHandler
    public void onAutoWhitelist(AutoWhitelistEvent event) {
        if (event.isCancelled()) return;
        Map<String, String> map = new HashMap<>();
        map.put("name", event.getPlayer().getName());
        map.put("uuid", event.getPlayer().getUniqueId().toString());
        sendMessage(data.getJDAManager().getChannel(ConfigData.Channel.whitelistAuto), DiscordUtil.getMessage(data, MessageData.Channel.serverAuto, map));
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp() || !data.getUptChecker().canUpdate(data.getConfigD().get(ConfigData.notifyUpdate), true)) return;

        data.getUptChecker().sendUpdate(BPlayer.of(player).getAsAudience());
    }
}
