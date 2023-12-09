package me.nobeld.minecraft.noblewhitelist.discord;

import me.nobeld.minecraft.noblewhitelist.api.AutoWhitelistEvent;
import me.nobeld.minecraft.noblewhitelist.api.WhitelistPassEvent;
import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

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
}
