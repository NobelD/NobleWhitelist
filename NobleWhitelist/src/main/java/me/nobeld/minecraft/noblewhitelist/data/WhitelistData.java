package me.nobeld.minecraft.noblewhitelist.data;

import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.config.ConfigFile;
import me.nobeld.minecraft.noblewhitelist.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.CheckType;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.SuccessData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class WhitelistData {
    private final NobleWhitelist plugin;
    private final ArrayList<SuccessData> successList = new ArrayList<>();
    public WhitelistData(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    public SuccessData registerSuccess(Player player) {
        return registerSuccess(plugin.getStorageInst().loadPlayer(player), player);
    }
    public SuccessData registerSuccess(PlayerWhitelisted wl, Player player) {
        if (wl == null) return new SuccessData(player, false, false, false);
        boolean name = wl.getOptName().isPresent();
        boolean uuid = wl.getOptUUID().isPresent();
        boolean perm;
        if (ConfigFile.getConfig(ConfigFile.countAsOp)) {
            perm = player.hasPermission("noblewhitelist.bypass") || player.isOp();
        } else perm = player.hasPermission("noblewhitelist.bypass");

        SuccessData data = new SuccessData(player, name, uuid, perm);
        successList.add(data);
        return data;
    }
    @Nullable
    public SuccessData getSuccess(Player player) {
        return successList.stream().filter(d -> d.player() == player).findFirst().orElse(null);
    }
    public void removeSuccess(Player player) {
        successList.stream().filter(d -> d.player() == player).findFirst().ifPresent(successList::remove);
    }
    public Optional<PlayerWhitelisted> getData(@Nullable String name, @Nullable UUID uuid, long id) {
        PlayerWhitelisted data = null;
        if (name != null) data = plugin.getStorageInst().loadPlayer(name);
        if (data == null && uuid != null) data = plugin.getStorageInst().loadPlayer(uuid);
        if (data == null && id >= 0) data = plugin.getStorageInst().loadPlayer(id);

        return Optional.ofNullable(data);
    }
    public Optional<PlayerWhitelisted> getData(@Nullable Player player) {
        if (player == null) return Optional.empty();
        return getData(player.getName(), player.getUniqueId(), -1);
    }
    public Optional<PlayerWhitelisted> getData(@Nullable String name, @Nullable UUID uuid) {
        return getData(name, uuid, -1);
    }
    public void addPlayer(@NotNull PlayerWhitelisted data) {
        plugin.getStorageInst().save(data);
    }
    public PlayerWhitelisted register(String name, UUID uuid, long id) {
        PlayerWhitelisted data = new PlayerWhitelisted(name, uuid, id, true);
        plugin.getStorageInst().save(data);
        return data;
    }
    public Optional<PlayerWhitelisted> addOptPlayer(Player player) {
        Optional<PlayerWhitelisted> data = getData(player.getName(), player.getUniqueId(), -1);
        if (data.isPresent()) return Optional.empty();

        PlayerWhitelisted saved = new PlayerWhitelisted(player.getName(), player.getUniqueId());
        plugin.getStorageInst().save(saved);
        return Optional.of(saved);
    }
    public boolean addPlayer(Player player) {
        Optional<PlayerWhitelisted> data = getData(player.getName(), player.getUniqueId(), -1);
        if (data.isPresent()) return false;
        plugin.getStorageInst().save(new PlayerWhitelisted(player.getName(), player.getUniqueId()));
        return true;
    }
    public void toggleJoinUser(PlayerWhitelisted data, boolean canJoin) {
        data.setWhitelisted(canJoin);
        plugin.getStorageInst().save(data);
    }
    public void linkUser(@NotNull PlayerWhitelisted data, long id) {
        data.setDiscordID(id);
        plugin.getStorageInst().save(data);
    }
    public boolean deleteUser(PlayerWhitelisted data) {
        if (data == null) return false;
        plugin.getStorageInst().delete(data);
        return true;
    }
    public boolean deleteUser(Player player) {
        if (player == null) return false;
        return deleteUser(player.getName(), player.getUniqueId());
    }
    public boolean deleteUser(String name, UUID uuid) {
        Optional<PlayerWhitelisted> data = getData(name, uuid, -1);
        if (data.isEmpty()) return false;
        plugin.getStorageInst().delete(data.get());
        return true;
    }
    public CheckType checkData(@NotNull PlayerWhitelisted data, @NotNull Player player) {
        String name = data.getName();
        if (name == null) return CheckType.NO_NAME;
        if (!data.isWhitelisted()) return CheckType.WHITELISTED_EXCLUDED;

        UUID uuid = data.getUUID();
        String pName = player.getName();
        if (uuid == null) {
            if (name.equals(pName)) return CheckType.NO_UUID;
            if (name.equalsIgnoreCase(pName)) return CheckType.NO_UUID_NAME_CAPS;
            return CheckType.NO_UUID;
        }
        if (ConfigFile.getConfig(ConfigFile.skipName)) return CheckType.NAME_SKIP;

        UUID pUUID = player.getUniqueId();
        if (name.equals(pName)) {
            if (!uuid.equals(pUUID)) return CheckType.NAME_DIFFERENT_UUID;
            return CheckType.NORMAL;
        }
        if (name.equalsIgnoreCase(pName)) {
            if (!uuid.equals(pUUID)) return CheckType.NAME_DIFFERENT_UUID;
            return CheckType.NAME_CAPS;
        }
        if (uuid.equals(pUUID)) return CheckType.UUID_NO_NAME;
        return CheckType.NORMAL;
    }
    public void replaceData(@NotNull PlayerWhitelisted data, @NotNull Player player) {
        String name = player.getName();
        UUID uuid = player.getUniqueId();

        switch (checkData(data, player)) {
            case UUID_NO_NAME -> {
                data.setName(name);
                plugin.getStorageInst().save(data);
                plugin.consoleMsg().sendMessage(MessageData.warningNameConsole(name));
                plugin.playerMsg(player).sendMessage(MessageData.warningNamePlayer(name));
            }
            case NO_UUID_NAME_CAPS -> {
                data.setName(name);
                data.setUuid(uuid);
                plugin.getStorageInst().save(data);
            }
            case NO_UUID -> {
                data.setUuid(uuid);
                plugin.getStorageInst().save(data);
            }
            case NO_NAME, NAME_CAPS -> {
                data.setName(name);
                plugin.getStorageInst().save(data);
            }
        }
    }
}
