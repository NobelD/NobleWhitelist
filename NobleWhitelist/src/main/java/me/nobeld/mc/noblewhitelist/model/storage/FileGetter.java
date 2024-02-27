package me.nobeld.mc.noblewhitelist.model.storage;

import me.nobeld.mc.noblewhitelist.model.whitelist.WhitelistEntry;
import me.nobeld.mc.noblewhitelist.model.base.PlayerWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface FileGetter extends DataGetter {
    @Override
    default WhitelistEntry loadPlayer(@NotNull String name) {
        return getAll().stream().filter(p -> p.getOptName().filter(n -> n.equalsIgnoreCase(name)).isPresent()).findFirst().orElse(null);
    }
    @Override
    default WhitelistEntry loadPlayer(@NotNull UUID uuid) {
        return getAll().stream().filter(p -> p.getOptUUID().filter(u -> u.equals(uuid)).isPresent()).findFirst().orElse(null);
    }
    @Override
    default WhitelistEntry loadPlayer(long id) {
        return getAll().stream().filter(p -> p.getDiscordID() == id).findFirst().orElse(null);
    }
    @Override
    default List<WhitelistEntry> loadAccounts(long id) {
        return getAll().stream().filter(p -> p.getDiscordID() == id).toList();
    }
    @Override
    default WhitelistEntry loadPlayer(@NotNull PlayerWrapper player) {
        return getAll().stream().filter(p -> {
            if (p.getOptName().filter(n -> n.equalsIgnoreCase(player.getName())).isPresent()) {
                return true;
            } else return p.getOptUUID().filter(u -> u.equals(player.getUUID())).isPresent();
        }).findFirst().orElse(null);
    }
    @Override
    default List<WhitelistEntry> listIndex(int page) {
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
    List<WhitelistEntry> getAll();
}
