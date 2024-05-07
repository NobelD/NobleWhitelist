package me.nobeld.noblewhitelist;

import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BPlayer implements PlayerWrapper {
    private final Player player;
    public BPlayer(Player player) {
        this.player = player;
    }
    public static BPlayer of(Player player) {
        return new BPlayer(player);
    }
    public Player getPlayer() {
        return player;
    }
    @Override
    public String getName() {
        return player.getName();
    }
    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }
    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
    // #TODO check performance impact
    @Override
    public boolean hasPermission(String permissionPrefix, int minimum) {
        if (minimum < 0) return false;
        if (!permissionPrefix.endsWith(".")) permissionPrefix += ".";
        final String prefix = permissionPrefix;

        return player.getEffectivePermissions().stream()
                .filter(p -> p.getPermission().startsWith(prefix))
                .map(p -> {
                    try {
                        return Integer.parseInt(p.getPermission().replace(prefix, ""));
                    } catch (NumberFormatException ignored) {
                        return -1;
                    }
                })
                .anyMatch(i -> i >= minimum);
    }
    @Override
    public boolean isOp() {
        return player.isOp();
    }
    @Override
    public void sendMessage(Component component) {
        NobleWhitelist.adv().playerAudience(player).sendMessage(component);
    }
    @Override
    public Audience getAsAudience() {
        return NobleWhitelist.adv().playerAudience(player);
    }
}
