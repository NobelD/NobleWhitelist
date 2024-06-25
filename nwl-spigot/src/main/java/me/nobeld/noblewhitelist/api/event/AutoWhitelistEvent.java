package me.nobeld.noblewhitelist.api.event;

import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AutoWhitelistEvent extends Event implements Cancellable {
    private boolean isCancelled;
    private final Player player;
    private final WhitelistEntry data;
    private static final HandlerList handlers = new HandlerList();

    public AutoWhitelistEvent(Player player, WhitelistEntry data) {
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

    public WhitelistEntry getDataRegistered() {
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
