package me.nobeld.minecraft.noblewhitelist.model;

import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface FileGetter extends DataGetter {
    @Override
    default PlayerWhitelisted loadPlayer(@NotNull String name) {
        return getAll().stream().filter(p -> p.getOptName().filter(n -> n.equalsIgnoreCase(name)).isPresent()).findFirst().orElse(null);
    }
    @Override
    default PlayerWhitelisted loadPlayer(@NotNull UUID uuid) {
        return getAll().stream().filter(p -> p.getOptUUID().filter(u -> u.equals(uuid)).isPresent()).findFirst().orElse(null);
    }
    @Override
    default PlayerWhitelisted loadPlayer(long id) {
        return getAll().stream().filter(p -> p.getDiscordID() == id).findFirst().orElse(null);
    }
    @Override
    default List<PlayerWhitelisted> loadAccounts(long id) {
        return getAll().stream().filter(p -> p.getDiscordID() == id).toList();
    }
    @Override
    default PlayerWhitelisted loadPlayer(@NotNull Player player) {
        return getAll().stream().filter(p -> {
            if (p.getOptName().filter(n -> n.equalsIgnoreCase(player.getName())).isPresent()) {
                return true;
            } else return p.getOptUUID().filter(u -> u.equals(player.getUniqueId())).isPresent();
        }).findFirst().orElse(null);
    }
    @Override
    default List<PlayerWhitelisted> listIndex(int page) {
        if (page <= 1) return getAll().stream().limit(10).collect(Collectors.toList());
        int amount = 10 * (page - 1);
        return getAll().stream().skip(amount).limit(10).collect(Collectors.toList());
    }
    @Override
    default long getTotal() {
        return getAll().size();
    }
    /**
     * @return the player data stored as list.
     */
    List<PlayerWhitelisted> getAll();
}
