package me.nobeld.noblewhitelist;

import me.nobeld.noblewhitelist.api.event.AutoWhitelistEvent;
import me.nobeld.noblewhitelist.api.event.WhitelistPassEvent;
import me.nobeld.noblewhitelist.model.BPlayer;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.whitelist.SuccessData;
import me.nobeld.noblewhitelist.model.whitelist.VanillaWhitelistType;
import me.nobeld.noblewhitelist.util.AdventureUtil;
import me.nobeld.noblewhitelist.config.ConfigData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class Listener implements org.bukkit.event.Listener {
    private final NobleWhitelist data;
    public Listener(NobleWhitelist data) {
        this.data = data;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        switch (event.getResult()) {
            case ALLOWED -> {
                // proceed
            }
            case KICK_WHITELIST -> {
                if (!data.getConfigD().getEnumUpper(ConfigData.WhitelistCF.vanillaWhitelistType).shouldIgnore()) {
                    return;
                }
            }
            default -> {
                return;
            }
        }
        Player player = event.getPlayer();
        Component msg = data.getMessageD().kickMsg(player.getName());
        if (data.isBlocked()) {
            disallowJoin(event, msg);
            return;
        }
        PairData<SuccessData, Boolean> pair = data.whitelistChecker().canPass(BPlayer.of(player));
        Bukkit.getPluginManager().callEvent(new WhitelistPassEvent(
                player, data.getConfigD().get(ConfigData.WhitelistCF.whitelistActive),
                pair.getSecond(), pair.getFirst(), msg, event)
        );
    }
    @EventHandler
    public void onWhitelist(WhitelistPassEvent event) {
        if (event.isCancelled()) return;
        if (!event.isWhitelistEnabled() || event.canPass()) {
            event.getJoinEvent().allow();
            return;
        }
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
        if ((player.isOp() || player.hasPermission("noblewhitelist.admin.update"))) {
            Bukkit.getScheduler().runTaskAsynchronously(data, () ->
                    data.getUptChecker().sendStatus(data.getAdventure().playerAudience(player), data.getConfigD().get(ConfigData.ServerCF.notifyUpdate), true));
        }
    }
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        if (args[0].equalsIgnoreCase("/whitelist") && (player.isOp() || player.hasPermission("minecraft.command.whitelist"))) {
            if (data.getConfigD().getEnumUpper(ConfigData.WhitelistCF.vanillaWhitelistType) == VanillaWhitelistType.DISABLED) {
                event.setCancelled(true);
                data.getAdventure().playerAudience(player).sendMessage(
                        Component.text("This command is disabled because NobleWhitelist is installed!", NamedTextColor.RED));
            }
        }
    }
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if (event.isCancelled()) return;
        String[] args = event.getCommand().split(" ");
        if (args[0].equalsIgnoreCase("whitelist")) {
            if (data.getConfigD().getEnumUpper(ConfigData.WhitelistCF.vanillaWhitelistType) == VanillaWhitelistType.DISABLED) {
                event.setCancelled(true);
                data.getAdventure().senderAudience(event.getSender()).sendMessage(
                        Component.text("This command is disabled because NobleWhitelist is installed!", NamedTextColor.RED));
            }
        }
    }
}
