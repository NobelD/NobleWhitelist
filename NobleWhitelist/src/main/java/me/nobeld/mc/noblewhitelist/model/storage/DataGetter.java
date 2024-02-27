package me.nobeld.mc.noblewhitelist.model.storage;

import me.nobeld.mc.noblewhitelist.model.whitelist.WhitelistEntry;
import me.nobeld.mc.noblewhitelist.model.base.PlayerWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface DataGetter {
    /**
     * @param player to retrieve their whitelist data
     * @return the player's data or null if none was found
     */
    @Nullable
    WhitelistEntry loadPlayer(@NotNull PlayerWrapper player);
    /**
     * @param name of the player to retrieve their whitelist data
     * @return the player's data or null if none was found
     */
    @Nullable
    WhitelistEntry loadPlayer(@NotNull String name);
    /**
     * @param uuid of the player to retrieve their whitelist data
     * @return the player's data or null if none was found
     */
    @Nullable
    WhitelistEntry loadPlayer(@NotNull UUID uuid);
    /**
     * @param id of the discord user to retrieve their whitelist data
     * @return the player's data or null if none was found
     */
    @Nullable
    WhitelistEntry loadPlayer(long id);
    /**
     * @param id of the discord user to retrieve all the accounts linked to
     * @return a list of the players linked to this player or empty if none was found
     */
    List<WhitelistEntry> loadAccounts(long id);
    /**
     * Get a list of the first 10 accounts registered, the page number determines the offset.
     * @param page the page to the get the index.
     * @return list of the players registered or empty if none was found.
     */
    List<WhitelistEntry> listIndex(int page);
    /**
     * Save or replace the player data
     * @param player data to be used
     */
    void save(@NotNull WhitelistEntry player);
    /**
     * Delete the player data
     * @param player data to be used
     */
    void delete(@NotNull WhitelistEntry player);
    /**
     * Clear all the data stored
     * @return true if the whitelist was cleared, false otherwise if the whitelist is empty
     */
    boolean clear();
    /**
     * Reload the player data
     */
    void reload();
    /**
     * @return the total amount of player data stored
     */
    long getTotal();
}
