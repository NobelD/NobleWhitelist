package me.nobeld.minecraft.noblewhitelist;

import me.nobeld.minecraft.noblewhitelist.api.WhitelistPassEvent;
import me.nobeld.minecraft.noblewhitelist.data.WhitelistData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.*;
import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.convertMsg;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {
    private final NobleWhitelist plugin;
    public PlayerListener(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        WhitelistData.SuccessEnum type = WhitelistData.SuccessEnum.NOT_ACTIVE;
        Player player = event.getPlayer();
        Bukkit.getPluginManager().callEvent(new WhitelistPassEvent(player, type));
        if (!plugin.fileData().whitelistActive()) return;
        if (plugin.whitelistChecker().canPass(player)) return;

        if (hasPaper()) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, plugin.messages().kickMsg(player.getName()));
        } else {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, getLegacy(plugin.messages().kickMsg(player.getName())));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.whitelistChecker().changeData(player);
        if (!player.isOp() || !plugin.getUptChecker().canUpdate(true)) return;

        plugin.playerMsg(player).sendMessage(convertMsg("<prefix><#F1B65C>There is a new version available: <#C775FF>" + plugin.getUptChecker().getLatest(), null));
        plugin.playerMsg(player).sendMessage(convertMsg("<prefix><#F1B65C>Download it at <#75CDFF>https://www.github.com/NobelD/NobleWhitelist/releases", null));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.whitelistData().removeSuccess(player);
    }
    public String getLegacy(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
