package me.nobeld.noblewhitelist;

import me.nobeld.noblewhitelist.api.event.AutoWhitelistEvent;
import me.nobeld.noblewhitelist.api.event.WhitelistPassEvent;
import me.nobeld.noblewhitelist.model.BPlayer;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.util.AdventureUtil;
import me.nobeld.noblewhitelist.config.ConfigData;
import net.kyori.adventure.text.Component;
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
        Component msg = data.getMessageD().kickMsg(player.getName());
        if (data.isBlocked()) {
            disallowJoin(event, msg);
            return;
        }
        Bukkit.getPluginManager().callEvent(new WhitelistPassEvent(player, data.getConfigD().get(ConfigData.WhitelistCF.whitelistActive),
                data.whitelistChecker().canPass(BPlayer.of(player)),
                data.whitelistChecker().createSuccess(BPlayer.of(player)),
                msg,
                event));
    }
    @EventHandler
    public void onWhitelist(WhitelistPassEvent event) {
        if (event.isCancelled()) return;
        if (!event.isWhitelistEnabled() || event.canPass()) return;
        disallowJoin(event.getJoinEvent(), event.getMessage());
    }
    @SuppressWarnings("deprecation")
    private void disallowJoin(PlayerLoginEvent event, Component msg) {
        if (NobleWhitelist.hasPaper()) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, msg);
        } else {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, AdventureUtil.asLegacy(msg));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!data.isBlocked()) {
            data.whitelistChecker().parseJoinData(BPlayer.of(player), d -> Bukkit.getPluginManager().callEvent(new AutoWhitelistEvent(player, d)));
        }
        if (
                (player.isOp() || player.hasPermission("noblewhitelist.admin.update")) &&
                data.getUptChecker().canUpdate(data.getConfigD().get(ConfigData.ServerCF.notifyUpdate), true)
        )
            data.getUptChecker().sendUpdate(data.getAdventure().playerAudience(player));
    }
}
