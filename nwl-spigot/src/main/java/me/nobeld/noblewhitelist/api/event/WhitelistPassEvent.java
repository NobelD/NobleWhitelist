package me.nobeld.noblewhitelist.api.event;

import me.nobeld.noblewhitelist.model.whitelist.SuccessEnum;
import me.nobeld.noblewhitelist.model.whitelist.SuccessData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

public class WhitelistPassEvent extends Event implements Cancellable {
    private boolean isCancelled;
    private final Player player;
    private final boolean enabled;
    private boolean canPass;
    private final SuccessEnum type;
    private final SuccessData data;
    private final PlayerLoginEvent event;
    private Component message;
    private static final HandlerList handlers = new HandlerList();
    /** Create a new Whitelist pass event which contains data about the player join. */
    public WhitelistPassEvent(Player player, boolean enabled, boolean canPass, SuccessData type, Component message, PlayerLoginEvent joinEvent) {
        this.player = player;
        this.enabled = enabled;
        this.canPass = canPass;
        this.data = type;
        this.type = type.successEnum();
        this.message = message;
        this.event = joinEvent;
    }
    /**
     * @return the player of the event.
     */
    public Player getPlayer() {
        return player;
    }
    public boolean isWhitelistEnabled() {
        return enabled;
    }
    /**
     * @return the success type.
     */
    public SuccessEnum getType() {
        return type;
    }
    public SuccessData getData() {
        return data;
    }
    public Component getMessage() {
        return message;
    }
    public boolean canPass() {
        return canPass;
    }
    public void setCanPass(boolean canPass) {
        this.canPass = canPass;
    }
    public void setMessage(Component message) {
        this.message = message;
    }
    /**
     * @return the base join event.
     */
    public PlayerLoginEvent getJoinEvent() {
        return event;
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
