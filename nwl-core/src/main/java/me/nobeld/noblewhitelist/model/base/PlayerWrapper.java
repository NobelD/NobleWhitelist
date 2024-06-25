package me.nobeld.noblewhitelist.model.base;

import me.nobeld.noblewhitelist.model.storage.DataGetter;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface PlayerWrapper {
    @NotNull
    String getName();

    @NotNull
    UUID getUUID();

    boolean hasPermission(String permission);

    boolean hasPermission(String permissionPrefix, int minimum);

    boolean isOp();

    void sendMessage(Component component);

    Audience getAsAudience();

    default Optional<WhitelistEntry> getWhitelistEntry(@NotNull DataGetter storage) {
        return getWhitelistEntry(storage, -1);
    }

    default Optional<WhitelistEntry> getWhitelistEntry(@NotNull DataGetter storage, long discordId) {
        WhitelistEntry data = storage.loadPlayer(getName());
        if (data == null) data = storage.loadPlayer(getUUID());
        if (data == null && discordId >= 0) storage.loadPlayer(discordId);

        return Optional.ofNullable(data);
    }

    default String asString() {
        return "PlayerW[name=" + getName() + ";uuid=" + getUUID() + "]";
    }
}
