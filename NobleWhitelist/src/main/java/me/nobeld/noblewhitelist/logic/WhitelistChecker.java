package me.nobeld.noblewhitelist.logic;

import me.nobeld.noblewhitelist.NobleWhitelist;
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
        boolean name = false;
        boolean uuid = false;
        if (entry != null) {
            name = entry.getOptName().isPresent();
            uuid = entry.getOptUUID().isPresent();
        }
        boolean perm;
        if (data.getConfigD().get(ConfigData.WhitelistCF.onlyOpPerm)) perm = player.isOp();
        else perm = player.isOp() ||
                player.hasPermission("noblewhitelist.bypass") ||
                player.hasPermission("noblewhitelist.bypass.", data.getConfigD().get(ConfigData.WhitelistCF.permissionMinimum));

        return new SuccessData(player, name, uuid, perm);
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
    /**
     * Checks and gives information about an entry and a player
     * @param entry entry of the player
     * @param player player to compare
     * @return result of the check
     */
    public CheckType checkEntry(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player) {
        String enName = entry.getName();
        if (enName == null) return CheckType.NO_NAME;
        String plName = player.getName();
        UUID enUUID = entry.getUUID();
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
    /**
     * Updates and replaces the data of a player
     * @param entry entry of the player
     * @param player player to use
     * @return true if some data was changed
     */
    public boolean updateData(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player) {
        String name = player.getName();
        UUID uuid = player.getUUID();

        switch (data.whitelistChecker().checkEntry(entry, player)) {
            case UUID_NO_NAME -> {
                entry.setName(name);
                data.getStorage().save(entry);
                NobleWhitelist.adv().consoleAudience().sendMessage(data.getMessageD().warningNameConsole(name));
                player.sendMessage(data.getMessageD().warningNamePlayer(name));
                return true;
            }
            case NO_UUID_NAME_CAPS, NO_UUID -> {
                entry.setName(name);
                entry.setUuid(uuid);
                data.getStorage().save(entry);
                return true;
            }
            case NOT_MATCH -> {
                entry.setUuid(uuid);
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
                this.data.whitelistData().savePlayerOptional(player).ifPresent(entryConsumer);
            }
        } else updateData(entry.get(), player);
    }
    /**
     * Determines if the player can join the server
     * @param player instance of the player
     * @return true if the player can join
     */
    public boolean canPass(PlayerWrapper player) {
        Optional<WhitelistEntry> entry = this.data.whitelistData().getEntry(player);

        if (entry.isPresent() && this.data.getConfigD().get(ConfigData.WhitelistCF.enforceNameDiffID) && checkEntry(entry.get(), player) == CheckType.NAME_DIFFERENT_UUID) return false;

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

        return result;
    }
}
