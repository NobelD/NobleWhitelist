package me.nobeld.noblewhitelist.logic;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.model.whitelist.CheckType;
import me.nobeld.noblewhitelist.model.whitelist.SuccessData;
import me.nobeld.noblewhitelist.model.whitelist.SuccessEnum;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

public class WhitelistChecker {
    private final NWLData data;
    public WhitelistChecker(NWLData data) {
        this.data = data;
    }
    public boolean permissionCheck(@NotNull PlayerWrapper player) {
        if (data.getConfigD().get(ConfigData.WhitelistCF.onlyOpPerm))
            return player.isOp();
        if (data.getConfigD().get(ConfigData.WhitelistCF.useCustomPermission)) {
            return player.isOp() ||
                    player.hasPermission(data.getConfigD().get(ConfigData.WhitelistCF.customPermission));
        } else
            return player.isOp() ||
                    player.hasPermission("noblewhitelist.bypass") ||
                    player.hasPermission("noblewhitelist.bypass.", data.getConfigD().get(ConfigData.WhitelistCF.permissionMinimum));
    }
    public SuccessEnum createSuccessAsEnum(PlayerWrapper player) {
        SuccessData suc = createSuccess(player);
        if (suc == null) return SuccessEnum.UNKNOWN;
        return suc.successEnum();
    }
    /**
     * Create and get information about a player of how joined the server.
     * @param player instance of the player
     * @return the success data of the player
     */
    public SuccessData createSuccess(@NotNull PlayerWrapper player) {
        return createSuccess(data.getStorage().loadPlayer(player), player);
    }
    /**
     * Create and get information about a player of how joined the server.
     * @param entry entry of the player
     * @param player instance of the player
     * @return the success data of the player
     */
    public SuccessData createSuccess(@Nullable WhitelistEntry entry, @NotNull PlayerWrapper player) {
        Boolean name = null;
        Boolean uuid = null;
        Boolean disabled = null;
        if (entry != null) {
            if (entry.getOptName().isPresent()) {
                name = entry.getOptName().get().equalsIgnoreCase(player.getName());
            }
            if (entry.getOptUUID().isPresent()) {
                uuid = entry.getOptUUID().get().equals(player.getUUID());
            }
            disabled = !entry.isWhitelisted();
        }
        boolean perm = permissionCheck(player);

        return new SuccessData(player, name, uuid, perm, disabled);
    }
    public CheckType checkEntry(PlayerWrapper player) {
        return checkEntry(player, false);
    }
    /**
     * Checks and gives information about a player
     * @param player player to compare
     * @return result of the check or invalid of the player does not have data
     */
    public CheckType checkEntry(PlayerWrapper player, boolean skipName) {
        if (player == null) return CheckType.INVALID;
        Optional<WhitelistEntry> entry = data.whitelistData().getEntry(player);
        if (entry.isEmpty()) return CheckType.INVALID;
        return checkEntry(entry.get(), player, skipName);
    }
    public CheckType checkEntry(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player) {
        return checkEntry(entry, player, false, false);
    }
    public CheckType checkEntry(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player, boolean skipName) {
        return checkEntry(entry, player, false, skipName);
    }
    /**
     * Checks and gives information about an entry and a player
     * @param entry entry of the player
     * @param player player to compare
     * @return result of the check
     */
    public CheckType checkEntry(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player, boolean ignoreUUID, boolean skipName) {
        short uuidResult; // -1 null, 1 match, 0 different
        if (ignoreUUID || entry.getUUID() == null) {
            uuidResult = -1;
        } else if (player.getUUID().equals(entry.getUUID())) {
            uuidResult = 1;
        } else {
            uuidResult = 0;
        }

        if (uuidResult == 1 && skipName) {
            return CheckType.SKIPPED_NAME;
        }
        String enName = entry.getName();
        if (enName == null) {
            return uuidResult == 1 ? CheckType.MISSING_NAME : CheckType.NONE;
        }
        String plName = player.getName();

        if (plName.equals(enName)) {
            return uuidResult == -1 ? CheckType.MISSING_UUID : uuidResult == 1 ? CheckType.FINE : CheckType.DIFFERENT_UUID;
        } else if (plName.equalsIgnoreCase(enName)) {
            return uuidResult == -1 ? CheckType.MISSING_UUID_AND_CAPS : uuidResult == 1 ? CheckType.CAPITALIZATION : CheckType.DIFFERENT_UUID_AND_CAPS;
        } else {
            return uuidResult == 1 ? CheckType.DIFFERENT_NAME : CheckType.NONE;
        }
    }
    /**
     * Updates and replaces the data of a player
     * @param player player to use
     * @return true if some data was changed, false if no data was changed or the player does not have data
     */
    public boolean updateData(@NotNull PlayerWrapper player) {
        return data.whitelistData().getEntry(player).filter(w -> updateData(w, player)).isPresent();
    }
    public boolean updateData(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player) {
        return updateData(entry, player, false);
    }
    /**
     * Updates and replaces the data of a player
     * @param entry entry of the player
     * @param player player to use
     * @return true if some data was changed
     */
    public boolean updateData(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player, boolean ignoreUUID) {
        String name = player.getName();
        UUID uuid = ignoreUUID ? null : player.getUUID();

        boolean skipName = data.getConfigD().get(ConfigData.WhitelistCF.skipName);
        CheckType type = data.whitelistChecker().checkEntry(entry, player, ignoreUUID, skipName);
        if (type == CheckType.DIFFERENT_NAME && !skipName) { // update name and sent notify of name change
            entry.setName(name);
            data.getStorage().save(entry);
            NobleWhitelist.adv().consoleAudience().sendMessage(data.getMessageD().warningNameConsole(name));
            player.sendMessage(data.getMessageD().warningNamePlayer(name));
        } else if (type == CheckType.MISSING_UUID_AND_CAPS) { // update uuid and name because of capitalization
            boolean b = !skipName || uuid != null;
            if (!skipName) entry.setName(name);
            if (uuid != null) entry.setUuid(uuid);
            if (b) data.getStorage().save(entry);
        } else if (type == CheckType.MISSING_UUID) { // update uuid if needed
            if (uuid != null) {
                entry.setUuid(uuid);
                data.getStorage().save(entry);
            }
        } else if (type == CheckType.CAPITALIZATION && !skipName) { // update name capitalization
            entry.setName(name);
            data.getStorage().save(entry);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Parses, loads and updates the player.
     * If auto whitelist is enabled this will also register them.
     * @param player player to parse
     * @param entryConsumer consumer if the player is auto registered
     */
    public void parseJoinData(PlayerWrapper player, Consumer<WhitelistEntry> entryConsumer) {
        Optional<WhitelistEntry> entry = this.data.whitelistData().getEntry(player);
        if (entry.isEmpty()) {
            if (this.data.getConfigD().get(ConfigData.WhitelistCF.autoRegister)) {
                if (this.data.getConfigD().get(ConfigData.SkipCF.skipUUID)) this.data.whitelistData().savePlayerOptionalNoUUID(player).ifPresent(entryConsumer);
                else this.data.whitelistData().savePlayerOptional(player).ifPresent(entryConsumer);
            }
        } else updateData(entry.get(), player, this.data.getConfigD().get(ConfigData.SkipCF.skipUUID));
    }
    /**
     * Determines if the player can join the server
     * @param player instance of the player
     * @return pair of success and boolean if the player can join
     */
    public PairData<SuccessData, Boolean> canPass(PlayerWrapper player) {
        Optional<WhitelistEntry> entry = this.data.whitelistData().getEntry(player);
        if (NobleWhitelist.getPlugin().isDebug()) {
            data.logger().log(Level.INFO,
                    entry.map(e -> "Starting check with entry: " + e + " for player: " + player)
                            .orElse("Starting check with no entry for player: " + player)
            );
        }

        // TODO this is a temporary fix because storage calls are not filtered at all
        if (entry.isPresent()) {
            var u = entry.get().getUUID();
            if (u != null && !player.getUUID().equals(u)) return PairData.of(SuccessData.allFalse(player), false);
        }

        final SuccessData suc = createSuccess(entry.orElse(null), player);
        return PairData.of(suc, suc.forValues(
                this.data.getConfigD().checkName(),
                this.data.getConfigD().checkUUID(),
                this.data.getConfigD().checkPerm()
                                            ));
    }
}
