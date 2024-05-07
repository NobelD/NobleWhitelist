package me.nobeld.noblewhitelist.logic;

import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.UUID;

public class WhitelistData {
    private final NWLData data;
    public WhitelistData(NWLData data) {
        this.data = data;
    }
    /**
     * Get an optional entry of a player
     * @param name name of the player
     * @param uuid uuid of the player
     * @param id discord id of the user
     * @return optional entry of a player
     */
    public Optional<WhitelistEntry> getEntry(@Nullable String name, @Nullable UUID uuid, @Range(from=-1, to=Long.MAX_VALUE) long id) {
        WhitelistEntry data = null;
        if (name != null) data = this.data.getStorage().loadPlayer(name);
        if (data == null && uuid != null) data = this.data.getStorage().loadPlayer(uuid);
        if (data == null && id >= 0) data = this.data.getStorage().loadPlayer(id);

        return Optional.ofNullable(data);
    }
    /**
     * Get an optional entry of a player
     * @param name name of the player
     * @param uuid uuid of the player
     * @return optional entry of a player
     */
    public Optional<WhitelistEntry> getEntry(@Nullable String name, @Nullable UUID uuid) {
        return getEntry(name, uuid, -1);
    }
    /**
     * Get an optional entry of a player
     * @param player instance of the player
     * @return optional entry of a player
     */
    public Optional<WhitelistEntry> getEntry(PlayerWrapper player) {
        return getEntry(player.getName(), player.getUUID(), -1);
    }
    /**
     * Saves the entry to the storage.
     * If already exists then will be overwritten.
     * @param entry the entry to save
     */
    public void saveEntry(@NotNull WhitelistEntry entry) {
        this.data.getStorage().save(entry);
    }
    /**
     * Saves and register the information of a player to the storage.
     * @param name name of the player
     * @param uuid uuid of the player
     * @param id discord id of the user
     * @return the entry of the player created
     */
    public WhitelistEntry registerAndSave(String name, UUID uuid, long id) {
        WhitelistEntry data = new WhitelistEntry(name, uuid, id, true);
        saveEntry(data);
        return data;
    }
    /**
     * Saves and register the player to the storage.
     * @param player player to save
     * @return true if was saved, false if already exists
     */
    public boolean savePlayer(PlayerWrapper player) {
        Optional<WhitelistEntry> data = getEntry(player.getName(), player.getUUID(), -1);
        if (data.isPresent()) return false;
        saveEntry(new WhitelistEntry(player.getName(), player.getUUID()));
        return true;
    }
    /**
     * Saves and register the player to the storage.
     * @param player player to save
     * @return optional of the entry
     */
    public Optional<WhitelistEntry> savePlayerOptional(PlayerWrapper player) {
        Optional<WhitelistEntry> data = getEntry(player.getName(), player.getUUID(), -1);
        if (data.isPresent()) return Optional.empty();

        WhitelistEntry saved = new WhitelistEntry(player.getName(), player.getUUID());
        saveEntry(saved);
        return Optional.of(saved);
    }
    /**
     * Saves and register the player to the storage.
     * @param player player to save
     * @return optional of the entry
     */
    public Optional<WhitelistEntry> savePlayerOptionalNoUUID(PlayerWrapper player) {
        Optional<WhitelistEntry> data = getEntry(player.getName(), null, -1);
        if (data.isPresent()) return Optional.empty();

        WhitelistEntry saved = new WhitelistEntry(player.getName(), null);
        saveEntry(saved);
        return Optional.of(saved);
    }
    /**
     * Toggles if the player can join or not to the server.
     * @param entry entry of the player
     * @param canJoin the state of the join
     */
    public void toggleJoin(WhitelistEntry entry, boolean canJoin) {
        entry.setWhitelisted(canJoin);
        saveEntry(entry);
    }
    /**
     * Links the discord id to a player entry.
     * @param data entry of the player
     * @param id discord id of the user
     */
    public void linkDiscord(@NotNull WhitelistEntry data, long id) {
        data.setDiscordID(id);
        saveEntry(data);
    }
    /**
     * Deletes the entry from the storage
     * @param entry entry of the player
     */
    public void deleteUser(WhitelistEntry entry) {
        data.getStorage().delete(entry);
    }
    /**
     * Deletes an entry of the storage
     * @param name name of the player
     * @param uuid of the player
     * @return true if was removed, false if the player is not registered
     */
    public boolean deleteUser(String name, UUID uuid) {
        Optional<WhitelistEntry> data = getEntry(name, uuid, -1);
        if (data.isEmpty()) return false;
        deleteUser(data.get());
        return true;
    }
    /**
     * Deletes an entry of the storage
     * @param player instance of the player
     * @return true if was removed, false if the player is not registered
     */
    public boolean deleteUser(PlayerWrapper player) {
        return deleteUser(player.getName(), player.getUUID());
    }
}
