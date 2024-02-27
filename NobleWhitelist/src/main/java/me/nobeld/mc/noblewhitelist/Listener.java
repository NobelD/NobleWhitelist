package me.nobeld.mc.noblewhitelist;

import me.nobeld.mc.noblewhitelist.api.event.AutoWhitelistEvent;
import me.nobeld.mc.noblewhitelist.api.event.WhitelistPassEvent;
import me.nobeld.mc.noblewhitelist.model.BPlayer;
import me.nobeld.mc.noblewhitelist.model.base.NWLData;
import me.nobeld.mc.noblewhitelist.util.AdventureUtil;
import me.nobeld.mc.noblewhitelist.config.ConfigData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listener implements org.bukkit.event.Listener {
    private final NWLData data;
    public Listener(NWLData data) {
        this.data = data;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Bukkit.getPluginManager().callEvent(new WhitelistPassEvent(player, data.getConfigD().get(ConfigData.WhitelistCF.whitelistActive),
                data.whitelistChecker().canPass(BPlayer.of(player)),
                data.whitelistChecker().registerSuccess(BPlayer.of(player)),
                data.getMessageD().kickMsg(player.getName()),
                event));
    }
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onWhitelist(WhitelistPassEvent event) {
        if (event.isCancelled()) return;
        if (!event.isWhitelistEnabled() || event.canPass()) return;

        if (NobleWhitelist.hasPaper()) {
            event.getJoinEvent().disallow(PlayerLoginEvent.Result.KICK_WHITELIST, event.getMessage());
        } else {
            event.getJoinEvent().disallow(PlayerLoginEvent.Result.KICK_WHITELIST, AdventureUtil.asLegacy(event.getMessage()));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        data.whitelistChecker().parseJoinData(BPlayer.of(player), d -> Bukkit.getPluginManager().callEvent(new AutoWhitelistEvent(player, d)));
        if (!player.isOp() || !data.getUptChecker().canUpdate(data.getConfigD().get(ConfigData.ServerCF.notifyUpdate), true)) return;

        data.getUptChecker().sendUpdate(data.getAdventure().playerAudience(player));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        data.whitelistChecker().removeSuccess(BPlayer.of(player));
    }
}
