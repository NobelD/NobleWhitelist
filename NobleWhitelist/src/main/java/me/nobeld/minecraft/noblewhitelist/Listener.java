package me.nobeld.minecraft.noblewhitelist;

import me.nobeld.minecraft.noblewhitelist.api.WhitelistPassEvent;
import me.nobeld.minecraft.noblewhitelist.config.ConfigFile;
import me.nobeld.minecraft.noblewhitelist.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.*;
import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.asLegacy;

public class Listener implements org.bukkit.event.Listener {
    private final NobleWhitelist plugin;
    public Listener(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Bukkit.getPluginManager().callEvent(new WhitelistPassEvent(player, ConfigFile.getConfig(ConfigFile.whitelistActive),
                plugin.whitelistChecker().canPass(player),
                plugin.whitelistData().registerSuccess(player),
                MessageData.kickMsg(player.getName()),
                event));
    }
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onWhitelist(WhitelistPassEvent event) {
        if (event.isCancelled()) return;
        if (!event.isWhitelistEnabled() || event.canPass()) return;

        if (hasPaper()) {
            event.getJoinEvent().disallow(PlayerLoginEvent.Result.KICK_WHITELIST, event.getMessage());
        } else {
            event.getJoinEvent().disallow(PlayerLoginEvent.Result.KICK_WHITELIST, asLegacy(event.getMessage()));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.whitelistChecker().changeData(player);
        if (!player.isOp() || !plugin.getUptChecker().canUpdate(ConfigFile.getConfig(ConfigFile.notifyUpdate), true)) return;

        plugin.playerMsg(player).sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>There is a new version available: <#C775FF>" + plugin.getUptChecker().getLatest(), null));
        plugin.playerMsg(player).sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>Download it at: <#75CDFF>https://www.github.com/NobelD/NobleWhitelist/releases", null));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.whitelistData().removeSuccess(player);
    }
}
