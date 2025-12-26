package me.nobeld.noblewhitelist.logic;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.noblewhitelist.config.ConfigData;
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
    /**
     * Checks and gives information about a player
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
     * @param entry entry of the player
     * @param player player to compare
     * @return result of the check
     */
    public CheckType checkEntry(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player, boolean ignoreUUID) {
        String enName = entry.getName();
        if (enName == null) return CheckType.NO_NAME;
        String plName = player.getName();
        UUID enUUID = ignoreUUID ? null : entry.getUUID();
        UUID plUUID = player.getUUID();

        if (enUUID == null) {
            if (enName.equals(plName)) return CheckType.NO_UUID;
            else if (enName.equalsIgnoreCase(plName)) return CheckType.NO_UUID_NAME_CAPS;
            else return CheckType.NOT_MATCH;
        }
        if (data.getConfigD().get(ConfigData.WhitelistCF.skipName)) {
            if (enUUID.equals(plUUID)) return CheckType.SKIPPED_NAME;
            else return CheckType.NOT_MATCH;
        }

        if (enName.equals(plName)) {
            if (!enUUID.equals(plUUID)) return CheckType.NAME_DIFFERENT_UUID;
            else return CheckType.NORMAL;

        } else if (enName.equalsIgnoreCase(plName)) {
            if (!enUUID.equals(plUUID)) return CheckType.NAME_CAPS_DIFFERENT_UUID;
            else return CheckType.NAME_CAPS;
        }
        if (enUUID.equals(plUUID)) return CheckType.UUID_NO_NAME;
        return CheckType.NORMAL;
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

        switch (data.whitelistChecker().checkEntry(entry, player, ignoreUUID)) {
            case UUID_NO_NAME -> {
                entry.setName(name);
                data.getStorage().save(entry);
                NobleWhitelist.adv().consoleAudience().sendMessage(data.getMessageD().warningNameConsole(name));
                player.sendMessage(data.getMessageD().warningNamePlayer(name));
                return true;
            }
            case NO_UUID_NAME_CAPS, NO_UUID -> {
                entry.setName(name);
                if (uuid != null) entry.setUuid(uuid);
                data.getStorage().save(entry);
                return true;
            }
            case NO_NAME, NAME_CAPS -> {
                entry.setName(name);
                data.getStorage().save(entry);
                return true;
            }
            default -> {
                return false;
            }
        }
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

        boolean enforce = this.data.getConfigD().get(ConfigData.WhitelistCF.enforceNameDiffID);
        if (entry.isPresent() && enforce) {
            CheckType type = checkEntry(entry.get(), player);
            if (type == CheckType.NAME_DIFFERENT_UUID || type == CheckType.NAME_CAPS_DIFFERENT_UUID)
                return PairData.of(SuccessData.allFalse(player), false);
        }

        final SuccessData suc = createSuccess(entry.orElse(null), player);
        CheckingOption nameCheck = this.data.getConfigD().checkName();
        boolean shouldSkip = enforce && suc.matchUUID() && (suc.name() == null || !suc.name());

        return PairData.of(suc, suc.forValues(
                shouldSkip && !nameCheck.isRequired() ? CheckingOption.DISABLED : nameCheck,
                this.data.getConfigD().checkUUID(),
                this.data.getConfigD().checkPerm()
                                            ));
    }
}
