package me.nobeld.noblewhitelist.model;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;

public record BPlayer(Player player) implements PlayerWrapper {
    public static BPlayer of(Player player) {
        return new BPlayer(player);
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
        if (NobleWhitelist.hasPaper()) return player;
        return NobleWhitelist.adv().playerAudience(player);
    }
}
