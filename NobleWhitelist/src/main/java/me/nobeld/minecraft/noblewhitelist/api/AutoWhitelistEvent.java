package me.nobeld.minecraft.noblewhitelist.api;

import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AutoWhitelistEvent extends Event implements Cancellable {
    private boolean isCancelled;
    private final Player player;
    private final PlayerWhitelisted data;
    private static final HandlerList handlers = new HandlerList();
    public AutoWhitelistEvent(Player player, PlayerWhitelisted data) {
        this.player = player;
        this.data = data;
        isCancelled = false;
    }
    /**
     * @return the player of the event.
     */
    public Player getPlayer() {
        return player;
    }
    public PlayerWhitelisted getDataRegistered() {
        return data;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
