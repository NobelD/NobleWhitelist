package me.nobeld.minecraft.noblewhitelist.api;

import me.nobeld.minecraft.noblewhitelist.data.WhitelistData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WhitelistPassEvent extends Event {
    private final Player player;
    private final WhitelistData.SuccessEnum type;
    private static final HandlerList handlers = new HandlerList();
    public WhitelistPassEvent(Player player, WhitelistData.SuccessEnum type) {
        this.player = player;
        this.type = type;
    }
    public Player getPlayer() {
        return player;
    }
    public WhitelistData.SuccessEnum getType() {
        return type;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
