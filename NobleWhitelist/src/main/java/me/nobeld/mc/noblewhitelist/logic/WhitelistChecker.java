package me.nobeld.mc.noblewhitelist.logic;

import me.nobeld.mc.noblewhitelist.NobleWhitelist;
import me.nobeld.mc.noblewhitelist.model.base.NWLData;
import me.nobeld.mc.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.mc.noblewhitelist.model.whitelist.*;
import me.nobeld.mc.noblewhitelist.config.ConfigData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class WhitelistChecker {
    private final NWLData data;
    private final ArrayList<SuccessData> successList = new ArrayList<>();
    public WhitelistChecker(NWLData data) {
        this.data = data;
    }
    /**
     * Create and get information about a player of how joined the server.
     * @param player instance of the player
     * @return the success data of the player
     */
    public SuccessData registerSuccess(@NotNull PlayerWrapper player) {
        return registerSuccess(data.getStorage().loadPlayer(player), player);
    }
    /**
     * Get information about a player of how joined the server.
     * @param player instance of the player
     * @return the success data of the player or null if it does not exist
     */
    @Nullable
    public SuccessData getSuccess(PlayerWrapper player) {
        return successList.stream().filter(d -> d.player().mergeString().equalsIgnoreCase(player.mergeString())).findFirst().orElse(null);
    }
    public SuccessEnum getSuccessEnum(PlayerWrapper player) {
        SuccessData suc = getSuccess(player);
        if (suc == null) return SuccessEnum.UNKNOWN;
        return suc.successEnum();
    }
    /**
     * Remove the information about a player of how joined the server.
     * @param player instance of the player
     */
    public void removeSuccess(PlayerWrapper player) {
        successList.stream().filter(d -> d.player().mergeString().equalsIgnoreCase(player.mergeString())).findFirst().ifPresent(successList::remove);
    }
    /**
     * Create and get information about a player of how joined the server.
     * @param entry entry of the player
     * @param player instance of the player
     * @return the success data of the player
     */
    public SuccessData registerSuccess(WhitelistEntry entry, @NotNull PlayerWrapper player) {
        if (entry == null) return new SuccessData(player, false, false, false);
        boolean name = entry.getOptName().isPresent();
        boolean uuid = entry.getOptUUID().isPresent();
        boolean perm;
        if (data.getConfigD().get(ConfigData.WhitelistCF.onlyOpPerm)) perm = player.isOp();
        else perm = player.isOp() ||
                player.hasPermission("noblewhitelist.bypass") ||
                player.hasPermission("noblewhitelist.bypass.", data.getConfigD().get(ConfigData.WhitelistCF.permissionMinimum));

        SuccessData data = new SuccessData(player, name, uuid, perm);
        successList.add(data);
        return data;
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
    public void parseJoinData(PlayerWrapper player, Consumer<WhitelistEntry> autoWlConsumer) {
        Optional<WhitelistEntry> data = this.data.whitelistData().getEntry(player);
        if (data.isEmpty()) {
            if (this.data.getConfigD().get(ConfigData.WhitelistCF.autoRegister)) {
                this.data.whitelistData().savePlayerOptional(player).ifPresent(autoWlConsumer);
            }
        } else updateData(data.get(), player);
    }
    /**
     * Determines if the player can join the server
     * @param player instance of the player
     * @return true if the player can join
     */
    public boolean canPass(PlayerWrapper player) {
        Optional<WhitelistEntry> data = this.data.whitelistData().getEntry(player);
        if (data.isEmpty()) return false;
        SuccessData suc = registerSuccess(data.get(), player);

        if (this.data.getConfigD().get(ConfigData.WhitelistCF.enforceNameDiffID) && checkEntry(data.get(), player) == CheckType.NAME_DIFFERENT_UUID) return false;

        ConfigData.CheckType name = this.data.getConfigD().checkName();
        ConfigData.CheckType uuid = this.data.getConfigD().checkUUID();
        ConfigData.CheckType perm = this.data.getConfigD().checkPerm();

        if (name.isRequired() && !suc.onlyName()) return false;
        if (uuid.isRequired() && !suc.onlyUuid()) return false;
        if (perm.isRequired() && !suc.onlyPerm()) return false;
        if (name.isDisabled()) return suc.uuid() || suc.perm();
        if (uuid.isDisabled()) return suc.name() || suc.perm();
        if (perm.isDisabled()) return suc.name() || suc.uuid();
        return suc.hasAny();
    }
}
