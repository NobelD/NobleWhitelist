package me.nobeld.minecraft.noblewhitelist.data;

import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.*;

public class WhitelistData {
    private final NobleWhitelist plugin;
    private final ArrayList<PlayerData> whitelist = new ArrayList<>();
    private final ArrayList<SuccessData> successList = new ArrayList<>();
    public WhitelistData(NobleWhitelist plugin) {
        this.plugin = plugin;
        loadWhitelist();
    }
    public void loadWhitelist() {
        whitelist.clear();
        for (String line : plugin.whitelistFile().getSection("whitelist").singleLayerKeySet()) {
            if (line == null) return;
            if (line.startsWith("example$")) {
                plugin.whitelistFile().remove("whitelist." + line);
                return;
            }
            if (!(plugin.whitelistFile().getSection("whitelist").get(line) instanceof String)) {
                plugin.whitelistFile().remove("whitelist." + line);
                return;
            }
            String uuid = plugin.whitelistFile().getSection("whitelist").getString(line);
            new PlayerData(line, uuid).addData();
        }
    }
    public Map<String, String> getWhitelist() {
        Map<String, String> playersList = new HashMap<>();
        whitelist.forEach(data -> {
            String name = data.name();
            if (data.notHasName()) name = "none";
            playersList.put(name, data.uuid());
        });
        return sortByKey(playersList);
    }
    public boolean clearWhitelist() {
        if (whitelist.isEmpty()) return false;
        plugin.whitelistFile().clear();
        whitelist.clear();
        return true;
    }
    public WhitelistData.SuccessData registerSuccess(Player player) {
        boolean name = plugin.whitelistData().hasName(player.getName());
        boolean uuid = plugin.whitelistData().hasUUID(player.getUniqueId().toString());
        boolean perm = player.hasPermission("noblewhitelist.bypass");

        SuccessData data = new WhitelistData.SuccessData(player, name, uuid, perm);
        successList.add(data);
        return data;
    }
    public SuccessData getSuccess(Player player) {
        SuccessData success = null;
        for (SuccessData data : successList) {
            if (data.player == player) success = data;
        }
        return success;
    }
    public void removeSuccess(Player player) {
        successList.remove(getSuccess(player));
    }
    public boolean addByPlayer(Player player) {
        String name = player.getName();
        String uuid = player.getUniqueId().toString();
        if (hasData(name, uuid)) return false;
        addPlayer(name, uuid);
        return true;
    }
    public void addPlayer(String name, String uuid) {
        if (hasData(name, uuid)) return;
        createPlayerData(name, uuid);
    }
    public void createPlayerData(String name, String uuid) {
        String realName = name;
        if (name == null) realName = randomName();
        PlayerData data = new PlayerData(realName, uuid);
        data.addData();
    }
    public PlayerData getByName(String name) {
        for (PlayerData data : whitelist) {
            if (data.name().equalsIgnoreCase(name)) return data;
        }
        return null;
    }
    public PlayerData getByUUID(String uuid) {
        for (PlayerData data : whitelist) {
            if (data.uuid().equalsIgnoreCase(uuid)) return data;
        }
        return null;
    }
    public PlayerData getByPlayer(Player player) {
        PlayerData uuidData = getByUUID(player.getUniqueId().toString());
        if (uuidData != null) return uuidData;
        return getByName(player.getName());
    }
    public boolean modifyByName(String name, boolean add) {
        if (hasName(name) == add) return false;
        if (add) createPlayerData(name, "none");
        else getByName(name.toLowerCase()).removeData();
        return true;
    }
    public boolean modifyByUUID(String uuid, boolean add) {
        if (hasUUID(uuid) == add) return false;
        if (add) createPlayerData(null, uuid);
        else getByUUID(uuid.toLowerCase()).removeData();
        return true;
    }
    public boolean hasData(String name, String uuid) {
        if (getByName(name) != null) return true;
        return getByUUID(uuid) != null;
    }
    public boolean hasData(Player player) {
        return hasData(player.getName(), player.getUniqueId().toString());
    }
    public boolean hasName(String name) {
        return getByName(name) != null;
    }
    public boolean hasUUID(String uuid) {
        return getByUUID(uuid) != null;
    }
    private String randomName() {
        String name = "none$" + RandomStringUtils.randomAlphabetic(8);
        if (getByName(name) != null) return randomName();
        return name;
    }
    public enum SuccessEnum {
        ALL, NORMAL, ONLY_UUID, ONLY_NAME, BYPASS, NONE, NOT_ACTIVE
    }

    public record SuccessData(Player player, boolean name, boolean uuid, boolean perm) {
        public boolean hasAny() {
            return name || uuid || perm;
        }
        public boolean hasAll() {
            return name && uuid && perm;
        }
        public boolean isNormal() {
            return name && uuid;
        }
        public boolean isWhitelisted() {
            return name || uuid;
        }
        public boolean isBypass() {
            return perm;
        }
        public boolean onlyName() {
            return name && !(uuid || perm);
        }
        public boolean onlyUuid() {
            return uuid && !(name || perm);
        }
        public boolean onlyPerm() {
            return perm && !(name || uuid);
        }
    }
    public class PlayerData {
        private String name;
        private String uuid;
        public PlayerData(String name, String uuid) {
            this.name = name;
            this.uuid = uuid.toLowerCase();
        }
        protected void removeData() {
            whitelist.remove(this);
            plugin.fileData().remove(name);
        }
        protected void addData() {
            whitelist.add(this);
            if (plugin.fileData().get(name) != null) return;
            plugin.fileData().set(name, uuid);
        }
        protected CheckDataType checkData(Player player) {
            if (notHasName()) return CheckDataType.NO_NAME;
            if (uuid.equalsIgnoreCase("none")) {
                if (name.equals(player.getName())) return CheckDataType.NO_UUID;
                if (name.equalsIgnoreCase(player.getName())) return CheckDataType.NO_UUID_NAME_CAPS;
                return CheckDataType.NO_UUID;
            }
            if (plugin.fileData().skipName()) return CheckDataType.NAME_SKIP;
            if (name.equals(player.getName())) {
                if (!uuid.equalsIgnoreCase(player.getUniqueId().toString())) return CheckDataType.NAME_DIFFERENT_UUID;
                return CheckDataType.NORMAL;
            }
            if (name.equalsIgnoreCase(player.getName())) {
                if (!uuid.equalsIgnoreCase(player.getUniqueId().toString())) return CheckDataType.NAME_DIFFERENT_UUID;
                return CheckDataType.NAME_CAPS;
            }
            if (uuid.equalsIgnoreCase(player.getUniqueId().toString())) return CheckDataType.UUID_NO_NAME;
            return CheckDataType.NORMAL;
        }
        protected void replaceData(Player player) {
            CheckDataType type = checkData(player);
            String name2 = player.getName();
            String uuid2 = player.getUniqueId().toString();
            if (type == CheckDataType.UUID_NO_NAME) {
                changeName(name2);
                plugin.consoleMsg().sendMessage(plugin.messages().warningNameConsole(player.getName()));
                plugin.playerMsg(player).sendMessage(plugin.messages().warningNamePlayer(player.getName()));
            }
            if (type == CheckDataType.NO_UUID_NAME_CAPS) changeIDAndName(name2, uuid2);
            if (type == CheckDataType.NO_UUID) replaceUUID(uuid2);
            if (type == CheckDataType.NO_NAME) changeName(name2);
            if (type == CheckDataType.NAME_CAPS) changeName(name2);
        }
        protected void changeIDAndName(String name2, String uuid2) {
            plugin.fileData().remove(name);
            plugin.fileData().set(name2, uuid2);
            name = name2;
            uuid = uuid2;
        }
        protected void changeName(String name2) {
            plugin.fileData().remove(name);
            plugin.fileData().set(name2, uuid);
            name = name2;
        }
        protected void replaceUUID(String uuid2) {
            uuid = uuid2.toLowerCase();
            plugin.fileData().set(name, uuid);
        }
        private boolean notHasName() {
            return name.startsWith("none$");
        }
        private String name() {
            return name;
        }
        private String uuid() {
            return uuid;
        }
        public enum CheckDataType {
            NORMAL, NO_UUID, NAME_CAPS, NO_NAME, NAME_SKIP, NO_UUID_NAME_CAPS, UUID_NO_NAME, NAME_DIFFERENT_UUID
        }
    }
}
