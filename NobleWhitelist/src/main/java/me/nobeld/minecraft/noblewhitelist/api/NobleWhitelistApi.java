package me.nobeld.minecraft.noblewhitelist.api;

import me.nobeld.minecraft.noblewhitelist.config.ConfigFile;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.data.WhitelistData;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.SuccessEnum;
import org.bukkit.entity.Player;

import java.util.List;

public class NobleWhitelistApi {
    private final NobleWhitelist plugin;
    public NobleWhitelistApi(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    /**
     * @return success type of the player join to the whitelist.
     */
    public SuccessEnum getSuccessType(Player player) {
        return plugin.whitelistChecker().successType(player);
    }
    /**
     * Create and register a player to the whitelist, if the player is already registered this does nothing.
     */
    public void createPlayerData(Player player) {
        plugin.whitelistData().addPlayer(player);
    }
    /**
     * @return true if the player is registered on the whitelist.
     */
    public boolean hasData(Player player) {
        return plugin.whitelistData().getData(player).isPresent();
    }
    public boolean hasByPass(Player player) {
        return plugin.whitelistData().registerSuccess(player).isBypass();
    }
    public boolean isWhitelisted(Player player) {
        return plugin.whitelistData().registerSuccess(player).isWhitelisted();
    }
    public WhitelistData getWlData() {
        return plugin.whitelistData();
    }
    /**
     * @return true if the player can join with the config values.
     */
    public boolean optionalJoin(Player player) {
        return plugin.whitelistData().registerSuccess(player).hasAny();
    }
    /**
     * @return true if the player can pass the whitelist with the default values.
     */
    public boolean canPass(Player player) {
        return plugin.whitelistChecker().canPass(player);
    }
    /**
     * @return true if the whitelist is active otherwise false.
     */
    public boolean whitelist() {
        return ConfigFile.getConfig(ConfigFile.whitelistActive);
    }
    /**
     * Activate or deactivated the whitelist.
     * @param activated the state to set the whitelist
     * @return true if the whitelist was changed, otherwise false if the whitelist was already in the provided state.
     */
    public boolean whitelist(boolean activated) {
        boolean actually = ConfigFile.getConfig(ConfigFile.whitelistActive);

        if (activated == actually) {
            return false;
        } else {
            ConfigFile.setConfig(ConfigFile.whitelistActive, activated);
            return true;
        }
    }
    /**
     * Get a list of the first 10 accounts registered, the page number determines the offset.
     * @param page the page to the get the index.
     * @return list of the players registered or empty if none was found.
     */
    public List<PlayerWhitelisted> getIndex(int page) {
        if (page <= 1) page = 1;
        return plugin.getStorageInst().listIndex(page);
    }
}
