package me.nobeld.noblewhitelist.logic;

import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;
import me.nobeld.noblewhitelist.model.whitelist.CheckType;
import me.nobeld.noblewhitelist.model.whitelist.SuccessData;
import me.nobeld.noblewhitelist.model.whitelist.SuccessEnum;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class WhitelistChecker {
    private final NWLData data;

    public WhitelistChecker(NWLData data) {
        this.data = data;
    }

    public SuccessEnum createSuccessAsEnum(PlayerWrapper player) {
        SuccessData suc = createSuccess(player);
        if (suc == null) return SuccessEnum.UNKNOWN;
        return suc.successEnum();
    }

    /**
     * Create and get information about a player of how joined the server.
     *
     * @param player instance of the player
     * @return the success data of the player
     */
    public SuccessData createSuccess(@NotNull PlayerWrapper player) {
        return createSuccess(data.getStorage().loadPlayer(player), player);
    }

    /**
     * Create and get information about a player of how joined the server.
     *
     * @param entry  entry of the player
     * @param player instance of the player
     * @return the success data of the player
     */
    public SuccessData createSuccess(@Nullable WhitelistEntry entry, @NotNull PlayerWrapper player) {
        boolean name = false;
        boolean uuid = false;
        if (entry != null) {
            name = entry.getOptName().isPresent();
            uuid = entry.getOptUUID().isPresent();
        }
        boolean perm;
        if (data.getConfigD().get(ConfigData.ByPassCF.onlyOpPerm)) perm = player.isOp();
        else perm = player.isOp() ||
                player.hasPermission("noblewhitelist.bypass") ||
                player.hasPermission("noblewhitelist.bypass.", data.getConfigD().get(ConfigData.ByPassCF.permissionMinimum));

        return new SuccessData(player, name, uuid, perm);
    }

    /**
     * Checks and gives information about a player
     *
     * @param player player to compare
     * @return result of the check or invalid of the player does not have data
     */
    public CheckType checkEntry(PlayerWrapper player) {
        if (player == null) return CheckType.INVALID;
        Optional<WhitelistEntry> entry = data.whitelistData().getEntry(player);
        if (entry.isEmpty()) return CheckType.INVALID;
        return checkEntry(entry.get(), player);
    }
    
    public CheckType checkEntry(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player) {
        return checkEntry(entry, player, false);
    }
    /**
     * Checks and gives information about an entry and a player
     *
     * @param entry  entry of the player
     * @param player player to compare
     * @return result of the check
     */
    public CheckType checkEntry(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player, boolean ignoreUUID) {
        return CheckType.getFromPlayer(entry, player, false, ignoreUUID);
    }

    /**
     * Updates and replaces the data of a player
     *
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
     *
     * @param entry  entry of the player
     * @param player player to use
     * @return true if some data was changed
     */
    public boolean updateData(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player, boolean ignoreUUID) {
        String name = player.getName();
        UUID uuid = ignoreUUID ? null : player.getUUID();

        CheckType check = data.whitelistChecker().checkEntry(entry, player);

        if (check.emptyNameYesUUID()) {
            entry.setName(name);
            data.getStorage().save(entry);
            data.getAdventure().consoleAudience().sendMessage(data.getMessageD().warningNameConsole(name));
            player.sendMessage(data.getMessageD().warningNamePlayer(name));
        } else if (check.emptyUUIDYesNameOrCaps()) {
            entry.setName(name);
            entry.setUuid(uuid);
            data.getStorage().save(entry);
        } else if (check.emptyNameYesUUID() || check.nameWithCapsYesUUID()) {
            entry.setName(name);
            data.getStorage().save(entry);
        } else return false;
        return true;
    }

    /**
     * Parses, loads and updates the player.
     * If auto whitelist is enabled this will also register them.
     *
     * @param player        player to parse
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
     *
     * @param player instance of the player
     * @return pair of success and boolean if the player can join
     */
    public PairData<SuccessData, Boolean> canPass(PlayerWrapper player) {
        Optional<WhitelistEntry> entry = this.data.whitelistData().getEntry(player);

        if (entry.isPresent() && this.data.getConfigD().get(ConfigData.WhitelistCF.enforceNameDiffID) && checkEntry(entry.get(), player).diffUUIDYesName())
            return PairData.of(SuccessData.allFalse(player), false);

        SuccessData suc = createSuccess(entry.orElse(null), player);

        CheckingOption name = this.data.getConfigD().checkName();
        CheckingOption uuid = this.data.getConfigD().checkUUID();
        CheckingOption perm = this.data.getConfigD().checkPerm();

        boolean result;
        if (name.isRequired() && !suc.onlyName()) result = false;
        else if (uuid.isRequired() && !suc.onlyUuid()) result = false;
        else if (perm.isRequired() && !suc.onlyPerm()) result = false;
        else if (name.isDisabled()) result = suc.uuid() || suc.perm();
        else if (uuid.isDisabled()) result = suc.name() || suc.perm();
        else if (perm.isDisabled()) result = suc.name() || suc.uuid();
        else result = suc.hasAny();

        return PairData.of(suc, result);
    }
}
