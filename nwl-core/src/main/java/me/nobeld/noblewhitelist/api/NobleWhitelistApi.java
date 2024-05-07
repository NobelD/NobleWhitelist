package me.nobeld.noblewhitelist.api;

import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;

import java.util.List;
import java.util.Optional;

public class NobleWhitelistApi {
    private final NWLData data;
    public NobleWhitelistApi(NWLData data) {
        this.data = data;
    }
    /**
     * Create and register a player to the whitelist, if the player is already registered this does nothing.
     */
    public void createPlayerData(PlayerWrapper player) {
        data.whitelistData().savePlayer(player);
    }
    /**
     * @return true if the player is registered on the whitelist.
     */
    public boolean hasData(PlayerWrapper player) {
        return data.whitelistData().getEntry(player).isPresent();
    }
    public boolean hasByPass(PlayerWrapper player) {
        return data.whitelistChecker().createSuccess(player).isBypass();
    }
    public boolean isWhitelisted(PlayerWrapper player) {
        return data.whitelistChecker().createSuccess(player).isWhitelisted();
    }
    public PairData<Boolean, Integer> getPermStatus() {
        return PairData.of(data.getConfigD().get(ConfigData.WhitelistCF.onlyOpPerm), data.getConfigD().get(ConfigData.WhitelistCF.permissionMinimum));
    }
    public void setPermMinimum(int min) {
        data.getConfigD().set(ConfigData.WhitelistCF.permissionMinimum, min);
    }
    /**
     * Get if the player can join the server assuming all the join options as optional.
     * @return true if the player can join.
     */
    public boolean optionalJoin(PlayerWrapper player) {
        return data.whitelistChecker().createSuccess(player).hasAny();
    }
    /**
     * Get if the player can join the server.
     * @return true if the player can join the whitelist.
     */
    public boolean canPass(PlayerWrapper player) {
        return data.whitelistChecker().canPass(player).getSecond();
    }
    /**
     * @return true if the whitelist is active otherwise false.
     */
    public boolean whitelist() {
        return data.getConfigD().get(ConfigData.WhitelistCF.whitelistActive);
    }
    /**
     * Activate or deactivated the whitelist.
     * @param activated the state to set the whitelist
     * @return true if the whitelist was changed, otherwise false if the whitelist was already in the provided state.
     */
    public boolean whitelist(boolean activated) {
        boolean actually = data.getConfigD().get(ConfigData.WhitelistCF.whitelistActive);

        if (activated == actually) {
            return false;
        } else {
            data.getConfigD().set(ConfigData.WhitelistCF.whitelistActive, activated);
            return true;
        }
    }
    /**
     * Get if the provided player is whitelisted and have a discord user linked.
     * @param player the player to check
     * @return true if the player has a discord user linked.
     */
    public boolean hasDiscordLinked(PlayerWrapper player) {
        return data.whitelistData().getEntry(player).map(WhitelistEntry::hasDiscord).orElse(false);
    }
    /**
     * Get if the player is whitelisted and get the id of their discord user linked.
     * @param player the player to check
     * @return optional of the player's discord user id.
     */
    public Optional<Long> getDiscordUser(PlayerWrapper player) {
        return data.whitelistData().getEntry(player).map(WhitelistEntry::getDiscordID);
    }
    /**
     * Get if the player is whitelisted but can not enter to the server.
     * @param player the player to check
     * @return true if the player can not join but is whitelisted.
     */
    public boolean isWhitelistDenied(PlayerWrapper player) {
        return data.whitelistData().getEntry(player).map(WhitelistEntry::isWhitelisted).orElse(false);
    }
    /**
     * Get a list of the first 10 accounts registered, the page number determines the offset.
     * @param page the page to the get the index.
     * @return list of the players registered or empty if none was found.
     */
    public List<WhitelistEntry> getIndex(int page) {
        if (page <= 1) page = 1;
        return data.getStorage().listIndex(page);
    }
}
